package Language.Syntax.AST.Grammar.Statements;

import Language.Syntax.StatementVisitor;

public abstract class Statement {
    public abstract <R> R accept(StatementVisitor<R> visitor);
}
