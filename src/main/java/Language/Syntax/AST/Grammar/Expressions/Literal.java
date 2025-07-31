package Language.Syntax.AST.Grammar.Expressions;

import Language.Syntax.ExpressionVisitor;

public class Literal extends Expression {
    public final Object value;

    public Literal(Object value) {
        this.value = value;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> expressionVisitor) {
        return expressionVisitor.visitLiteralExpression(this);
    }
}
