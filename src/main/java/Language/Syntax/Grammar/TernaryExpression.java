package Language.Syntax.Grammar;

import Language.Syntax.Visitor;

public class TernaryExpression extends Expression {
    public final Expression condition;
    public final Expression thenBranch;
    public final Expression elseBranch;

    public TernaryExpression(Expression condition, Expression thenBranch, Expression elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitTernaryExpression(this);
    }
}

