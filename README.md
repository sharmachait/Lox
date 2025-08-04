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

Adding conditions
```cfg
program        → declaration* EOF ;
declaration    → varDecl | statement ;
statement      → exprStmt | printStmt | block | ifStmt ;
ifStmt         → "if" "(" expression ")" statement ("else" statement)? ;
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

this grammar is ambiguous, Consider:
`if (first) if (second) whenTrue(); else whenFalse();`

which if does the else belong to
this is a classic dangling else problem
one solution is to have two if statements one that has an else and one that doesnt in the CFG itself

or we can make a sematic choice, by binding the else to the nearest if in the scope, 
by eagerly looking for an else when ever we encounter an if

logical or has a lower precedence than logical and and to reflect that we update the CFG to  
```cfg
program        → declaration* EOF ;
declaration    → varDecl | statement ;
statement      → exprStmt | printStmt | block | ifStmt ;
ifStmt         → "if" "(" expression ")" statement ("else" statement)? ;
block          → "{" declaration "}" ;
exprStmt       → expression ";" ;
printStmt      → "print" expression ";" ;
varDecl        → "var" IDENTIFIER ( "=" expression )? ";" ;
expression     → comma ;
comma          → ternary ( "," ternary )* ;
ternary        → assignment ( "?" expression ":" ternary )? ;
assignment     → IDENTIFIER "=" assignment | logic_or ;
logic_or       → logic_and ( "or" logic_and )* ;
logic_and      → equality ( "and" equality )* ;
equality       → comparison ( ( "!=" | "==" ) comparison )* ;
comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
term           → factor ( ( "-" | "+" ) factor )* ;
factor         → unary ( ( "/" | "*" ) unary )* ;
unary          → ( "!" | "-" ) unary | primary ;
primary        → NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" | IDENTIFIER ;
```

to add while loops and for loops break and continue as well

```cfg
program        → declaration* EOF ;
declaration    → varDecl | statement ;
statement      → exprStmt | printStmt | block | ifStmt | whileStmt | forStmt | breakStmt | continueStmt ;
breakStmt      → "break" ";" ;
continueStmt   → "continue" ";" ;
ifStmt         → "if" "(" expression ")" statement ("else" statement)? ;
whileStmt      → "while" "(" expression ")" statement ; 
forStmt        → "for" "(" ( varDecl | exprStmt | ";") expression? ";" expression? ";" ")" statement ; 
block          → "{" declaration "}" ;
exprStmt       → expression ";" ;
printStmt      → "print" expression ";" ;
varDecl        → "var" IDENTIFIER ( "=" expression )? ";" ;
expression     → comma ;
comma          → ternary ( "," ternary )* ;
ternary        → assignment ( "?" expression ":" ternary )? ;
assignment     → IDENTIFIER "=" assignment | logic_or ;
logic_or       → logic_and ( "or" logic_and )* ;
logic_and      → equality ( "and" equality )* ;
equality       → comparison ( ( "!=" | "==" ) comparison )* ;
comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
term           → factor ( ( "-" | "+" ) factor )* ;
factor         → unary ( ( "/" | "*" ) unary )* ;
unary          → ( "!" | "-" ) unary | primary ;
primary        → NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" | IDENTIFIER ;
```
as anything you can doo with for loops can also be done with while loops, 
when we detect a for loop we can translate it to a while loop

Adding functions
they need to be high precedence because any expression that resolves to anything that can be called should be able to be called
calling functions is like a post fix operator, on one thing, so its kind like a unaary

```cfg
program        → declaration* EOF ;
declaration    → varDecl | statement ;
statement      → exprStmt | printStmt | block | ifStmt | whileStmt | forStmt | breakStmt | continueStmt ;
breakStmt      → "break" ";" ;
continueStmt   → "continue" ";" ;
ifStmt         → "if" "(" expression ")" statement ("else" statement)? ;
whileStmt      → "while" "(" expression ")" statement ; 
forStmt        → "for" "(" ( varDecl | exprStmt | ";") expression? ";" expression? ";" ")" statement ; 
block          → "{" declaration "}" ;
exprStmt       → expression ";" ;
printStmt      → "print" expression ";" ;
varDecl        → "var" IDENTIFIER ( "=" expression )? ";" ;
expression     → comma ;
comma          → ternary ( "," ternary )* ;
ternary        → assignment ( "?" expression ":" ternary )? ;
assignment     → IDENTIFIER "=" assignment | logic_or ;
logic_or       → logic_and ( "or" logic_and )* ;
logic_and      → equality ( "and" equality )* ;
equality       → comparison ( ( "!=" | "==" ) comparison )* ;
comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
term           → factor ( ( "-" | "+" ) factor )* ;
factor         → unary ( ( "/" | "*" ) unary )* ;
unary          → ( "!" | "-" ) unary | call ;
call           → primary( "(" arguments? ")" )* ;
arguments      → expression ( "," expression )* ; 
primary        → NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" | IDENTIFIER ;
```
To handle zero-argument calls, the call rule itself considers the entire arguments production to be optional.

native functions are globally scoped, so they need to be added to a global Environment that is not changing
therefore we add a global in the Interpreter
we also need to add function declarations to the grammar

```cfg
program        → declaration* EOF ;
declaration    → varDecl | statement | funDecl ;
funDecl        → "fun" function;
function       → IDENTIFIER "(" parameters? ")" block ;
parmeters      → IDENTIFIER ( "," IDENTIFIER )* ;
statement      → exprStmt | printStmt | block | ifStmt | whileStmt | forStmt | breakStmt | continueStmt ;
breakStmt      → "break" ";" ;
continueStmt   → "continue" ";" ;
ifStmt         → "if" "(" expression ")" statement ("else" statement)? ;
whileStmt      → "while" "(" expression ")" statement ; 
forStmt        → "for" "(" ( varDecl | exprStmt | ";") expression? ";" expression? ";" ")" statement ; 
block          → "{" declaration "}" ;
exprStmt       → expression ";" ;
printStmt      → "print" expression ";" ;
varDecl        → "var" IDENTIFIER ( "=" expression )? ";" ;
expression     → comma ;
comma          → ternary ( "," ternary )* ;
ternary        → assignment ( "?" expression ":" ternary )? ;
assignment     → IDENTIFIER "=" assignment | logic_or ;
logic_or       → logic_and ( "or" logic_and )* ;
logic_and      → equality ( "and" equality )* ;
equality       → comparison ( ( "!=" | "==" ) comparison )* ;
comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
term           → factor ( ( "-" | "+" ) factor )* ;
factor         → unary ( ( "/" | "*" ) unary )* ;
unary          → ( "!" | "-" ) unary | call ;
call           → primary ( "(" arguments? ")" )* ;
arguments      → expression ( "," expression )* ; 
primary        → NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" | IDENTIFIER ;
```