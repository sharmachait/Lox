package Language.Syntax;

import Language.Syntax.AST.Grammar.Statements.ExpressionStatement;
import Language.Syntax.AST.Grammar.Statements.Print;
import Language.Syntax.AST.Grammar.Statements.VarDecl;

public interface StatementVisitor<R> {
    R visitExpressionStatement(ExpressionStatement stmt);
    R visitPrintStatement(Print stmt);
    R visitVarStmt(VarDecl stmt);
}
