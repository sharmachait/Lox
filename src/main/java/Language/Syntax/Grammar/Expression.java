package Language.Syntax.Grammar;

import Language.Syntax.Visitor;

public abstract class Expression {
    public abstract <R> R accept(Visitor<R> visitor);
}
