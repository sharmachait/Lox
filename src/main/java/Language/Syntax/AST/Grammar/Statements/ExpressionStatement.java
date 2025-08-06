package Language.Syntax.AST.Grammar.Statements;

import Language.Syntax.AST.Grammar.Expressions.Expression;
import Language.Syntax.StatementVisitor;

public class ExpressionStatement extends Statement {
    public final Expression expression;
    public ExpressionStatement(Expression expression) {
        this.expression = expression;
    }
    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
//        System.out.println("============ExpressionStatement============");
//        System.out.println(new AstPrinter().print(this.expression));
//        System.out.println("============ExpressionStatement============");
        return visitor.visitExpressionStatement(this);
    }
}
