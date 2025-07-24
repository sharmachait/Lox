package Language.Syntax.Parsing;

import Language.Syntax.Grammar.*;

public class AstPrinter implements Visitor<String>{
    String print(Expression expr){
        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpression(BinaryExpression binaryExpression) {
        String lexeme = binaryExpression.operator.lexeme;
        Expression left = binaryExpression.left;
        Expression right = binaryExpression.right;
        return parenthesize(lexeme, left, right);
    }

    private String parenthesize(String lexeme, Expression left, Expression right) {
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

    private String parenthesize(String group, Expression expression) {
    }

    @Override
    public String visitLiteralExpression(Literal literal) {
        if(literal.value == null) return "nil";
        return literal.value.toString();
    }
}
