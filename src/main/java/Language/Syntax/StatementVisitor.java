package Language.Syntax;

import Language.Syntax.AST.Grammar.Statements.*;

public interface StatementVisitor<R> {
    R visitExpressionStatement(ExpressionStatement stmt);
    R visitPrintStatement(Print stmt);
    R visitVarStatement(VarDecl stmt);
    R visitBlockStatement(Block stmt);
    R visitIfStatement(If stmt);
}
