package Language.Syntax.Analysis;

import Language.Lexicon.Token;
import Language.Syntax.AST.Grammar.Expressions.*;
import Language.Syntax.AST.Grammar.Statements.*;
import Language.Syntax.ExpressionVisitor;
import Language.Syntax.Interpreter;
import Language.Syntax.StatementVisitor;
import Runner.Runner;

import java.util.*;

public class Resolver implements ExpressionVisitor<Void>, StatementVisitor<Void>{
    private class Scope{
        public Map<String, Boolean> resolution = new HashMap<>();
        public Set<Token> isUsed = new HashSet<>();
    }
    private final Interpreter interpreter;
    private final Stack<Scope> scopes = new Stack<>();
    private FunctionType currentFunction = FunctionType.NONE;
    private LoopType currentLoop = LoopType.NONE;
    public Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    @Override
    public Void visitBinaryExpression(BinaryExpression binaryExpression) {
        binaryExpression.left.accept(this);
        binaryExpression.right.accept(this);
        return null;
    }

    @Override
    public Void visitTernaryExpression(TernaryExpression ternaryExpression) {
        ternaryExpression.condition.accept(this);
        ternaryExpression.thenBranch.accept(this);
        ternaryExpression.elseBranch.accept(this);
        return null;
    }

    @Override
    public Void visitUnaryExpression(UnaryExpression unaryExpression) {
        unaryExpression.right.accept(this);
        return null;
    }

    @Override
    public Void visitGroupingExpression(Grouping grouping) {
        grouping.expression.accept(this);
        return null;
    }

    @Override
    public Void visitLiteralExpression(Literal literal) {
        return null;
    }

    @Override
    public Void visitVariableExpression(Variable variable) {
        // if the initializer for a variable declaration references a variable,
        // the initializers resolution will bring the control here

        if(!scopes.isEmpty()){
            Map<String, Boolean> scope = scopes.peek().resolution;//.getOrDefault(variable.name.lexeme, false);
            if(scope.containsKey(variable.name.lexeme) && scope.get(variable.name.lexeme) == false){
                Runner.parserError(variable.name, "Can not read local variable before its own initialization.");
            }
        }
        Set<Token> isUsed = scopes.peek().isUsed;
        isUsed.remove(variable.name);
        markResolutionScope(variable, variable.name);
        return null;
    }

    private void markResolutionScope(Expression expr, Token name) {
        for(int i = scopes.size()-1; i >= 0; i--){
            if(scopes.get(i).resolution.containsKey(name.lexeme)) {
                int tokenCurrScopeDistResolutionScope = scopes.size()-1-i;
                // tells us how deep need to go to resolve this variable in the environment chain
                // since we add to scopes when a new scope is created the global scope and the global variables
                // are not resolved
                interpreter.markResolutionScope(expr, tokenCurrScopeDistResolutionScope);
                return;
            }
        }
    }

    @Override
    public Void visitAssignmentExpression(Assignment assign) {
        assign.value.accept(this);
        markResolutionScope(assign, assign.name);
        return null;
    }

    @Override
    public Void visitLogicalExpression(Logical logical) {
        logical.left.accept(this);
        logical.right.accept(this);
        return null;
    }

    @Override
    public Void visitCallExpression(Call call) {
        call.callee.accept(this);
        for(Expression arg: call.arguments){
            arg.accept(this);
        }
        return null;
    }

    @Override
    public Void visitExpressionStatement(ExpressionStatement stmt) {
        stmt.expression.accept(this);
        return null;
    }

    @Override
    public Void visitPrintStatement(Print stmt) {
        stmt.expression.accept(this);
        return null;
    }

    @Override
    public Void visitVarStatement(VarDecl stmt) {
        declare(stmt.name);
        if(stmt.initializer != null) // simulating variable initialization
            stmt.initializer.accept(this);
        define(stmt.name);
//        split binding into two steps, declaring then defining, in order to handle funny edge cases
//        var a = "outer";
//        {
//            var a = a;
//        }
        // handle by making it an error to reference the same variable being declared in the initializer as well
        return null;
    }

    private void declare(Token name) {
        if(scopes.isEmpty()) return;
        boolean isResolved = false;
        Map<String,Boolean> scope = scopes.peek().resolution;
        Set<Token> isUsed = scopes.peek().isUsed;// declare in the deepest scope so far
        // declaring variables with the same name in local scopes an error
        if(scope.containsKey(name.lexeme)){
            Runner.parserError(name,  "Already a variable with this name in this scope.");
        }
        scope.put(name.lexeme, isResolved);
        isUsed.add(name);
    }

    private void define(Token name) {
        if(scopes.isEmpty()) return;
        boolean isResolved = true;
        Map<String,Boolean> scope = scopes.peek().resolution; //variable is resolved after the initializer is resolved
        scope.put(name.lexeme, isResolved);
    }

    @Override
    public Void visitBlockStatement(Block stmt) {
        beginScope();
        resolve(stmt.statements);
        endScope();
        return null;
    }

    private void beginScope() {
        scopes.push(new Scope());
    }

    private void endScope() {
        Scope scope = scopes.pop();
        Set<Token> isUsed = scope.isUsed;
        for(Token key: isUsed){
            System.out.println("[ line: "+key.line+" ]"+" Warning: variable "+key.lexeme+" defined but never used.");
        }
    }

    public void resolve(List<Statement> statements) {
        for (Statement statement : statements) {
            statement.accept(this);
        }
    }

    @Override
    public Void visitIfStatement(If stmt) {
        stmt.condition.accept(this);
        stmt.thenBranch.accept(this);
        if(stmt.elseBranch!=null) stmt.elseBranch.accept(this);
        return null;
    }

    @Override
    public Void visitWhileStatement(While stmt) {
        LoopType prev = currentLoop;
        currentLoop = LoopType.LOOP;
        stmt.condition.accept(this);
        stmt.body.accept(this);
        currentLoop = prev;
        return null;
    }

    @Override
    public Void visitBreakStatement(Break stmt) {
        if(currentLoop == LoopType.NONE) Runner.parserError(stmt.token, "Can't break from top-level code.");
        return null;
    }

    @Override
    public Void visitContinueStatement(Continue stmt) {
        if(currentLoop == LoopType.NONE) Runner.parserError(stmt.token, "Can't break from top-level code.");
        return null;
    }

    @Override
    public Void visitFunctionStatement(Function function) {
        declare(function.name);
        define(function.name);
        resolveFunctionBody(function, FunctionType.FUNCTION);
        return null;
    }

    private void resolveFunctionBody(Function function, FunctionType functionType) {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = functionType;
        beginScope();
        for(Token param: function.params){
            declare(param);
            define(param);
        }
        resolve(function.body);
        endScope();
        currentFunction = enclosingFunction;
    }

    @Override
    public Void visitReturnStatement(Return stmt) {
        if (currentFunction == FunctionType.NONE) {
            Runner.parserError(stmt.keyword, "Can't return from top-level code.");
        }
        if(stmt.expression!=null)
            stmt.expression.accept(this);
        return null;
    }
    private enum FunctionType {
        NONE,
        FUNCTION
    }
    private enum LoopType {
        NONE,
        LOOP
    }
}
