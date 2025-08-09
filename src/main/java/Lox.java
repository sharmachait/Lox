import Runner.FileRunner;
import Runner.IdleRunner;

import java.io.IOException;


public class Lox {
  public static void main(String[] args) throws IOException {
    if (args.length > 1) {
      System.out.println("Usage: jlox [script]");
      System.exit(64);
    } else if (args.length == 1) {
      FileRunner fr = new FileRunner();
      fr.runFile(args[0]);
    } else {
      IdleRunner idle = new IdleRunner();
      idle.runPrompt();
    }
  }
//    public static void main(String[] args) {
//        Expression expr = new BinaryExpression(
//                new UnaryExpression(
//                        new Token(TokenType.MINUS, "-", null, 1),
//                        new Literal(123)
//                ),
//                new Token(TokenType.STAR, "*", null, 1),
//                new Grouping(new Literal(45.67))
//        );
//        System.out.println(new AstPrinter().print(expr));
//    }
}