package Language.Syntax.AST.Grammar.Expressions;

import Language.Syntax.ExpressionVisitor;

public class Grouping extends Expression{
    public final Expression expression;

    public Grouping(Expression expression) {
        this.expression = expression;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> expressionVisitor) {
        return expressionVisitor.visitGroupingExpression(this);
    }
}
