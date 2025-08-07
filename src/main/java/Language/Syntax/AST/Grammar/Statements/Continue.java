package Language.Syntax.AST.Grammar.Statements;

import Language.Syntax.StatementVisitor;

public class Continue extends Statement{
    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return null;
    }
}
