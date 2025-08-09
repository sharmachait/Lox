package Language.Syntax;

import Language.Lexicon.Token;
import Error.InterpreterException;
import java.util.HashMap;
import java.util.Map;

public class Environment {
    public static class Val{
        public boolean isDeclaredOrAssigned = false;
        public Object val;
        public static Val of(Object val, boolean isDeclaredOrAssigned){
            return new Val(val, isDeclaredOrAssigned);
        }
        public static Val of(Object val){
            return new Val(val);
        }
        private Val(Object val, boolean isDeclaredOrAssigned){
            this.val = val;
            this.isDeclaredOrAssigned = isDeclaredOrAssigned;
        }
        private Val(Object val){
            this.val = val;
        }
    }
    private final Environment enclosing;

    public Environment() {
        enclosing = null;
    }

    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    private final Map<String, Val> values = new HashMap<>();
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
    public void define(String name, Object value, boolean isAssigned){
        values.put(name, Val.of(value, isAssigned));
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
    public Val get(Token name) throws InterpreterException {
        if(values.containsKey(name.lexeme)){
            if(!values.get(name.lexeme).isDeclaredOrAssigned){
                throw new InterpreterException("Variable not declared / assigned a value, but is being accessed '" + name.lexeme + "'.", name);
            }
            return values.get(name.lexeme);
        }

        if (enclosing != null) return enclosing.get(name);

        throw new InterpreterException("Undefined variable '" + name.lexeme + "'.", name);
    }

    public Val assign(Token name, Val value){
        if(values.containsKey(name.lexeme)){
            values.put(name.lexeme, value);
            return value;
        }
        if(enclosing!=null){
            return enclosing.assign(name, value);
        }
        throw new InterpreterException("Undefined variable '" + name.lexeme + "'.", name);
    }
    public Val getFromDepth(Integer distance, String lexeme) {
        Environment ancestor = ancestor(distance);
        assert ancestor.values.get(lexeme) != null;
        return ancestor.values.get(lexeme);
    }

    private Environment ancestor(Integer distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
            assert environment != null;
            environment = environment.enclosing;
        }
        assert environment != null;
        return environment;
    }
    public Val assignToDepth(Integer distance, Token name, Val val) {
        Environment ancestor = ancestor(distance);
        assert ancestor.values.containsKey(name.lexeme);
        return ancestor.values.put(name.lexeme, val);
    }
}
