package Language.Syntax.Grammar;

import Language.Syntax.Parsing.Visitor;

public class Literal extends Expression {
    public final Object value;

    public Literal(Object value) {
        this.value = value;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitLiteralExpression(this);
    }
}
