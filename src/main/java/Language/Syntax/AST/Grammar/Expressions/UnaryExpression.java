package Language.Syntax.AST.Grammar.Expressions;

import Language.Lexicon.Token;
import Language.Syntax.ExpressionVisitor;

public class UnaryExpression extends Expression{
    public final Token operator;
    public final Expression right;

    public UnaryExpression(Token operator, Expression right) {
        this.operator = operator;
        this.right = right;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> expressionVisitor) {
        return expressionVisitor.visitUnaryExpression(this);
    }
}
