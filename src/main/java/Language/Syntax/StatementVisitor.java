package Language.Syntax;
import Error.BreakException;
import Error.ContinueException;
import Language.Syntax.AST.Grammar.Statements.*;

public interface StatementVisitor<R> {
    R visitExpressionStatement(ExpressionStatement stmt);
    R visitPrintStatement(Print stmt);
    R visitVarStatement(VarDecl stmt);
    R visitBlockStatement(Block stmt);
    R visitIfStatement(If stmt);
    R visitWhileStatement(While stmt);
    R visitBreakStatement(Break stmt);
    R visitContinueStatement(Continue stmt);
    R visitFunctionStatement(Function function);
    R visitReturnStatement(Return aReturn);
}
