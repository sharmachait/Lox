package Language.Syntax.Grammar;

import Language.Lexicon.Token;
import Language.Syntax.Visitor;

public class UnaryExpression extends Expression{
    public final Token operator;
    public final Expression right;

    public UnaryExpression(Token operator, Expression right) {
        this.operator = operator;
        this.right = right;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitUnaryExpression(this);
    }
}
