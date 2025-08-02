We started with 

```cfg
expression     → equality ;
equality       → comparison ( ( "!=" | "==" ) comparison )* ;
comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
term           → factor ( ( "-" | "+" ) factor )* ;
factor         → unary ( ( "/" | "*" ) unary )* ;
unary          → ( "!" | "-" ) unary | primary ;
primary        → NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" ;
```

then we added commas
```cfg
expression     → comma ;
comma          → assignment ( "," assignment )* ;
assignment     → equality ;
equality       → comparison ( ( "!=" | "==" ) comparison )* ;
comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
term           → factor ( ( "-" | "+" ) factor )* ;
factor         → unary ( ( "/" | "*" ) unary )* ;
unary          → ( "!" | "-" ) unary | primary ;
primary        → NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" ;
```

then ternary expressions
```cfg
expression     → comma ;
comma          → ternary ( "," ternary )* ;
ternary        → assignment ( "?" expression ":" ternary )? ;
assignment     → equality ;
equality       → comparison ( ( "!=" | "==" ) comparison )* ;
comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
term           → factor ( ( "-" | "+" ) factor )* ;
factor         → unary ( ( "/" | "*" ) unary )* ;
unary          → ( "!" | "-" ) unary | primary ;
primary        → NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" ;
```

now we are going to add program, statements, expression statements and print statement
```cfg
program        → statement* EOF ;
statement      → exprStmt | printStmt ;
exprStmt       → expression ";" ;
printStmt      → "print" expression ";" ;
expression     → comma ;
comma          → ternary ( "," ternary )* ;
ternary        → assignment ( "?" expression ":" ternary )? ;
assignment     → equality ;
equality       → comparison ( ( "!=" | "==" ) comparison )* ;
comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
term           → factor ( ( "-" | "+" ) factor )* ;
factor         → unary ( ( "/" | "*" ) unary )* ;
unary          → ( "!" | "-" ) unary | primary ;
primary        → NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" ;
```

There is no place in the grammar where both an expression and a statement are allowed. 
The operands of, say, + are always expressions, never statements. The body of a while loop is always a statement.

we’re going to split the statement grammar in two to handle them. 
That’s because the grammar restricts where some kinds of statements are allowed.

The clauses in control flow statements—think the then and else branches of an if statement or the body of a while—are each a single statement. 

But that statement is not allowed to be one that declares a name. 

This is OK:
`if (monday) print "Ugh, already?";`
But this is not:
`if (monday) var beverage = "espresso";`

to accommodate this distinction we again change the grammar by adding declaration statements, 
var declaration statements and updating Primaries to be identifiers as well
```cfg
program        → declaration* EOF ;
declaration    → varDecl | statement
statement      → exprStmt | printStmt ;
exprStmt       → expression ";" ;
printStmt      → "print" expression ";" ;
varDecl        → "var" IDENTIFIER ( "=" expression )? ";" ;
expression     → comma ;
comma          → ternary ( "," ternary )* ;
ternary        → assignment ( "?" expression ":" ternary )? ;
assignment     → equality ;
equality       → comparison ( ( "!=" | "==" ) comparison )* ;
comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
term           → factor ( ( "-" | "+" ) factor )* ;
factor         → unary ( ( "/" | "*" ) unary )* ;
unary          → ( "!" | "-" ) unary | primary ;
primary        → NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" | IDENTIFIER ;
```

adding reassignment of variables
```cfg
program        → declaration* EOF ;
declaration    → varDecl | statement
statement      → exprStmt | printStmt ;
exprStmt       → expression ";" ;
printStmt      → "print" expression ";" ;
varDecl        → "var" IDENTIFIER ( "=" expression )? ";" ;
expression     → comma ;
comma          → ternary ( "," ternary )* ;
ternary        → assignment ( "?" expression ":" ternary )? ;
assignment     → IDENTIFIER "=" assignment | equality ;
equality       → comparison ( ( "!=" | "==" ) comparison )* ;
comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
term           → factor ( ( "-" | "+" ) factor )* ;
factor         → unary ( ( "/" | "*" ) unary )* ;
unary          → ( "!" | "-" ) unary | primary ;
primary        → NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" | IDENTIFIER ;
```
this allows `a=b=c;`
but still doesnt allow
`instance.field = "value";`

assignment is weird because we dont know its an assignment untill we have parsed the LHS
this is different from a+b because a evaualtes to a value, but in case of assignment we dont evaluate to anything
we assign the LHS to it in the env

We want the syntax tree to reflect that an l-value isn’t evaluated like a normal expression, 
which is why we keep a token for LHS and not an expression

Adding Lexical scopes
```cfg
program        → declaration* EOF ;
declaration    → varDecl | statement ;
statement      → exprStmt | printStmt | block ;
block          → "{" declaration "}" ;
exprStmt       → expression ";" ;
printStmt      → "print" expression ";" ;
varDecl        → "var" IDENTIFIER ( "=" expression )? ";" ;
expression     → comma ;
comma          → ternary ( "," ternary )* ;
ternary        → assignment ( "?" expression ":" ternary )? ;
assignment     → IDENTIFIER "=" assignment | equality ;
equality       → comparison ( ( "!=" | "==" ) comparison )* ;
comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
term           → factor ( ( "-" | "+" ) factor )* ;
factor         → unary ( ( "/" | "*" ) unary )* ;
unary          → ( "!" | "-" ) unary | primary ;
primary        → NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" | IDENTIFIER ;
```