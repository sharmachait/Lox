package Language.Syntax.AST.Grammar.Statements;

import Language.Syntax.AST.Grammar.Expressions.Expression;
import Language.Syntax.StatementVisitor;

public class If extends Statement{
    public final Expression condition;
    public final Statement thenBranch;
    public final Statement elseBranch;

    public If(Expression condition, Statement thenBranch, Statement elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visitIfStatement(this);
    }
}
