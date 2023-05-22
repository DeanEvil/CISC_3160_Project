import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class Interpreter {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Interpreter file");
            System.exit(0);
        }

        // Tokenize the input file
        ArrayList<Token> tokens = new ArrayList<>();
        Scanner sc = new Scanner(args[0]);
        Tokenizer.tokenize(sc, tokens);

        // Create a string representation of the tokens
        StringBuilder tokenString = new StringBuilder();
        for (Token token : tokens) {
            tokenString.append(token).append(" ");
        }
        System.out.println("Tokens: " + tokenString);

        // Parse and evaluate the expression
        ExpParser parser = new ExpParser(tokens);
        int result = parser.evaluate();
        System.out.println("Result: " + result);
    }
}

class Token {
    public static final int ID = 0; // Identifier
    public static final int IL = 1; // Integer literal
    public static final int OP = 2; // Operator
    public static final int PM = 3; // Punctuation mark
    public static final int LP = 4; // Left parenthesis
    public static final int RP = 5; // Right parenthesis

    public int type;
    public String lexeme;

    public Token(int type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }

    public String toString() {
        return type + lexeme;
    }
}

class Tokenizer {
    public static void tokenize(Scanner sc, ArrayList<Token> tokens) {
        while (sc.hasNext()) {
            String line = sc.nextLine();
            String[] segs = line.split("\\s+");
            for (String seg : segs) {
                tokenize(seg, tokens);
            }
        }
    }

    public static void tokenize(String str, ArrayList<Token> tokens) {
        int i = 0;
        int e; // ending index of a token
        int n = str.length();

        for (;;) {
            if (i >= n) {
                return;
            }
            switch (str.charAt(i)) {
                case '0' -> {
                    tokens.add(new Token(Token.IL, "0"));
                    if (i < n - 1 && Character.isDigit(str.charAt(i + 1))) {
                        error("Invalid literal: " + str.charAt(i) + str.charAt(i + 1));
                    }
                }
                case '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                    e = extractLiteral(str, i + 1);
                    tokens.add(new Token(Token.IL, str.substring(i, e)));
                    i = e;
                }
                case 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' -> {
                    e = extractIdentifier(str, i + 1);
                    tokens.add(new Token(Token.ID, str.substring(i, e)));
                    i = e;
                }
                case '=', '+', '-', '*' -> {
                    tokens.add(new Token(Token.OP, Character.toString(str.charAt(i))));
                    i++;
                }
                case '(' -> {
                    tokens.add(new Token(Token.LP, "("));
                    i++;
                }
                case ')' -> {
                    tokens.add(new Token(Token.RP, ")"));
                    i++;
                }
                case ';' -> {
                    tokens.add(new Token(Token.PM, ";"));
                    i++;
                }
                default -> error("Unrecognized symbol: " + str.charAt(i));
            }
        }
    }

    static int extractLiteral(String str, int i) {
        while (i < str.length() && Character.isDigit(str.charAt(i))) {
            i++;
        }
        return i;
    }

    static int extractIdentifier(String str, int i) {
        while (i < str.length() && (Character.isLetter(str.charAt(i)) || Character.isDigit(str.charAt(i)))) {
            i++;
        }
        return i;
    }

    static void error(String str) {
        throw new RuntimeException(str);
    }
}

class ExpParser {
    private final ArrayList<Token> tokens;
    private int curIndex;
    private final Stack<Integer> stack;

    public ExpParser(ArrayList<Token> tokens) {
        this.tokens = tokens;
        this.curIndex = 0;
        this.stack = new Stack<>();
    }

    public int evaluate() {
        exp();
        if (currentToken().type != Token.PM) {
            error("Unexpected token: " + currentToken().lexeme);
        }
        return stack.pop();
    }

    private Token currentToken() {
        if (curIndex >= tokens.size()) {
            return new Token(Token.PM, "$");
        }
        return tokens.get(curIndex);
    }

    private void readNextToken() {
        curIndex++;
    }

    private void exp() {
        term();
        expPrime();
    }

    private void expPrime() {
        Token token = currentToken();
        if (token.type == Token.OP) {
            if (token.lexeme.equals("+")) {
                readNextToken();
                term();
                stack.push(stack.pop() + stack.pop());
                expPrime();
            } else if (token.lexeme.equals("-")) {
                readNextToken();
                term();
                int b = stack.pop();
                int a = stack.pop();
                stack.push(a - b);
                expPrime();
            } else {
                error("Unexpected token: " + token.lexeme);
            }
        }
    }

    private void term() {
        factor();
        termPrime();
    }

    private void termPrime() {
        Token token = currentToken();
        if (token.type == Token.OP) {
            if (token.lexeme.equals("*")) {
                readNextToken();
                factor();
                stack.push(stack.pop() * stack.pop());
                termPrime();
            } else if (token.lexeme.equals("/")) {
                readNextToken();
                factor();
                int b = stack.pop();
                int a = stack.pop();
                stack.push(a / b);
                termPrime();
            } else {
                error("Unexpected token: " + token.lexeme);
            }
        }
    }

    private void factor() {
        Token token = currentToken();
        switch (token.type) {
            case Token.LP -> {
                readNextToken();
                exp();
                if (currentToken().type == Token.RP) {
                    readNextToken();
                } else {
                    error("Missing closing parenthesis");
                }
            }
            case Token.IL -> {
                int num = Integer.parseInt(token.lexeme);
                stack.push(num);
                readNextToken();
            }
            default -> error("Unexpected token: " + token.lexeme);
        }
    }

    private void error(String str) {
        throw new RuntimeException("Syntax error: " + str);
    }
}
