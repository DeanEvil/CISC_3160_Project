import java.util.Stack;

public class ExpParser {
    static String str;
    static int curIndex;
    static Stack<Integer> stack;

    static char currentToken() {
        if (curIndex >= str.length()) {
            return '$';
        }
        return str.charAt(curIndex);
    }

    static void readNextToken() {
        curIndex++;
    }

    static void exp() {
        term();
        expPrime();
    }

    static void expPrime() {
        char token = currentToken();
        switch (token) {
            case '+':
                readNextToken();
                term();
                stack.push(stack.pop() + stack.pop());
                expPrime();
                break;
            case '-':
                readNextToken();
                term();
                int b = stack.pop();
                int a = stack.pop();
                stack.push(a - b);
                expPrime();
                break;
        }
    }

    static void term() {
        factor();
        termPrime();
    }

    static void termPrime() {
        char token = currentToken();
        switch (token) {
            case '*':
                readNextToken();
                factor();
                stack.push(stack.pop() * stack.pop());
                termPrime();
                break;
            case '/':
                readNextToken();
                factor();
                int b = stack.pop();
                int a = stack.pop();
                stack.push(a / b);
                termPrime();
                break;
        }
    }

    static void factor() {
        char token = currentToken();
        switch (token) {
            case '(':
                readNextToken();
                exp();
                if (currentToken() == ')') {
                    readNextToken();
                } else {
                    error();
                }
                break;
            default:
                if (Character.isDigit(token)) {
                    int num = Character.getNumericValue(token);
                    stack.push(num);
                    readNextToken();
                } else {
                    error();
                }
        }
    }

    static void error() {
        throw new RuntimeException("syntax error");
    }

    public static void main(String[] args) {
        str = "(2+3)*4";
        curIndex = 0;
        stack = new Stack<>();
        exp();
        if (currentToken() != '$') {
            error();
        } else {
            System.out.println("valid");
            System.out.println("Result: " + stack.pop());
        }
    }
}

