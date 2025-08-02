package Language.Syntax.AST.Grammar.Statements;

import Language.Lexicon.Token;
import Language.Syntax.AST.Grammar.Expressions.Expression;
import Language.Syntax.StatementVisitor;

public class VarDecl extends Statement {
    public final Token name;
    public final Expression initializer;

    public VarDecl(Token name, Expression initializer) {
        this.name = name;
        this.initializer = initializer;
    }

    public VarDecl(Token name) {
        this.name = name;
        this.initializer = null;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visitVarStatement(this);
    }
}
