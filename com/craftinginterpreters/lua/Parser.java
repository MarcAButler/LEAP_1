package com.craftinginterpreters.lua;

import java.util.ArrayList;
import java.util.List;

import static com.craftinginterpreters.lua.TokenType.*;

public class Parser
{
    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens)
    {
        this.tokens = tokens;
    }

    List<Stmt> parse()
    {
        List<Stmt> statements = new ArrayList<Stmt>();
        while (!isAtEnd())
        {
            statements.add(declaration());
        }
        return statements;
    }

    private Expr expression()
    {
        return assignment();
    }

    private Stmt declaration()
    {
        try
        {
            // [!] If there is an IDENTIFIER and a "="
            // we will declare it as a variable
            if (match(LOCAL) && peek().type == IDENTIFIER) return varLocalDeclaration();
            if (match(IDENTIFIER)) return varDeclaration();

            return statement();
        }
        catch (ParseError error)
        {
            synchronize();
            return null;
        }
    }

    private Stmt statement()
    {
        if (match(IF)) return ifStatement();
        if (match(PRINT)) return printStatement();
        if (match(INPUT)) return inputStatement();
        if (match(WHILE)) return whileStatement();

        // The actual repeat - until logic that is hit when a until keyword is found
        //if (match(UNTIL)) return repeatStatement();
        if (match(REPEAT)) return repeatStatement();
        
        // Simply the block constructesd from repeatBlock()
        //if (match(REPEAT)) return new Stmt.Block(repeatBlock());


        // [!] Should this go here?
        //if (match(LOCAL)) return localStatement();
        //if (match(LOCAL)) return varDeclaration();

        // If there is an "if" statement with a "then" or a loop with a "do"
        // a block has begun
        if (match(THEN, DO)) return new Stmt.Block(block());
        return expressionStatement();
    }

    private Stmt ifStatement()
    {
        // Lua does not concern itself with parenthesize in if statements
        // [!] consume(LEFT_PAREN, "Expect '(' after 'if'.");
        Expr condition = expression();
        // [!] consume(LEFT_PAREN, "Expect '(' after 'if'.");
        
        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(ELSE))
        {
            System.out.println("I AM BEING CALLED");
            elseBranch = statement();
        }

        return new Stmt.If(condition, thenBranch, elseBranch);
    }

    private Stmt printStatement()
    {
        consume(LEFT_PAREN, "Expect '(' after 'print.'");
        Expr value = expression();
        consume(RIGHT_PAREN, "Expect ')' after expression.");


        // [!] Expressions do not end with a ';' in Lua nor do we consume
        // a NEW_LINE denoting the end of the expression 
        // consume(SEMICOLON, "Expect ';' after value.");
        //consume(NEW_LINE, "Expect 'NEW_LINE' after value.");
        return new Stmt.Print(value);
    }

    private Stmt inputStatement()
    {
        consume(LEFT_PAREN, "Expect '(' after 'print.'");
        consume(RIGHT_PAREN, "Expect ')' after expression.");
        return new Stmt.Input(null);
    }

    // private Stmt localStatement()
    // {
    //     return declaration();
    // }

    // Parsing a Lua variable declaration
    private Stmt varDeclaration()
    {
        // Token name = consume(IDENTIFIER, "Expect variable name.");
        Token name = previous();

        
        // Handle variable intialization
        Expr initializer = null;

        if (match(EQUAL))
        {
            initializer = expression();
        }
        // If the type is a keyword then just get out of this method: return
        else if (contains(name.type))
        {
            return new Stmt.Var(name, initializer);
        }
        // Otherwise we throw an error for not properly assigning
        else
        {
            throw error(peek(), "Expect '='.");
        }

        return new Stmt.Var(name, initializer);
    }

    private Stmt varLocalDeclaration()
    {
        advance();
        // Token name = consume(IDENTIFIER, "Expect variable name.");
        Token name = previous();


        // Handle variable intialization
        Expr initializer = null;

        if (match(EQUAL))
        {
            initializer = expression();
        }
        // If the type is a keyword then just get out of this method: return
        else if (contains(name.type))
        {
            return new Stmt.LocalVar(name, initializer);
        }
        // Otherwise we throw an error for not properly assigning
        else
        {
            throw error(peek(), "Expect '='.");
        }

        return new Stmt.LocalVar(name, initializer);
    }

    public static boolean contains(TokenType purposedString)
    {
        for (TokenType token : TokenType.values())
        {
            if (token == purposedString)
            {
                return true;
            }
        }
        return false;
    }

    private Stmt whileStatement()
    {
        // [!] Note this is Lua, we do not check for parentheses
        Expr condition = expression();
        Stmt body = statement();

        return new Stmt.While(condition, body);
    }

    private Stmt repeatStatement()
    {
        // The body will be encountered first
        Stmt body = new Stmt.Block(repeatBlock());

        //consume(UNTIL, "Expect 'until' after statement(s).");
        Expr condition = expression();
        
        return new Stmt.Repeat(body, condition);
    }

    private Stmt expressionStatement()
    {
        Expr expr = expression();
        // [!] Expressions do not end with a ';' in Lua nor do we consume
        // a NEW_LINE denoting the end of the expression 
        //consume(SEMICOLON, "Expect ';' after expression.");
        //consume(NEW_LINE, "Expect 'NEW_LINE' after value.");
        return new Stmt.Expression(expr);
    }

    private List<Stmt> block()
    {
        List<Stmt> statements = new ArrayList<>();

        while (!check(END) && !isAtEnd())
        {
            statements.add(declaration());
        }

        consume(END, "Expect 'end' after block.");
        return statements;
    }

    private List<Stmt> repeatBlock()
    {
        List<Stmt> statements = new ArrayList<>();

        while (!check(UNTIL) && !isAtEnd())
        {
            statements.add(declaration());
        }

        consume(UNTIL, "Expect 'until' after block.");
        return statements;
    }

    private Expr assignment()
    {
        Expr expr = or();

        if (match(EQUAL))
        {
            Token equals = previous();
            Expr value = assignment();

            if (expr instanceof Expr.Variable)
            {
                Token name = ((Expr.Variable)expr).name;
                return new Expr.Assign(name, value);
            }
            error(equals, "Invalid assignment target.");
        }

        return expr;
    }

    private Expr or()
    {
        Expr expr = and();

        while (match(OR))
        {
            Token operator = previous();
            Expr right = and();
            expr = new Expr.Logical(expr, operator, right);
        }
        return expr;
    }

    private Expr and()
    {
        Expr expr = equality();

        while (match(AND))
        {
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr equality()
    {
        Expr expr = comparison();

        while (match(TILDE_EQUAL, EQUAL_EQUAL))
        {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr comparison()
    {
        Expr expr = concatenation();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL))
        {
            Token operator = previous();
            Expr right = concatenation();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    // [!] 
    private Expr concatenation()
    {
        Expr expr = term();

        while (match(DOT_DOT))
        {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr term()
    {
        Expr expr = factor();

        while (match(HYPHEN, PLUS))
        {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr factor()
    {
        Expr expr = exponent();

        while(match(SLASH, STAR))
        {
            Token operator = previous();
            Expr right = exponent();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr exponent()
    {
        Expr expr = unary();

        while(match(EXPONENT))
        {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary()
    {
        if (match(TILDE, HYPHEN))
        {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return primary();
    }

    private Expr primary()
    {
        if (match(FALSE)) return new Expr.Literal(false);
        if (match(TRUE)) return new Expr.Literal(true);
        if (match(NIL)) return new Expr.Literal(null);

        // [!] Is a NEW_LINE considered to be a literal? and if so,
        // should we distinguish it from NUMBER and STRING?
        //if (match(NUMBER, STRING, NEW_LINE))
        if (match(NUMBER, STRING))
        {
            return new Expr.Literal(previous().literal);
        }

        // Parsing Lua variable expression
        if (match(IDENTIFIER))
        {
            return new Expr.Variable(previous());
        }

        if (match(LEFT_PAREN))
        {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "Expect expression.");
    }

    private boolean match(TokenType... types)
    {
        for (TokenType type : types)
        {
            if (check(type))
            {
                advance();
                return true;
            }
        }
        
        return false;
    }

    private boolean check(TokenType type)
    {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance()
    {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd()
    {
        return peek().type == EOF;
    }

    private Token peek()
    {
        return tokens.get(current);
    }

    private Token previous()
    {
        return tokens.get(current - 1);
    }

    private ParseError error(Token token , String message)
    {
        Lua.error(token, message);
        return new ParseError();
    }

    private void synchronize()
    {
        advance();

        while (!isAtEnd())
        {
            if (previous().type == SEMICOLON) return;

            switch (peek().type)
            {
                case IF:
                case INPUT:
                case WHILE:
                case PRINT:
                    return;
            }

            advance();
        }
    }

    private Token consume(TokenType type, String message)
    {
        if(check(type)) return advance();

        throw error(peek(), message);
    }
}
