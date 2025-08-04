package Language.Syntax.AST.Grammar.Expressions;

import Language.Lexicon.Token;
import Language.Syntax.ExpressionVisitor;

import java.util.List;

public class Call extends Expression{
    public final Expression callee;
    public final Token paren;
    public final List<Expression> arguments;

    public Call(Expression callee
            , Token paren
            , List<Expression> arguments) {
        this.callee = callee;
        this.paren = paren;
        this.arguments = arguments;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> expressionVisitor) {
        return expressionVisitor.visitCallExpression(this);
    }
}
