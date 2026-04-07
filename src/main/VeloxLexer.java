package main;

import java.util.*;
import java.util.regex.*;

public class VeloxLexer {

    private static final Set<String> KEYWORDS = new HashSet<>(
            Arrays.asList("CAST", "SURGE", "INVERT", "LOCK", "BOOST", "NULLIFY", "PHASE")
        );

    // Token rules
    private static final Object[][] TOKEN_RULES = {
            // 1. Comments - ignored
            { "//[^\n]*", null },
            // 2. End-of-statement marker ##
            { "##", TokenType.END },
            // 3. SPEED - number followed by a valid speed unit (no space)
            // [+\-]? [0-9]+ (.[0-9]+)? then unit
            { "[+\\-]?[0-9]+(?:\\.[0-9]+)?(?:kms|mps|warp|swift|crawl)",
                    TokenType.SPEED },
            // 4. NUMBER - int or decimal
            { "[+\\-]?[0-9]+(?:\\.[0-9]+)?", TokenType.NUMBER },
            // 5. Operators
            { "[+\\-*/^~]", TokenType.OPERATOR },
            // 6. Separators
            { "[(),:;]", TokenType.SEPARATOR },
            // 7. Identifier / Keyword (resolved by value)
            { "[A-Za-z_][A-Za-z0-9_]*", TokenType.IDENTIFIER },
    };

    // Pre-compile all patterns
    private static final List<Object[]> COMPILED;
    static {
        COMPILED = new ArrayList<>();
        for (Object[] rule : TOKEN_RULES) {
            COMPILED.add(new Object[] {
                    Pattern.compile("^(" + rule[0] + ")"),
                    rule[1]
            });
        }
    }

    public List<Token> tokenizeLine(String line, int lineNum) {
        List<Token> tokens = new ArrayList<>();
        String rem = line;

        while (!rem.isEmpty()) {
            // skip whitespace
            if (Character.isWhitespace(rem.charAt(0))) {
                rem = rem.substring(1);
                continue;
            }

            boolean matched = false;
            for (Object[] rule : COMPILED) {
                Pattern pat = (Pattern) rule[0];
                TokenType tt = (TokenType) rule[1];
                Matcher m = pat.matcher(rem);

                if (m.find()) {
                    String val = m.group(1);
                    if (tt != null) {
                        // Resolve IDENTIFIER -> KEYWORD if needed
                        if (tt == TokenType.IDENTIFIER && KEYWORDS.contains(val))
                            tt = TokenType.KEYWORD;
                        tokens.add(new Token(tt, val, lineNum));
                    }
                    rem = rem.substring(val.length());
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                tokens.add(new Token(TokenType.UNKNOWN,
                        String.valueOf(rem.charAt(0)), lineNum));
                rem = rem.substring(1);
            }
        }
        return tokens;
    }


    public void validateStructure(List<Token> all) {
        System.out.println("\n--- Structural Validation ---");
        boolean inStmt = false;
        boolean startsKW = false;
        int stmtCount = 0;
        int stmtLine = -1;
        int errors = 0;

        for (Token t : all) {
            if (!inStmt && t.type != TokenType.END) {
                inStmt = true;
                startsKW = (t.type == TokenType.KEYWORD);
                stmtLine = t.line;
            }
            if (t.type == TokenType.END && inStmt) {
                stmtCount++;
                if (startsKW) {
                    System.out.printf("  [OK]    Stmt #%d (line %d): starts with KEYWORD%n",
                            stmtCount, stmtLine);
                } else {
                    System.out.printf("  [ERROR] Stmt #%d (line %d): does NOT start with KEYWORD!%n",
                            stmtCount, stmtLine);
                    errors++;
                }
                inStmt = false;
            }
        }
        if (inStmt) {
            System.out.printf("  [ERROR] Unclosed statement at line %d (missing ##)%n", stmtLine);
            errors++;
        }
        System.out.printf("  Statements: %d  |  Errors: %d%n", stmtCount, errors);
    }

}
