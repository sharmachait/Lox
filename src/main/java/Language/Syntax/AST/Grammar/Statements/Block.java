package Language.Syntax.AST.Grammar.Statements;

import Language.Syntax.StatementVisitor;

import java.util.List;

public class Block extends Statement{
    public final List<Statement> statements;

    public Block(List<Statement> statements) {
        this.statements = statements;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visitBlockStatement(this);
    }
}
