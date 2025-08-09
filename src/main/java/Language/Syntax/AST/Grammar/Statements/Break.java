package Language.Syntax.AST.Grammar.Statements;

import Language.Lexicon.Token;
import Language.Syntax.StatementVisitor;

public class Break extends Statement{
    public final Token token;

    public Break(Token token) {
        this.token = token;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visitBreakStatement(this);
    }
}
