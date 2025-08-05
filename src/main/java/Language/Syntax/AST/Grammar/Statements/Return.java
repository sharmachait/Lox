package Language.Syntax.AST.Grammar.Statements;

import Language.Lexicon.Token;
import Language.Syntax.AST.Grammar.Expressions.Expression;
import Language.Syntax.StatementVisitor;

public class Return extends Statement{
    public final Token keyword;
    public final Expression expression;

    public Return(Token keyword, Expression expression) {
        this.keyword = keyword;
        this.expression = expression;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visitReturnStatement(this);
    }
}
