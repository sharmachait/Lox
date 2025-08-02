package Runner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import Error.LoxError;

import static Runner.Runner.run;

public class IdleRunner {

    public void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        while(true) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null) break; // ctrl+d returns null in read line
            run(line, true);
            Runner.scanError = null;
            Runner.parseError = null;
            Runner.interpreterException = null;
        }
    }
}
