package Language.Syntax.AST.Grammar.Expressions;

import Language.Lexicon.Token;
import Language.Syntax.ExpressionVisitor;

public class TernaryExpression extends Expression {
    public final Token token;
    public final Expression condition;
    public final Expression thenBranch;
    public final Expression elseBranch;

    public TernaryExpression(Expression condition, Expression thenBranch, Expression elseBranch, Token token) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
        this.token = token;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> expressionVisitor) {
        return expressionVisitor.visitTernaryExpression(this);
    }
}

