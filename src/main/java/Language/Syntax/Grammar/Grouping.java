package Language.Syntax.Grammar;

import Language.Syntax.Visitor;

public class Grouping extends Expression{
    public final Expression expression;

    public Grouping(Expression expression) {
        this.expression = expression;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitGroupingExpression(this);
    }
}
