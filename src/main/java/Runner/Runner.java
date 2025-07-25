package Runner;

import java.util.List;
import Error.LoxError;
import Error.ParseError;
import Language.Lexicon.LoxScanner;
import Language.Lexicon.Token;
import Language.Lexicon.TokenType;


public class Runner {
    public static LoxError scanError = null;
    public static ParseError parseError = null;
    public static LoxError run(String source){
        LoxScanner scanner = new LoxScanner(source);

        List<Token> tokens = scanner.scanTokens();
        if(scanError !=null){
            return scanError;
        }
        System.out.println("============Tokens============");
        for(int i=0; i<tokens.size(); i++){
            Token token = tokens.get(i);
//            if(token.equals("error")){
//                return error(i, "error in: "+ token);
//            }
            System.out.println(token);
        }
        return null;
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
}
https://craftinginterpreters.com/parsing-expressions.html#panic-mode-error-recovery