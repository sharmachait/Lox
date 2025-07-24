package Language.Syntax.Parsing;

import Language.Syntax.Grammar.BinaryExpression;
import Language.Syntax.Grammar.Grouping;
import Language.Syntax.Grammar.Literal;
import Language.Syntax.Grammar.UnaryExpression;

public interface Visitor<R> {
    public R visitBinaryExpression(BinaryExpression binaryExpression);
    public R visitUnaryExpression(UnaryExpression unaryExpression);
    public R visitGroupingExpression(Grouping grouping);
    public R visitLiteralExpression(Literal literal);
}