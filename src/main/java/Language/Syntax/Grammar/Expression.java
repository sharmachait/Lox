package Language.Syntax.Grammar;

import Language.Syntax.Parsing.Visitor;

public abstract class Expression {
    public abstract <R> R accept(Visitor<R> visitor);
}
