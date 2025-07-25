package Language.Lexicon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Error.LoxError;
import Runner.Runner;

import static Language.Lexicon.TokenType.*;

public class LoxScanner {
    private final String source;
    private static final Map<String, TokenType> keywords;
    static {
        keywords = new HashMap<>();
        keywords.put("and",    AND);
        keywords.put("class",  CLASS);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        keywords.put("fun",    FUN);
        keywords.put("if",     IF);
        keywords.put("nil",    NIL);
        keywords.put("or",     OR);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("super",  SUPER);
        keywords.put("this",   THIS);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);
    }
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
        LoxError err = Runner.scanError;
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
            case '/':
                if(matchNext('/')){
                    while(peek()!='\n' && !isAtEnd()) advance();
                }else if(matchNext('*')){
                    consumeMultilineComment();
                }else{
                    addToken(SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;
            case '\n':
                line++;
                break;
            case '"': string(); break;
            default:
                if(Character.isDigit(c)){
                    number();
                }else if(isAlpha(c)){
                    identifier();
                }else{
                    Runner.scannerError(line, "Unexpected character: "+c+".");
                }
                break;
        }
    }

    private void consumeMultilineComment() {
        while (!isAtEnd() && !(peek() == '*' && peekNext() == '/')) {
            if (peek() == '\n') line++;
            advance();
        }
        if (isAtEnd()) {
            Runner.scannerError(line, "Unterminated multi line comment: " + source.substring(start, current));
            return;
        }

        advance(); // '*'
        advance(); // '/'
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        String text = source.substring(start, current);
        TokenType type = keywords.getOrDefault(text, IDENTIFIER);
        addToken(type);
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || Character.isDigit(c);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }
    private void number() {
        while(Character.isDigit(peek())) advance();
        if(peek() == '.' && Character.isDigit(peekNext())){
            advance();
            if(!Character.isDigit(peek())){
                Runner.scannerError(line, "Invalid number literal: "+source.substring(start,current)+".");
                return;
            }
            while(Character.isDigit(peek())) advance();
        }
        addToken(NUMBER, Double.parseDouble(source.substring(start,current)));
    }

    private void string() {
        while(peek() != '"' && !isAtEnd()){
            if(peek() == '\n') line++; // we support multi line strings
            advance();
        }
        if(isAtEnd()){
            Runner.scannerError(line, "Unterminated string: "+source.substring(start,source.length())+" .");
            return;
        }
        // current must necessarily be at "
        advance();
        String value = source.substring(start+1, current-1);
        // If Lox supported escape sequences like \n, we’d unescape those here.
        addToken(STRING, value);
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
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }
    private char peekNext() {
        int next = current+1;
        if (next >= source.length()) return '\0';
        return source.charAt(next);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}
