package Language.Syntax;

import Language.Lexicon.Token;
import Error.InterpreterException;
import java.util.HashMap;
import java.util.Map;

public class Environment {
    private static volatile Environment env;
    private Environment(){}
    public static Environment getInstance(){
        if(env == null){
            synchronized (Environment.class){
                if(env==null){
                    env = new Environment();
                }
            }
        }
        return env;
    }
    private final Map<String, Object> values = new HashMap<>();
    // we use Strings instead of Tokens because many objects of Tokens with the same lexeme may be created at different places throughout the source code,
    // we want them all to reference the same key in the environment
    /*
     * since we dont check if a variable exists before adding it agin into the map
     * the following piece of code is allowed in lox
     *
     * var a = 1;
     * print a;
     * var a = 2;
     * print a;
     *
     * thats a semantic choice of the language, like python the variable may be redefined
     * */
    public void define(String name, Object value){
        values.put(name, value);
    }

    // You can refer to a variable in a chunk of code without immediately evaluating it if that chunk of code is wrapped inside a function.
    // if we make it a static error to mention a variable before declaring it we can do recursion
    // we are making it an interpreter error at runtime because if it were a syntax error, then we cant do
    // fun isOdd(n) {
    //     if (n == 0) return false;
    //     return isEven(n - 1);
    // }
    //
    // fun isEven(n) {
    //     if (n == 0) return true;
    //     return isOdd(n - 1);
    // }
    //
    // when we are parsing the isOdd method there is no such thing as isEven method
    // we throw an error only when we try to evaluate a variable like so
    // print a;
    // var a = "hi";
    public Object get(Token name) throws InterpreterException {
        if(values.containsKey(name.lexeme)){
            return values.get(name.lexeme);
        }

        throw new InterpreterException("Undefined variable '" + name.lexeme + "'.", name);
    }

    public Object assign(Token name, Object value){
        if(values.containsKey(name.lexeme)){
            values.put(name.lexeme, value);
            return value;
        }
        throw new InterpreterException("Undefined variable '" + name.lexeme + "'.", name);
    }
}
