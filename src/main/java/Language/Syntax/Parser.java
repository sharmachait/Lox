package Language.Syntax;

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

    public Expression parse(){
        try{
            return expression();
        }catch(ParseError e){
            return null;
        }
    }

    private Expression expression(){
        return comma();
    }

    private Expression comma(){
        try{
            handleBinaryOperatorWithoutLeftExpression(COMMA);
        }catch (ParseError e){
            //discard right hand expression
            //Expression discarded = ternary();
            return null;
        }
        Expression expr = ternary();
        while(match(COMMA)){
            Token operator = previous();
            Expression right = ternary();
            expr = new BinaryExpression(expr, operator, right);
        }
        return expr;
    }

    private Expression ternary(){
        try{
            handleBinaryOperatorWithoutLeftExpression(QUESTION);
        }catch (ParseError e){
            //discard right hand expression
            //Expression discarded = assignment();
            return null;
        }
        Expression expr = assignment();
        if(match(QUESTION)){
            Expression trueBranch = expression();
            consume(COLON, "Colon Expected after ? of ternary expression.");
            Token operator = previous();
            Expression falseBranch = expression();
            expr = new TernaryExpression(expr, trueBranch, falseBranch, operator);
        }
        return expr;
    }

    private Expression assignment(){
        return equality();
    }

    private Expression equality() {
        try{
            handleBinaryOperatorWithoutLeftExpression(BANG_EQUAL, EQUAL_EQUAL);
        }catch (ParseError e){
            //discard right hand expression
            //Expression discarded = comparison();
            return null;
        }
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
        try{
            handleBinaryOperatorWithoutLeftExpression(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL);
        }catch (ParseError e){
            //discard right hand expression
            //Expression discarded = term();
            return null;
        }
        Expression expr = term();

        while(match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)){
            Token operator = previous();
            Expression right = term();
            expr = new BinaryExpression(expr, operator, right);
        }

        return expr;
    }
    private Expression term() {
        try{
            handleBinaryOperatorWithoutLeftExpression(PLUS);
        }catch (ParseError e){
            //discard right hand expression
            //Expression discarded = factor();
            return null;
        }
        Expression expr = factor();

        while(match(MINUS, PLUS)){
            Token operator = previous();
            Expression right = factor();
            expr = new BinaryExpression(expr, operator, right);
        }

        return expr;
    }

    private Expression factor() {
        try{
            handleBinaryOperatorWithoutLeftExpression(SLASH, STAR);
        }catch (ParseError e){
            //discard right hand expression
            //Expression discarded = unary();
            return null;
        }
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

    private void synchronize(){
        advance();
        while(!isAtEnd()){
            if(previous().type == SEMICOLON) return;
            switch(peek().type){
                case CLASS, FUN, VAR, FOR, IF, WHILE, PRINT, RETURN:
                    return;
            }
            advance();
        }
    }

    private void handleBinaryOperatorWithoutLeftExpression(TokenType... operators) throws ParseError {
        for(TokenType type:  operators){
            if(check(type)){
                Token operator = advance();
                throw error(operator,"Missing left-hand operand for '"+ operator.lexeme+"'");
            }
        }
    }

    private Token consume(TokenType tokenType, String message) {
        if(check(tokenType)) return advance();
        throw error(peek(), message);
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
