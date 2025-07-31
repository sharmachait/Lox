package Language.Syntax.AST.Grammar.Expressions;

import Language.Syntax.ExpressionVisitor;

public abstract class Expression {
    public abstract <R> R accept(ExpressionVisitor<R> expressionVisitor);
}
