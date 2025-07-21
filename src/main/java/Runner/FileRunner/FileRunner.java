package Runner.FileRunner;

import Error.LoxError;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static Runner.Runner.run;

public class FileRunner {
    private LoxError error;

    public void runFile(String filePath) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        this.error = run(new String(bytes, Charset.defaultCharset()));
        if(this.error != null){
            System.exit(65);
        }
    }
}
