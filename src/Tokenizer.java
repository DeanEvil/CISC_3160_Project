import java.util.*;

public class Tokenizer {
    public static void tokenize(Scanner sc, ArrayList<Token> tokens) {
        while (sc.hasNext()) {
            String line = sc.nextLine();
            String[] segs = line.split(" \t");
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
                case '0':
                    tokens.add(new Token(Token.IL, "0"));
                    if (i < n - 1 && Character.isDigit(str.charAt(i + 1))) {
                        error("invalid literal");
                    }
                    break;
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    e = extractLiteral(str, i + 1);
                    tokens.add(new Token(Token.IL, str.substring(i, e)));
                    i = e;
                    break;

                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                    e = extractIdentifier(str, i + 1);
                    tokens.add(new Token(Token.ID, str.substring(i, e)));
                    i = e;
                    break;

                case '=':
                case '+':
                case '-':
                case '*':
                    tokens.add(new Token(Token.OP, Character.toString(str.charAt(i))));
                    i++;
                    break;
                case '(':
                    tokens.add(new Token(Token.LP, "("));
                    i++;
                    break;
                case ')':
                    tokens.add(new Token(Token.RP, ")"));
                    i++;
                    break;
                case ';':
                    tokens.add(new Token(Token.PM, ";"));
                    i++;
                    break;
                default:
                    error("unrecognized symbol: " + str);
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

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Tokenizer file");
            System.exit(0);
        }
        ArrayList<Token> tokens = new ArrayList<>();
        Scanner sc = new Scanner(args[0]);
        tokenize(sc, tokens);
        System.out.println(tokens);
    }
}

class Token {
    public static int ID = 0; // Identifier
    public static int IL = 1; // Integer literal
    public static int OP = 2; // operator
    public static int PM = 3; // punctuation mark
    public static int LP = 4; // left parenthesis
    public static int RP = 5; // right parenthesis

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

