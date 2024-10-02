package com.example.simplecalc;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {


     TextView inputTextView;
     TextView resultTextView;
     StringBuilder inputExpression;
    String buttonText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputTextView = findViewById(R.id.input);
        resultTextView = findViewById(R.id.output);
        inputExpression = new StringBuilder();
        setupButtonListeners();
    }
    public void setupButtonListeners() {
        int[] buttonIds = {
                R.id.btn_zero, R.id.btn_one, R.id.btn_two, R.id.btn_three, R.id.btn_four,
                R.id.btn_five, R.id.btn_six, R.id.btn_seven, R.id.btn_eight, R.id.btn_nine, R.id.btn_dot,
                R.id.btn_sum, R.id.btn_sub, R.id.btn_mul, R.id.btn_div,
                R.id.btn_right_bracket, R.id.btn_left_bracket, R.id.btn_clear,R.id.btn_delete, R.id.btn_equal
        };

        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button button = (Button) v;
                    buttonText = button.getText().toString();

                    switch (buttonText) {
                        case "=":
                            calculateResult();
                            break;
                        case "C":
                            clearInput();
                            break;
                        case "⌫":
                            deleteLastChar();
                            break;
                        default:
                            appendInput(buttonText);
                            break;
                    }
                }
            });
        }

    }

    public void appendInput(String value) {
        if (value.equals(".") && inputExpression.length() > 0) {
            char lastChar = inputExpression.charAt(inputExpression.length() - 1);
            if (Character.isDigit(lastChar) && !inputExpression.toString().contains(".")) {
                inputExpression.append(value);
            }
        } else if (Character.isDigit(value.charAt(0)) || isOperator(value.charAt(0)) || value.equals(".")) {
            inputExpression.append(value);
        }
        else if (value.equals("(")) {
            if (inputExpression.length() == 0 ||
                    (!inputExpression.toString().matches(".*[0-9]") && !inputExpression.toString().endsWith(")"))) {
                inputExpression.append(value);
            }
        } else if (value.equals(")")) {
            int leftBrackets = 0;
            int rightBrackets = 0;
            for (int i = 0; i < inputExpression.length(); i++) {
                if (inputExpression.charAt(i) == '(') leftBrackets++;
                if (inputExpression.charAt(i) == ')') rightBrackets++;
            }
            if (leftBrackets > rightBrackets) {
                inputExpression.append(value);
            }
        } else {
            inputExpression.append(value);
        }
        inputTextView.setText(inputExpression.toString());


    }

    @SuppressLint("SetTextI18n")
    public void calculateResult() {
        try {
            double result = evaluateExpression(inputExpression.toString());
            resultTextView.setText(String.valueOf(result));
        } catch (Exception e) {
            resultTextView.setText("Error");
        }
    }

    public void clearInput() {
        inputExpression.setLength(0);
        inputTextView.setText("");
        resultTextView.setText("");
    }
    public void deleteLastChar() {
        int length = inputExpression.length();
        if (length > 0) {
            inputExpression.deleteCharAt(length - 1);
            inputTextView.setText(inputExpression.toString());
}
    }

    public double evaluateExpression(String expression) throws Exception {
        return evaluatePostfix(convertPostfix(expression));
    }

   public  String convertPostfix(String expression) throws Exception {
        Stack<Character> operators = new Stack<>();
        StringBuilder output = new StringBuilder();
        char[] tokens = expression.toCharArray();

        for (char token : tokens) {
            if (Character.isDigit(token) || token == '.') {
                output.append(token);
            } else if (token == '(') {
                operators.push(token);
            } else if (token == ')') {
                output.append(' ');
                while (!operators.isEmpty() && operators.peek() != '(') {
                    output.append(operators.pop()).append(' ');
                }
                operators.pop();
            } else if (isOperator(token)) {
                output.append(' ');
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(token)) {
                    output.append(operators.pop()).append(' ');
                }
                operators.push(token);
            }
        }

        while (!operators.isEmpty()) {
            output.append(' ').append(operators.pop());
        }

        return output.toString();
    }

     double evaluatePostfix(String postfix) throws Exception {
        Stack<Double> stack = new Stack<>();
        String[] tokens = postfix.split("\\s");

        for (String token : tokens) {
            if (token.isEmpty()) continue;
            if (isOperator(token.charAt(0)) && token.length() == 1) {
                double b = stack.pop();
                double a = stack.pop();
                switch (token.charAt(0)) {
                    case '+':
                        stack.push(a + b);
                        break;
                    case '-':
                        stack.push(a - b);
                        break;
                    case '*':
                        stack.push(a * b);
                        break;
                    case '/':
                        stack.push(a / b);
                        break;
                }
            } else {
                stack.push(Double.parseDouble(token));
            }
        }

        return stack.pop();
    }

     boolean isOperator(char token) {
        return token == '+' || token == '-' || token == '*' || token == '/';
    }

     int precedence(char operator) {
        switch (operator) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
        }
        return -1;
    }
}
