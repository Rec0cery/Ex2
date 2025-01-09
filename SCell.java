import java.util.Stack;

public class SCell implements Cell
{
    private String line;
    private int type;
    private int order;

    public SCell(String s)
    {
        setData(s);
        this.order = 0;
    }
    @Override
    public String getData() {
        return line;
    }
    @Override
    public void setData(String s)
    {
        this.line = s;
        this.type = TypeDerminator(s);
    }
    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int t) {
        this.type = t;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public void setOrder(int t) {
        this.order = t;
    }

    @Override
    public String toString() {
        return getData();
    }
    
    private int TypeDerminator(String data)
    {
        if (data == null || data.isEmpty()) {
            return Ex2Utils.TEXT;
        }
        if (isNumber(data)) {
            return Ex2Utils.NUMBER;
        }
        if (isFormula(data)) {
            return Ex2Utils.FORM;
        }
        return Ex2Utils.TEXT; // Default to TEXT if none of the above matches
    }

    /**
     * Checks if the given string is a valid number.
     *
     * @param text The string to check.
     * @return True if the string represents a number, false otherwise.
     */
    private boolean isNumber(String text) {
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static int OperatorPrecedence(char operator)
    {
        if (operator == '+' || operator == '-') {
            return 1;
        } else if (operator == '*' || operator == '/') {
            return 2;
        }
        return -1;
    }
    private boolean hasBalancedParentheses(String formula) 
    {
        int balance = 0;
        for (char c : formula.toCharArray()) {
            if (c == '(') {
                balance++;
            } else if (c == ')') {
                balance--;
                if (balance < 0) {
                    return false;
                }
            }
        }
        return balance == 0;
    }
    private boolean isFormula(String text) {
        if (text == null || !text.startsWith("=")) {
            return false;
        }
        String formula = text.substring(1); // Remove '=' for validation
        return formula.matches("[A-Za-z0-9()+\\-*/.]+") && hasBalancedParentheses(formula);
    }

    private static boolean isOperator(char c)
    {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

  
    
  
    public static Double computeFormulaValue(String formula)
    {
        if (formula == null || !formula.startsWith("=")) 
        {
            return null; 
        }

        String expression = formula.substring(1); 

        try {
            return evaluateExpression(expression);
        } catch (Exception e) {
            return null; 
        }
    }


    private static Double evaluateExpression(String expression) {
        Stack<Double> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (Character.isDigit(c) || c == '.') {
                StringBuilder sb = new StringBuilder();
                while (i < expression.length() &&
                        (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    sb.append(expression.charAt(i));
                    i++;
                }
                i--; 
                values.push(Double.parseDouble(sb.toString()));
            }
      
            else if (c == '(') {
                operators.push(c);
            }
            
            else if (c == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    values.push(OperatorOn(operators.pop(), values.pop(), values.pop()));
                }
                operators.pop(); 
            }
            // If the current character is an operator
            else if (isOperator(c)) {
                while (!operators.isEmpty() && OperatorPrecedence(c) <= OperatorPrecedence(operators.peek())) {
                    values.push(OperatorOn(operators.pop(), values.pop(), values.pop()));
                }
                operators.push(c);
            }
        }

        // Perform remaining operations in the stack
        while (!operators.isEmpty()) {
            values.push(OperatorOn(operators.pop(), values.pop(), values.pop()));
        }

        return values.pop(); // The final result is the last value in the stack
    }

    public boolean DoesValidEntry()
    {
        if(this.type==1||this.type==2||this.type==3) return true;
        return false;
    }
    private static Double OperatorOn(char operator, Double b, Double a)
    {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    throw new ArithmeticException("Divided by zero");
                }
                return a / b;
            default:
                throw new IllegalArgumentException("Invalid operator " + operator);
        }
    }
}
