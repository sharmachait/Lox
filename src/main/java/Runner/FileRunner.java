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
        LoxError error = run(new String(bytes, Charset.defaultCharset()));
        if(error != null){
            System.exit(65);
        }
    }
}
