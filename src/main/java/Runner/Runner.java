package Runner;

import java.util.Scanner;
import java.util.stream.Stream;

public class Runner {
    public static void run(String source){
        Scanner scanner = new Scanner(source);
        Stream<String> tokens = scanner.tokens();

       tokens.forEach((token)-> System.out.println(token));
    }
}
