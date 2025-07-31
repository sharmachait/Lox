package Runner;

import java.util.List;
import Error.LoxError;
import Error.ParseError;
import Error.InterpreterException;
import Language.Lexicon.LoxScanner;
import Language.Lexicon.Token;
import Language.Lexicon.TokenType;
import Language.Syntax.AstPrinter;
import Language.Syntax.Interpreter;
import Language.Syntax.Grammar.Expression;
import Language.Syntax.Parser;


public class Runner {
    public static LoxError scanError = null;
    public static ParseError parseError = null;
    public static InterpreterException interpreterException = null;
    public static void run(String source){
        LoxScanner scanner = new LoxScanner(source);

        List<Token> tokens = scanner.scanTokens();
        if(scanError !=null){
            return;
        }
        System.out.println("============Tokens============");
        for(int i=0; i<tokens.size(); i++){
            Token token = tokens.get(i);
//            if(token.equals("error")){
//                return error(i, "error in: "+ token);
//            }
            System.out.println(token);
        }
        System.out.println("============Tokens============");
        Parser parser = new Parser(tokens);
        Expression expr = parser.parse();

        if(parseError !=null){
            return ;
        }
        System.out.println("============Parser============");
        System.out.println(new AstPrinter().print(expr));
        System.out.println("============Parser============");
        Interpreter interpreter = new Interpreter();
        System.out.println("============Interpreter============");
        interpreter.interpret(expr);
        System.out.println("============Interpreter============");
    }
    public static void scannerError(int line, String message){
        LoxError err = new LoxError(line, "",  message);
        System.err.println(err);
        scanError = err;
    }
    public static void parserError(Token token, String message) {
        ParseError  err = new ParseError(token.lexeme + ": " + message);
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
        parseError = err;
    }
    private static void report(int line, String where,
                               String message) {
        System.err.println(
                "[line " + line + "] Error" + where + ": " + message);
    }

    public static void runtimeException(InterpreterException e) {
        report(e.token.line, e.token.lexeme, e.getMessage());
        interpreterException = e;
    }
}
