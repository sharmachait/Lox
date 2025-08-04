package Language.Syntax;

import java.util.List;
/*
* The Java representation of any Lox object that can be called like a function will implement this interface.
* That includes user-defined functions, naturally,
* but also class objects since classes are “called” to construct new instances.
* */
public interface LoxCallable {
    Object call(Interpreter interpreter, List<Object> arguments);
}

