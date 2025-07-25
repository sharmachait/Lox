package Runner.IdleRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import Error.LoxError;
import Runner.Runner;

import static Runner.Runner.run;

public class IdleRunner {

    public void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        while(true) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null) break; // ctrl+d returns null in read line
            LoxError error = run(line);
            Runner.scanError = null;
        }
    }
}
