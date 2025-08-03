package Language.Syntax;

import Language.Syntax.AST.Grammar.Expressions.*;

public interface ExpressionVisitor<R> {
     R visitBinaryExpression(BinaryExpression binaryExpression);
     R visitTernaryExpression(TernaryExpression ternaryExpression);
     R visitUnaryExpression(UnaryExpression unaryExpression);
     R visitGroupingExpression(Grouping grouping);
     R visitLiteralExpression(Literal literal);
     R visitVariableExpression(Variable variable);
     R visitAssignmentExpression(Assignment assign);
    R visitLogicalExpression(Logical logical);
}