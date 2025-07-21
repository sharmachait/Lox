import java.io.IOException;

import static Runner.FileRunner.FileRunner.runFile;
import static Runner.IdleRunner.IdleRunner.runPrompt;

public class Lox {
  public static void main(String[] args) throws IOException {
    if (args.length > 1) {
      System.out.println("Usage: jlox [script]");
      System.exit(64);
    } else if (args.length == 1) {
      runFile(args[0]);
    } else {
      runPrompt();
    }
  }
}
