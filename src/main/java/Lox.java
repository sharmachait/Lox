import Runner.FileRunner.FileRunner;
import Runner.IdleRunner.IdleRunner;

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
}
// https://craftinginterpreters.com/representing-code.html