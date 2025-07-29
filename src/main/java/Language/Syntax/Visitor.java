package Language.Syntax;

import Language.Syntax.Grammar.*;

public interface Visitor<R> {
    public R visitBinaryExpression(BinaryExpression binaryExpression);
    public R visitTernaryExpression(TernaryExpression ternaryExpression);
    public R visitUnaryExpression(UnaryExpression unaryExpression);
    public R visitGroupingExpression(Grouping grouping);
    public R visitLiteralExpression(Literal literal);
}