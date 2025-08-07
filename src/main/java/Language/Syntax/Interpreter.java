package Language.Syntax;


import Language.Lexicon.Token;
import Language.Lexicon.TokenType;
import Language.Syntax.AST.Grammar.Expressions.*;
import Language.Syntax.AST.Grammar.Statements.*;
import Language.Syntax.Functions.Clock;
import Language.Syntax.Functions.LoxCallable;
import Language.Syntax.Functions.LoxFunction;
import Language.Syntax.Functions.ReadLine;
import Runner.Runner;
import Error.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Unlike expressions, statements produce no values, so the return type of the visit methods is Void
public class Interpreter implements ExpressionVisitor<Object>, StatementVisitor<Object> {
    final Environment global = new Environment(); // never changing
    private Environment env = global; // can change as we enter different blocks of scope
    private final Map<Expression, Integer> resolutionScopeDepth = new HashMap<>();
    public Environment getGlobal() {
        return global;
    }

    public Interpreter() {
        global.define("clock", new Clock(), true);
        global.define("readLine", new ReadLine(), true);
    }

    public List<Object> interpret(List<Statement> statements) {
        List<Object> res = new ArrayList<>();
        try{
            for(Statement statement : statements){
                res.add(statement.accept(this));
            }
        }catch (InterpreterException e){
            Runner.runtimeException(e);
        }
        return res;
    }

    private String stringify(Object result) {
        if(result == null) return "nil";
        if(result instanceof Double) {
            String text = result.toString();
            if(text.endsWith(".0")){
                text = text.substring(0, text.length()-2);
            }
            return text;
        }
        return result.toString();
    }

    @Override
    public Object visitBinaryExpression(BinaryExpression binaryExpression) throws InterpreterException {

        try{
            Object left = binaryExpression.left.accept(this);
            Object right = binaryExpression.right.accept(this);
            switch (binaryExpression.operator.type){
                case GREATER:
                    return (double)left > (double)right;
                case GREATER_EQUAL:
                    return (double)left >= (double)right;
                case LESS:
                    return (double)left < (double)right;
                case LESS_EQUAL:
                    return (double)left <= (double)right;
                case BANG_EQUAL:
                    return !isEqual(left, right);
                case EQUAL_EQUAL:
                    return isEqual(left, right);
                case MINUS:
                    return (double)left - (double)right;
                case SLASH:
                    return (double)left / (double)right;
                case STAR:
                    return (double)left * (double)right;
                case PLUS:
                    if(left instanceof String || right instanceof String){
                        return ""+left+right;
                    }
                    return (double)left + (double)right;
            }
        }catch(ClassCastException e){
            throw new InterpreterException("Invalid binary expression", e, binaryExpression.operator);
        }
        throw new InterpreterException("Invalid binary expression", binaryExpression.operator);
    }

    private boolean isEqual(Object left, Object right) {

        // if we did implicit type casting in lox like in javascript, this is where we would handle that

        if(left == null && right == null) return true;
        if(left == null || right == null) return false;
        return left.equals(right);
    }

    @Override
    public Object visitTernaryExpression(TernaryExpression ternaryExpression) throws InterpreterException {
        try{
            Object condition = ternaryExpression.condition.accept(this);
            Object trueBranch = ternaryExpression.thenBranch.accept(this);
            Object falseBranch = ternaryExpression.elseBranch.accept(this);
            if(isTruthy(condition)){
                return trueBranch;
            }
            return falseBranch;
        } catch (RuntimeException e) {
            throw new InterpreterException("Invalid ternary expression", e, ternaryExpression.token);
        }
    }

    @Override
    public Object visitUnaryExpression(UnaryExpression unaryExpression) throws InterpreterException{

        try {
            Object right = unaryExpression.right.accept(this);
            switch (unaryExpression.operator.type) {
                case MINUS:
                    return -(double) right;
                case BANG:
                    return !isTruthy(right);
//                default:
//                    throw new RuntimeException("Unexpected unary operator: " + unaryExpression.operator.type);
            }
        } catch (ClassCastException e) {
            throw new InterpreterException("Invalid unary expression", e, unaryExpression.operator);
        }
        // if control ever comes here our parser / scanner are not working as expected
        // perhaps we should throw an exception here
        throw new InterpreterException("Invalid unary expression",  unaryExpression.operator);
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean)object;
        return true;
    }

    @Override
    public Object visitGroupingExpression(Grouping grouping) throws InterpreterException {
        // the only reason we created a group expression is to manage precedence
        return grouping.expression.accept(this);
    }

    @Override
    public Object visitLiteralExpression(Literal literal){
        /*
        * We eagerly produced the runtime value way back during scanning and stuffed it in the token.
        * The parser took that value and stuck it in the literal tree node,
        * so to evaluate a literal, we simply pull it back out.
        * */
        return literal.value;
    }

    @Override
    public Object visitExpressionStatement(ExpressionStatement stmt) {
         return stmt.expression.accept(this);
    }

    @Override
    public Object visitPrintStatement(Print stmt) {
        Object value = stmt.expression.accept(this);
        System.out.println(stringify(value));
        return value;
    }

    @Override
    public Object visitVarStatement(VarDecl stmt) {
        Object value = null;
        if(stmt.initializer!=null){
            value = stmt.initializer.accept(this);
            env.define(stmt.name.lexeme, value, true);
        }else{
            env.define(stmt.name.lexeme, value, false);
        }


        return value;
    }

    @Override
    public Object visitBlockStatement(Block block) {
        Environment scopedEnv = new Environment(this.env);
        return executeBlock(block.statements, scopedEnv);
    }

    public Object executeBlock(List<Statement> block, Environment scopedEnv) {
        Environment previous = this.env;
        Object res = null;
        try{
            this.env = scopedEnv; // shadowed
            for(Statement stmt: block ){
                res = stmt.accept(this);
            }
        }finally{
            this.env = previous;
        }
        return res;
    }

    @Override
    public Object visitIfStatement(If stmt) {
        Object res=null;
        Object condition = stmt.condition.accept(this);
        if(isTruthy(condition)){
            res = stmt.thenBranch.accept(this);
        }else if(stmt.elseBranch!=null){
            res = stmt.elseBranch.accept(this);
        }
        return res;
    }

    @Override
    public Object visitWhileStatement(While stmt) {
        Object res = null;
        while(isTruthy(stmt.condition.accept(this))){
            try{
                res = stmt.body.accept(this);
            } catch (ReturnException e){
                return e.val;
            }
            catch (ContinueException e){
                continue;
            }catch (BreakException e) {
                break;
            }
        }
        return res;
    }

    @Override
    public Object visitBreakStatement(Break stmt){
        throw new BreakException();
    }

    @Override
    public Object visitContinueStatement(Continue stmt){
        throw new ContinueException();
    }

    @Override
    public Object visitFunctionStatement(Function functionStatement) {
        LoxFunction function = new LoxFunction(functionStatement, env); // add the Env when the function is declared
        env.define(functionStatement.name.lexeme, function, true);
        return null;
    }

    @Override
    public Object visitReturnStatement(Return stmt) {
        Object val = null;
        if(stmt.expression!=null)
             val = stmt.expression.accept(this);
        throw new ReturnException(val);
    }

    @Override
    public Object visitVariableExpression(Variable variable){
        return lookUpVariableResolution(variable.name, variable);
    }

    private Object lookUpVariableResolution(Token name, Expression variable) {
        Integer distance = resolutionScopeDepth.get(variable);
        if(distance == null){
            return env.getFromDepth(distance, name.lexeme);
        }else{
            return global.get(name);
        }
    }

    @Override
    public Object visitAssignmentExpression(Assignment assign) {
        Object value = assign.value.accept(this);
        Environment.Val val = Environment.Val.of(value, true);
        return env.assign(assign.name, val);
    }

    @Override
    public Object visitLogicalExpression(Logical logical) {
        Object left = logical.left.accept(this);
        Token operator = logical.operator;
        if(operator.type == TokenType.OR){
            if(isTruthy(left)) return left;
        }else{
            if(!isTruthy(left)) return left;
        }
        return logical.right.accept(this);
    }

    @Override
    public Object visitCallExpression(Call call) {

        Object callee = call.callee.accept(this);
        if(!(callee instanceof LoxCallable)){
            throw new InterpreterException("Can only call functions and classes.", call.paren);
        }
        List<Object> arguments = new ArrayList<>();

        for(Expression arg : call.arguments){
            arguments.add(arg.accept(this));
        }

        LoxCallable function = (LoxCallable) callee;
        if(arguments.size() != function.arity()){
            throw new InterpreterException(
                    "Expected " + function.arity() + " arguments but got " + arguments.size() + ".",
                    call.paren);
        }
        return function.call(this, arguments);
    }

    public void markResolutionScope(Expression expr, int depth) {
        // typically data like this is stored in the expression node it self
        // the benefit of storing it in a hashmap is that its easy to look up, update and discard
        // which is very common place for modern IDEs to do as they reparse and reparse the code again and again
        // after each key stroke
        // and since each expression is java object with a unique hash, there is no confusion if multiple expressions
        // resolve same variables
        resolutionScopeDepth.put(expr, depth);
    }
}
