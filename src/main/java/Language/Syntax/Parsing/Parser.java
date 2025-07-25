package Language.Syntax.Parsing;

import Language.Lexicon.Token;
import Language.Lexicon.TokenType;
import Language.Syntax.Grammar.*;
import Error.ParseError;
import Runner.Runner;
import java.util.List;

import static Language.Lexicon.TokenType.*;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }
    
    private Expression expression(){
        return equality();
    }

    private Expression equality() {
        Expression expr = comparison();

        while(match(BANG_EQUAL, EQUAL_EQUAL)){
            Token operator = previous();
            Expression right = comparison();
            expr = new BinaryExpression(expr, operator, right);
        }

        // since we dont enter the loop when we dont match any equality operator we are essentially just calling comparison and returning

        return expr;
    }

    private Expression comparison() {
        Expression expr = term();

        while(match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)){
            Token operator = previous();
            Expression right = term();
            expr = new BinaryExpression(expr, operator, right);
        }

        return expr;
    }
    private Expression term() {
        Expression expr = factor();

        while(match(MINUS, PLUS)){
            Token operator = previous();
            Expression right = factor();
            expr = new BinaryExpression(expr, operator, right);
        }

        return expr;
    }

    private Expression factor() {
        Expression expr = unary();

        while(match(SLASH, STAR)){
            Token operator = previous();
            Expression right = unary();
            expr = new BinaryExpression(expr, operator, right);
        }

        return expr;
    }
    private Expression unary(){
        if(match(BANG,MINUS)){
            Token operator = previous();
            Expression right = unary();
            return new UnaryExpression(operator, right);
        }
        return primary();
    }

    private Expression primary(){
        if(match(FALSE)) return new Literal(false);
        if(match(TRUE)) return new Literal(true);
        if(match(NIL)) return new Literal(null);

        if(match(NUMBER, STRING)){
            return new Literal(previous().literal);
        }

        if(match(LEFT_PAREN)){
            Expression expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Grouping(expr);
        }

        throw error(peek(), "Expression expected.");
    }

    private ParseError error(Token token, String message){
        Runner.parserError(token, message);
        return new ParseError(token.lexeme + ": " + message);
    }

    private boolean match(TokenType... tokenTypes){
        for(TokenType type: tokenTypes){
            if(check(type)){
                advance();
                return true;
            }
        }
        return false;
    }
    private boolean check(TokenType type){
        if(isAtEnd()) return false;
        return peek().type == type;
    }
    private Token advance(){
        if(!isAtEnd()) current++;
        return previous();
    }
    private boolean isAtEnd(){
        return peek().type == EOF;
    }
    private Token peek(){
        return tokens.get(current);
    }
    private Token previous() {
        return tokens.get(current - 1);
    }
}
