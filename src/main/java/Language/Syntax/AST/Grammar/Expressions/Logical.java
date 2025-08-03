package Language.Syntax.AST.Grammar.Expressions;

import Language.Lexicon.Token;
import Language.Syntax.ExpressionVisitor;

public class Logical extends Expression{
    public final Expression left;
    public final Token operator;
    public final Expression right;

    public Logical(Expression left, Token operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> expressionVisitor) {
        return expressionVisitor.visitLogicalExpression(this);
    }
}
