package Language.Syntax.AST.Grammar.Statements;

import Language.Syntax.StatementVisitor;

public class Break extends Statement{
    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return null;
    }
}
