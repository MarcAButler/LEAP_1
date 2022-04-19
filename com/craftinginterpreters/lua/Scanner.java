package com.craftinginterpreters.lua;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.craftinginterpreters.lua.TokenType.*;

public class Scanner
{
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final Map<String, TokenType> keywords;

    static
    {
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("function", FUNCTION);
        keywords.put("if", IF);
        keywords.put("then", THEN);
        keywords.put("do", DO);
        keywords.put("end", END);
        keywords.put("nil", NIL);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("true", TRUE);
        keywords.put("local", LOCAL);
        keywords.put("while", WHILE);
    }

    Scanner(String source)
    {
        this.source = source;
    }

    List<Token> scanTokens()
    {
        while (!isAtEnd())
        {
            // We are at the beginning of the next lexeme
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private boolean isAtEnd()
    {
        return current >= source.length();
    }

    private void scanToken()
    {
        char c = advance();
        switch (c)
        {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(RIGHT_BRACE); break;
            case '}': addToken(LEFT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case '/': addToken(SLASH); break;
            case '-':
                if(match('-'))
                {
                    // A comment goes until the end of the line
                    // Not considered by parser
                    while (peek() != '\n' && !isAtEnd()) advance();
                }
                else
                {
                    addToken(HYPHEN);
                }
                break;
            case '~':
                addToken(match('=') ? TILDE_EQUAL : TILDE);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            // Because Lua is a free-form language, it will ignore spaces and comments
            // between lexical elements (tokens), expect as delimters between two tokens
            // Note that it will not through a error, however
            case '.':
                addToken(match('.') ? DOT_DOT : DOT);
                break;
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
            // Because we are using Lua, we consume a add a NewLine character
            // in order to complete writing statements
                addToken(NEW_LINE);
                line++;
                break;
            case '"': string(); break;
            default:
                if (isDigit(c))
                {
                    number();
                }
                else if(isAlpha(c))
                {
                    identifier();
                }
                else
                {
                    Lua.error(line, "Unexpected character.");
                }
                break;
        }
    }

    private void identifier()
    {
        while (isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = IDENTIFIER;
        
        addToken(type);
    }

    private void number()
    {
        while (isDigit(peek())) advance();

        // Look for a fractional part
        if (peek() == '.' && isDigit(peekNext()))
        {
            // Consume the "."
            advance();

            while(isDigit(peek())) advance();
        }

        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private void string()
    {
        while(peek() != '"' && !isAtEnd())
        {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd())
        {
            Lua.error(line, "Unterminated string.");
            return;
        }

        // The closing "
        advance();

        // Trim the surrounding quotes
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private boolean match(char expected)
    {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private char peek()
    {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char peekNext()
    {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean isAlpha(char c)
    {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c)
    {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isDigit(char c)
    {
        return c >= '0' && c <= '9';
    }

    private char advance()
    {
        return source.charAt(current++);
    }

    private void addToken(TokenType type)
    {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal)
    {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}
