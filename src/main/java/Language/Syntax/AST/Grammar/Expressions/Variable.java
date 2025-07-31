package Language.Syntax.AST.Grammar.Expressions;

import Language.Lexicon.Token;
import Language.Syntax.ExpressionVisitor;

public class Variable extends Expression {
    public final Token name;

    public Variable(Token name) {
        this.name = name;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> expressionVisitor) {
        return expressionVisitor.visitVariableExpression(this);
    }
}
