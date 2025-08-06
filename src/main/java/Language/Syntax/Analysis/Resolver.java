package Language.Syntax.Analysis;

import Language.Syntax.AST.Grammar.Expressions.*;
import Language.Syntax.AST.Grammar.Statements.*;
import Language.Syntax.ExpressionVisitor;
import Language.Syntax.Interpreter;
import Language.Syntax.StatementVisitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Resolver implements ExpressionVisitor<Void>, StatementVisitor<Void>{

    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();

    public Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    @Override
    public Void visitBinaryExpression(BinaryExpression binaryExpression) {
        return null;
    }

    @Override
    public Void visitTernaryExpression(TernaryExpression ternaryExpression) {
        return null;
    }

    @Override
    public Void visitUnaryExpression(UnaryExpression unaryExpression) {
        return null;
    }

    @Override
    public Void visitGroupingExpression(Grouping grouping) {
        return null;
    }

    @Override
    public Void visitLiteralExpression(Literal literal) {
        return null;
    }

    @Override
    public Void visitVariableExpression(Variable variable) {
        return null;
    }

    @Override
    public Void visitAssignmentExpression(Assignment assign) {
        return null;
    }

    @Override
    public Void visitLogicalExpression(Logical logical) {
        return null;
    }

    @Override
    public Void visitCallExpression(Call call) {
        return null;
    }

    @Override
    public Void visitExpressionStatement(ExpressionStatement stmt) {
        return null;
    }

    @Override
    public Void visitPrintStatement(Print stmt) {
        return null;
    }

    @Override
    public Void visitVarStatement(VarDecl stmt) {
        declare(stmt.name);
        if(stmt.initializer != null) stmt.initializer.accept(this);
        define(stmt.name);
//        split binding into two steps, declaring then defining, in order to handle funny edge cases
//        var a = "outer";
//        {
//            var a = a;
//        }
        return null;
    }

    @Override
    public Void visitBlockStatement(Block stmt) {
        beginScope();
        resolve(stmt.statements);
        endScope();
        return null;
    }

    private void beginScope() {
        scopes.push(new HashMap<String, Boolean>());
    }

    private void endScope() {
        scopes.pop();
    }

    private void resolve(List<Statement> statements) {
        for (Statement statement : statements) {
            statement.accept(this);
        }
    }

    @Override
    public Void visitIfStatement(If stmt) {
        return null;
    }

    @Override
    public Void visitWhileStatement(While stmt) {
        return null;
    }

    @Override
    public Void visitBreakStatement(Break stmt) {
        return null;
    }

    @Override
    public Void visitContinueStatement(Continue stmt) {
        return null;
    }

    @Override
    public Void visitFunctionStatement(Function function) {
        return null;
    }

    @Override
    public Void visitReturnStatement(Return aReturn) {
        return null;
    }
}
