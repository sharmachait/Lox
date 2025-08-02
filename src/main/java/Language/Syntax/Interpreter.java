package Language.Syntax;

import Error.InterpreterException;
import Language.Syntax.AST.Grammar.Expressions.*;
import Language.Syntax.AST.Grammar.Statements.ExpressionStatement;
import Language.Syntax.AST.Grammar.Statements.Print;
import Language.Syntax.AST.Grammar.Statements.Statement;
import Language.Syntax.AST.Grammar.Statements.VarDecl;
import Runner.Runner;

import java.util.List;

//Unlike expressions, statements produce no values, so the return type of the visit methods is Void
public class Interpreter implements ExpressionVisitor<Object>, StatementVisitor<Void> {
    private Environment env = Environment.getInstance();
    public void interpret(List<Statement> statements) {
        try{
            for(Statement statement : statements){
                statement.accept(this);
            }
        }catch (InterpreterException e){
            Runner.runtimeException(e);
        }
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
    public Void visitExpressionStatement(ExpressionStatement stmt) {
         stmt.expression.accept(this);
         return null;
    }

    @Override
    public Void visitPrintStatement(Print stmt) {
        Object value = stmt.expression.accept(this);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitVarStmt(VarDecl stmt) {
        Object value = null;
        if(stmt.initializer!=null){
            value = stmt.initializer.accept(this);
        }
        env.define(stmt.name.lexeme, value);
        return null;
    }

    @Override
    public Object visitVariableExpression(Variable variable){
        return env.get(variable.name);
    }

    @Override
    public Object visitAssignmentExpression(Assignment assign) {
        Object value = assign.value.accept(this);
        return env.assign(assign.name, value);
    }
}
