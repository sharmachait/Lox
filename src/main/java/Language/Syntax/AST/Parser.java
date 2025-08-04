package Language.Syntax.AST;

import Language.Lexicon.Token;
import Language.Lexicon.TokenType;
import Error.ParseError;
import Language.Syntax.AST.Grammar.Expressions.*;
import Language.Syntax.AST.Grammar.Statements.*;
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
            if(match(VAR)) return varDeclaration();
            if(match(FUN)) return function("function");
            return statement();
        }catch(Exception e){
            skipCurrentStatement();
            return null;
        }
    }

    private Statement function(String kind) throws ParseError {
        Token name = consume(IDENTIFIER, "Expect " + kind + " name.");
        consume(LEFT_PAREN, "Expect '(' after " + kind + " name.");
        List<Token> parameters = new ArrayList<>();
        if(!check(RIGHT_PAREN)){
            do {
                if (parameters.size() >= 255) {
                    error(peek(), "Can't have more than 255 parameters.");
                }

                parameters.add(
                        consume(IDENTIFIER, "Expect parameter name."));
            } while (match(COMMA));
        }
        consume(RIGHT_PAREN, "Expect ')' after parameters.");
        consume(LEFT_BRACE, "Expect '{' before " + kind + " body.");
        List<Statement> body = block();
        return new Function(name, parameters, body);
    }

    private List<Statement> block()throws ParseError{
        List<Statement> statements = new ArrayList<>();
        while(!check(RIGHT_BRACE) && !isAtEnd()){
            Statement statement = declaration();
            statements.add(statement);
        }
        consume(RIGHT_BRACE, "Expect '}' after block.");
        return statements;
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
        if(match(WHILE)) return whileStatement();
        if(match(FOR)) return forStatement();
        if(match(BREAK)) return breakStatement();
        if(match(CONTINUE)) return continueStatement();
        if(match(LEFT_BRACE)) return new Block(block());
        if(match(BREAK)) return new Block(block());
        if(match(IF)) return ifStatement();
        return expressionStatement();
    }

    private Statement continueStatement() throws ParseError {
        consume(SEMICOLON, "Expect ';' after break.");
        return new Continue();
    }

    private Statement breakStatement() throws ParseError {
        consume(SEMICOLON, "Expect ';' after break.");
        return new Break();
    }

    private Statement forStatement() throws ParseError {
        consume(LEFT_PAREN, "Expect '(' after 'for'.");
        Statement initializer;

        if(match(SEMICOLON)){
            initializer = null;
        }else if(match(VAR)){
            initializer = varDeclaration();
        }else{
            initializer = expressionStatement(); // should be statement(); if we make it like other languages
        }

        Expression condition = null;
        if(!check(SEMICOLON)){
            condition = expression();
        }
        consume(SEMICOLON, "Expect ';' after loop condition.");

        Expression incer = null;

        if(!check(RIGHT_PAREN)){
            incer = expression();
        }
        consume(RIGHT_PAREN, "Expect ')' after for clauses.");
        Statement body = statement();

        if(incer!=null) {
            body = new Block(
                List.of(
                    body,
                    new ExpressionStatement(incer)
                )
            );
        }
        /*
        * {
        * body => block | statement
        * incerStatement
        * }
        */
        if(condition == null) condition = new Literal(true);
        Statement whileStmt = new While(condition, body);

        Statement forStmt = whileStmt;

        if(initializer!=null){
            forStmt = new Block(
                    List.of(initializer, whileStmt)
            );
        }
        /*
         *{
         * initializer,
         *  {
         *  body => block | statement,
         *  incerStatement
         *  }
         * }
         */
        return forStmt;
    }

    private Statement whileStatement() throws ParseError {
        consume(LEFT_PAREN, "Expect '(' after 'while'.");
        Expression condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after condition.");
        Statement body = statement();
        return new While(condition, body);
    }

    private Statement ifStatement() throws ParseError {
        consume(LEFT_PAREN, "Expect '(' after 'if'.");
        Expression condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after if condition.");
        Statement thenBranch = statement();
        Statement elseBranch = null;
        if(match(ELSE)){
            elseBranch = statement();
        }
        return new If(condition, thenBranch, elseBranch);
    }

    private Statement printStatement() throws ParseError {
        Expression expression = expression();
        consume(SEMICOLON, "Expect ';' after value.");
        return new Print(expression);
    }

    private Statement expressionStatement() throws ParseError {
        Expression expression = expression();
        consume(SEMICOLON, "Expect ';' after value.");
        return new ExpressionStatement(expression);
    }

    private Expression expression() throws ParseError {
        return ternary();
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
        Expression expression =  or();
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

    public Expression or() throws ParseError{
        handleBinaryOperatorWithoutLeftExpression(OR);
        Expression left = and();
        while(match(OR)){
            Token operator = previous();
            Expression right = and();
            left = new Logical(left, operator, right);
        }
        return left;
    }

    public Expression and() throws ParseError{
        handleBinaryOperatorWithoutLeftExpression(AND);
        Expression left = equality();
        while(match(AND)){
            Token operator = previous();
            Expression right = equality();
            left = new Logical(left, operator, right);
        }
        return left;
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
        return call();
    }

    private Expression call() throws ParseError {
        Expression expr = primary();
        List<Expression> arguments = new ArrayList<>();
        while(true){ // we do it in a while loop because our language treas functions like 1st members and we can return functions from function so we can call them like func()()()()()()()();
            if(match(LEFT_PAREN)) {
                if(!check(RIGHT_PAREN)) arguments = arguments();
                Token paren = consume(RIGHT_PAREN,"Expect ')' after arguments.");
                expr = new Call(expr
                        , paren
                        , arguments);
            } else {
                break;
            }
        }
        return expr;
    }

    private List<Expression> arguments() throws ParseError {
        List<Expression> arguments = new ArrayList<>();

        Expression arg = expression();
        arguments.add(arg);
        while(match(COMMA)){
            if (arguments.size() >= 255) {
                error(peek(), "Can't have more than 255 arguments.");
            }
            arguments.add(expression());
        }
//            the lords loop
//            do {
//                arguments.add(expression());
//            } while (match(COMMA));
        return arguments;
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
        boolean ans = peek().type == EOF;
        return ans;
    }
    private Token peek(){
        Token peeked = tokens.get(current);
        return peeked;
    }
    private Token previous() {
        return tokens.get(current - 1);
    }
}
