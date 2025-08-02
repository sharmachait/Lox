package Language.Syntax.AST;

import Language.Lexicon.Token;
import Language.Lexicon.TokenType;
import Error.ParseError;
import Language.Syntax.AST.Grammar.Expressions.*;
import Language.Syntax.AST.Grammar.Statements.ExpressionStatement;
import Language.Syntax.AST.Grammar.Statements.Print;
import Language.Syntax.AST.Grammar.Statements.Statement;
import Language.Syntax.AST.Grammar.Statements.VarDecl;
import Language.Syntax.AstPrinter;
import Runner.Runner;

import java.util.ArrayList;
import java.util.List;

import static Language.Lexicon.TokenType.*;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Statement> parse(){
        List<Statement> statements = new ArrayList<>();
        while(!isAtEnd()){
            statements.add(declaration());
        }
        return statements;
    }

    private Statement declaration() {
        try{
            if(match(VAR)){
                return varDeclaration();
            }
            return statement();
        }catch(Exception e){
            skipCurrentStatement();
            return null;
        }
    }

    private Statement varDeclaration() throws ParseError {
        Variable var = (Variable)primary();
        Token name = var.name;
        Expression initializer = null;
        if(match(EQUAL)){
            initializer = expression();
        }
        consume(SEMICOLON, "Expect ';' after variable declaration.");
        return new VarDecl(name, initializer);
    }

    private Statement statement() throws ParseError {
        if(match(PRINT)) return printStatement();
        return expressionStatement();
    }

    private Statement printStatement() throws ParseError {
        Expression expression = expression();
        consume(SEMICOLON, "Expect ';' after value.");
        return new Print(expression);
    }

    private Statement expressionStatement() throws ParseError {
        Expression expression = expression();
        consume(SEMICOLON, "Expect ';' after value.");
        System.out.println("============PrintStatement============");
        System.out.println(new AstPrinter().print(expression));
        System.out.println("============PrintStatement============");
        return new ExpressionStatement(expression);
    }

    private Expression expression() throws ParseError {
        return comma();
    }

    private Expression comma() throws ParseError {
        handleBinaryOperatorWithoutLeftExpression(COMMA);
        Expression expr = ternary();
        while(match(COMMA)){
            Token operator = previous();
            Expression right = ternary();
            expr = new BinaryExpression(expr, operator, right);
        }
        return expr;
    }

    private Expression ternary() throws ParseError {
        handleBinaryOperatorWithoutLeftExpression(QUESTION);
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

    private Expression assignment() throws ParseError {
        Expression expression =  equality();
        if(match(EQUAL)) {
            Token equals = previous();
            Expression value = assignment();

            if(expression instanceof Variable) {
                Token name = ((Variable) expression).name;
                return new Assignment(name, value);
            }
            throw error(equals, "Invalid assignment target.");
        }
        return expression;
    }

    private Expression equality() throws ParseError {
        handleBinaryOperatorWithoutLeftExpression(BANG_EQUAL, EQUAL_EQUAL);
        Expression expr = comparison();

        while(match(BANG_EQUAL, EQUAL_EQUAL)){
            Token operator = previous();
            Expression right = comparison();
            expr = new BinaryExpression(expr, operator, right);
        }

        // since we dont enter the loop when we dont match any equality operator we are essentially just calling comparison and returning

        return expr;
    }

    private Expression comparison() throws ParseError {
        handleBinaryOperatorWithoutLeftExpression(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL);
        Expression expr = term();

        while(match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)){
            Token operator = previous();
            Expression right = term();
            expr = new BinaryExpression(expr, operator, right);
        }

        return expr;
    }
    private Expression term() throws ParseError {
        handleBinaryOperatorWithoutLeftExpression(PLUS);
        Expression expr = factor();

        while(match(MINUS, PLUS)){
            Token operator = previous();
            Expression right = factor();
            expr = new BinaryExpression(expr, operator, right);
        }

        return expr;
    }

    private Expression factor() throws ParseError {
        handleBinaryOperatorWithoutLeftExpression(SLASH, STAR);
        Expression expr = unary();

        while(match(SLASH, STAR)){
            Token operator = previous();
            Expression right = unary();
            expr = new BinaryExpression(expr, operator, right);
        }

        return expr;
    }
    private Expression unary() throws ParseError {
        if(match(BANG,MINUS)){
            Token operator = previous();
            Expression right = unary();
            return new UnaryExpression(operator, right);
        }
        return primary();
    }

    private Expression primary() throws ParseError {
        if(match(FALSE)) return new Literal(false);
        if(match(TRUE)) return new Literal(true);
        if(match(NIL)) return new Literal(null);
        if(match(IDENTIFIER)) {
            return new Variable(previous());
        }
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

    private void skipCurrentStatement(){
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

    private Token consume(TokenType tokenType, String message) throws ParseError {
        if(check(tokenType)) return advance();
        throw error(peek(), message);
    }

    private ParseError error(Token token, String message) throws ParseError {
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
