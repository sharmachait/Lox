package Language.Syntax.Functions;

import Error.ReturnException;
import Language.Syntax.AST.Grammar.Statements.Function;
import Language.Syntax.Environment;
import Language.Syntax.Interpreter;

import java.util.List;


public class LoxFunction implements LoxCallable {
    private final Function function;

    private final Environment closure;

    public LoxFunction(Function function, Environment closure) {
        this.function = function;
        this.closure = closure;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(this.closure);
        for(int i = 0; i < function.params.size(); i++){
            String param = function.params.get(i).lexeme;
            Object arg = arguments.get(i);

            environment.define(param, arg, true);
        }

        try{
            return interpreter.executeBlock(function.body, environment);
        }
        catch (ReturnException e){
            return e.val;
        }
    }

    @Override
    public int arity() {
        int arity = function.params.size();
        return arity;
    }

    @Override
    public String toString() {
        return "<fn " + function.name.lexeme + ">";
    }
}
