package Error;

import Language.Lexicon.Token;

public class InterpreterException extends RuntimeException{
    public final Token token;
    public InterpreterException(String message, Token token){
        super(message);
        this.token = token;
    }

    public InterpreterException(String message, Throwable cause, Token token) {
        super(message, cause);
        this.token = token;
    }
}
