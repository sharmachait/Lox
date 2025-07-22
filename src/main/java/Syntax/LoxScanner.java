package Syntax;

import java.util.ArrayList;
import java.util.List;
import Error.LoxError;
import Runner.Runner;

import static Syntax.TokenType.*;

public class LoxScanner {
    private final String source;
    private int start = 0;
    private int current = 0;
    private int line = 1;
    private final List<Token> tokens = new ArrayList<>();

    public LoxScanner(String source) {
        this.source = source;
    }
    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        LoxError err = Runner.error;
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(':
                addToken(LEFT_PAREN);
                break;
            case ')':
                addToken(RIGHT_PAREN);
                break;
            case '{':
                addToken(LEFT_BRACE);
                break;
            case '}':
                addToken(RIGHT_BRACE);
                break;
            case ',':
                addToken(COMMA);
                break;
            case '.':
                addToken(DOT);
                break;
            case '-':
                addToken(MINUS);
                break;
            case '+':
                addToken(PLUS);
                break;
            case ';':
                addToken(SEMICOLON);
                break;
            case '*':
                addToken(STAR);
                break;
            case '!':
                addToken(matchNext('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(matchNext('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(matchNext('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(matchNext('=') ? GREATER_EQUAL : GREATER);
                break;
            default:
                Runner.error(line, "Unexpected character: "+c+".");
                break;
        }
    }
    private boolean matchNext(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }
    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}
