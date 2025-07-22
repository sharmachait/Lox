package Runner;

import java.util.List;
import Error.LoxError;
import Syntax.LoxScanner;
import Syntax.Token;


public class Runner {
    public static LoxError error = null;
    public static LoxError run(String source){
        LoxScanner scanner = new LoxScanner(source);
        List<Token> tokens = scanner.scanTokens();
        if(error!=null){
            return error;
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
    public static LoxError error(int line, String message){
        LoxError err = new LoxError(line, "",  message);
        System.err.println(err);
        error = err;
        return err;
    }
}
