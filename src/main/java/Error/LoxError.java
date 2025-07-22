package Error;

public class LoxError {
    private final int line;
    private final String where;
    private final String message;

    public LoxError(int line, String where, String message) {
        this.line = line;
        this.where = where;
        this.message = message;
    }

    @Override
    public String toString() {
        return "[line " + line + "] Error" + where + ": " + message;
    }
}
