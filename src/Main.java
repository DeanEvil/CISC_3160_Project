import java.util.*;

class Interpreter {
    private Map<String, Integer> variables;

    public Interpreter() {
        variables = new HashMap<>();
    }

    public void interpret(String program) {
        List<String> statements = parseStatements(program);
        if (statements == null) {
            System.out.println("Syntax error!");
            return;
        }

        for (String statement : statements) {
            if (!executeStatement(statement)) {
                System.out.println("Error executing statement: " + statement);
                return;
            }
        }

        printVariables();
    }

    private List<String> parseStatements(String program) {
        List<String> statements = new ArrayList<>();
        String[] lines = program.split(";");

        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty()) {
                statements.add(line);
            }
        }

        if (statements.isEmpty()) {
            return null;
        }

        return statements;
    }

    private boolean executeStatement(String statement) {
        String[] parts = statement.split("=");
        if (parts.length != 2) {
            return false;
        }

        String identifier = parts[0].trim();
        String expression = parts[1].trim();

        if (!isValidIdentifier(identifier)) {
            return false;
        }

        int value = evaluateExpression(expression);
        if (value == Integer.MIN_VALUE) {
            return false;
        }

        variables.put(identifier, value);
        return true;
    }

    private int evaluateExpression(String expression) {
        Stack<Integer> stack = new Stack<>();
        Stack<Character> operatorStack = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);

            if (ch == ' ') {
                continue;
            }

            if (Character.isDigit(ch)) {
                int num = ch - '0';
                while (i + 1 < expression.length() && Character.isDigit(expression.charAt(i + 1))) {
                    num = num * 10 + (expression.charAt(i + 1) - '0');
                    i++;
                }
                stack.push(num);
            } else if (ch == '(') {
                operatorStack.push(ch);
            } else if (ch == ')') {
                while (!operatorStack.isEmpty() && operatorStack.peek() != '(') {
                    if (!applyOperator(operatorStack.pop(), stack)) {
                        return Integer.MIN_VALUE;
                    }
                }

                if (operatorStack.isEmpty() || operatorStack.peek() != '(') {
                    return Integer.MIN_VALUE;
                } else {
                    operatorStack.pop();
                }
            } else if (isOperator(ch)) {
                while (!operatorStack.isEmpty() && precedence(operatorStack.peek()) >= precedence(ch)) {
                    if (!applyOperator(operatorStack.pop(), stack)) {
                        return Integer.MIN_VALUE;
                    }
                }

                operatorStack.push(ch);
            } else {
                StringBuilder identifier = new StringBuilder();
                identifier.append(ch);
                while (i + 1 < expression.length() && isValidIdentifierCharacter(expression.charAt(i + 1))) {
                    identifier.append(expression.charAt(i + 1));
                    i++;
                }

                String var = identifier.toString().trim();
                if (!variables.containsKey(var)) {
                    System.out.println("Variable not initialized: " + var);
                    return Integer.MIN_VALUE;
                }

                stack.push(variables.get(var));
            }
        }

        while (!operatorStack.isEmpty()) {
            if (!applyOperator(operatorStack.pop(), stack)) {
                return Integer.MIN_VALUE;
            }
        }

        if (stack.size() != 1) {
            return Integer.MIN_VALUE;
        }

        return stack.pop();
    }

    private boolean applyOperator(char operator, Stack<Integer> stack) {
        if (stack.isEmpty()) {
            return false;
        }

        int rightOperand = stack.pop();
        if (stack.isEmpty()) {
            return false;
        }

        int leftOperand = stack.pop();
        int result;

        switch (operator) {
            case '+':
                result = leftOperand + rightOperand;
                break;
            case '-':
                result = leftOperand - rightOperand;
                break;
            case '*':
                result = leftOperand * rightOperand;
                break;
            default:
                return false;
        }

        stack.push(result);
        return true;
    }

    private boolean isValidIdentifier(String identifier) {
        if (identifier.isEmpty()) {
            return false;
        }

        char firstChar = identifier.charAt(0);
        if (!Character.isLetter(firstChar) && firstChar != '_') {
            return false;
        }

        for (int i = 1; i < identifier.length(); i++) {
            char ch = identifier.charAt(i);
            if (!isValidIdentifierCharacter(ch)) {
                return false;
            }
        }

        return true;
    }

    private boolean isValidIdentifierCharacter(char ch) {
        return Character.isLetter(ch) || Character.isDigit(ch) || ch == '_';
    }

    private boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*';
    }

    private int precedence(char operator) {
        switch (operator) {
            case '+':
            case '-':
                return 1;
            case '*':
                return 2;
            default:
                return -1;
        }
    }

    private void printVariables() {
        for (Map.Entry<String, Integer> entry : variables.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
    }
}

public class Main {
    public static void main(String[] args) {
        String program = "x = 5; y = 3 + x * 2; z = x - y;";
        Interpreter interpreter = new Interpreter();
        interpreter.interpret(program);
    }
}

