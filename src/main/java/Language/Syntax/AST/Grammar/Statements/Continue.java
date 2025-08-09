package Language.Syntax.AST.Grammar.Statements;

import Language.Lexicon.Token;
import Language.Syntax.StatementVisitor;

public class Continue extends Statement{
    public final Token token;

    public Continue(Token token) {
        this.token = token;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visitContinueStatement(this);
    }
}
