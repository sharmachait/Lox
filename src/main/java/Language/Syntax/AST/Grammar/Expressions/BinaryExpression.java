package Language.Syntax.AST.Grammar.Expressions;

import Language.Lexicon.Token;
import Language.Syntax.ExpressionVisitor;

public class BinaryExpression extends Expression {
    public final Expression left, right;
    public final Token operator;
    public BinaryExpression(Expression left, Token operator, Expression right) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> expressionVisitor) {
        return expressionVisitor.visitBinaryExpression(this);
    }

}
