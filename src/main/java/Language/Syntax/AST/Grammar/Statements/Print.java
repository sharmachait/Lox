package Language.Syntax.AST.Grammar.Statements;

import Language.Syntax.AST.Grammar.Expressions.Expression;
import Language.Syntax.StatementVisitor;

public class Print extends Statement {
    public final Expression expression;
    public Print(Expression expression) {
        this.expression = expression;
    }
    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
//        System.out.println("============PrintStatement============");
//        System.out.println(new AstPrinter().print(this.expression));
//        System.out.println("============PrintStatement============");
        return visitor.visitPrintStatement(this);
    }
}
