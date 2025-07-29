package Language.Syntax.Grammar;

import Language.Lexicon.Token;
import Language.Syntax.Visitor;

public class BinaryExpression extends Expression {
    public final Expression left, right;
    public final Token operator;
    public BinaryExpression(Expression left, Token operator, Expression right) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitBinaryExpression(this);
    }

}
