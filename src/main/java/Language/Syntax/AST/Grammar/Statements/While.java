package Language.Syntax.AST.Grammar.Statements;

import Language.Syntax.AST.Grammar.Expressions.Expression;
import Language.Syntax.StatementVisitor;

public class While extends Statement {
    public final Expression condition;
    public final Statement body;

    public While(Expression condition, Statement body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visitWhileStatement(this);
    }
}
