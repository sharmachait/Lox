package Language.Syntax.AST.Grammar.Statements;

import Language.Lexicon.Token;
import Language.Syntax.StatementVisitor;

import java.util.List;

public class Function extends Statement{
    public final Token name;
    public final List<Token> params;
    public final Block body;

    public Function(Token name, List<Token> params, List<Statement> stmts) {
        this.name = name;
        this.params = params;
        this.body = new Block(stmts);
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visitFunctionStatement(this);
    }
}
