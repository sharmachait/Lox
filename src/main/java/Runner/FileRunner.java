package Runner;

import Error.LoxError;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static Runner.Runner.run;

public class FileRunner {

    public void runFile(String filePath) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        run(new String(bytes, Charset.defaultCharset()), false);
        if(Runner.scanError != null || Runner.parseError != null){
            System.exit(65);
        }
        if(Runner.interpreterException != null){
            System.exit(70);
        }
    }
}
