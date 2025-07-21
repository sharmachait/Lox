package Runner;

import java.util.List;
import java.util.Scanner;
import Error.LoxError;


public class Runner {
    public static LoxError run(String source){
        Scanner scanner = new Scanner(source);
        List<String> tokens = scanner.tokens().toList();
        boolean error = false;

        for(int i=0; i<tokens.size(); i++){
            String token = tokens.get(i);
            if(token.equals("error")){
                return error(i, "error in: "+ token);
            }
            System.out.println(token);
        }
        return null;
    }
    static LoxError error(int line, String message){
        LoxError err = new LoxError(line, "",  message);
        System.err.println(err);
        return err;
    }
}
