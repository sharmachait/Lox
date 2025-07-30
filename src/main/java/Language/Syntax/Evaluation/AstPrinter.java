package Language.Syntax.Evaluation;

import Language.Syntax.Grammar.*;
import Language.Syntax.Visitor;

public class AstPrinter implements Visitor<String> {
    public String print(Expression expr){
        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpression(BinaryExpression binaryExpression) {
        String lexeme = binaryExpression.operator.lexeme;
        Expression left = binaryExpression.left;
        Expression right = binaryExpression.right;
        return parenthesize(lexeme, left, right);
    }

    @Override
    public String visitTernaryExpression(TernaryExpression ternaryExpression) {

        String trueBranch = ternaryExpression.thenBranch.accept(this);
        String falseBranch = ternaryExpression.elseBranch.accept(this);
        String condition = ternaryExpression.condition.accept(this);

        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append("(");
        sb.append(condition);
        sb.append(") ? ");
        sb.append(trueBranch);
        sb.append(" : ");
        sb.append(falseBranch);
        sb.append(")");

        return sb.toString();
    }

    @Override
    public String visitUnaryExpression(UnaryExpression unaryExpression) {
        String lexeme = unaryExpression.operator.lexeme;
        Expression right = unaryExpression.right;
        return parenthesize(lexeme, right);
    }

    @Override
    public String visitGroupingExpression(Grouping grouping) {
        return parenthesize("group", grouping.expression);
    }

    private String parenthesize(String name, Expression... expressions) {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(name);
        for(Expression expression : expressions){
            sb.append(" ");
            sb.append(expression.accept(this));
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public String visitLiteralExpression(Literal literal) {
        if(literal.value == null) return "nil";
        return literal.value.toString();
    }
}
