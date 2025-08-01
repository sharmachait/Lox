package Language.Syntax;

import Language.Lexicon.Token;
import Error.InterpreterException;
import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final Map<String, Object> values = new HashMap<>();
    // we use Strings instead of Tokens because many objects of Tokens with the same lexeme may be created at different places throughout the source code,
    // we want them all to reference the same key in the environment
    public void define(String name, Object value){
        values.put(name, value);
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
    }

    public Object get(Token name) throws InterpreterException {
        if(values.containsKey(name.lexeme)){
            return values.get(name.lexeme);
        }

        throw new InterpreterException("Undefined variable '" + name.lexeme + "'.", name);
    }

}
