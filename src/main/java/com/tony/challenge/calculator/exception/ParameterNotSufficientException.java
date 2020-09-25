package com.tony.challenge.calculator.exception;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * If an operator cannot find a sufficient number of parameters on the stack, a warning is displayed:
 * operator <operator> (position: <pos>): insufficient parameters
 * After displaying the warning, all further processing of the string terminates and the current state of the stack is displayed
 */
public class ParameterNotSufficientException extends RuntimeException {
    private String operator;
    private int pos;
    private BigDecimal[] numbers;

    public ParameterNotSufficientException(BigDecimal[] numbers) {
        this.numbers = numbers;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setPos(int pos) {
        this.pos = pos + 1; //convert to natural number sequence
    }

    @Override
    public String getMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("operator %s (position: %s): insufficient parameters", operator, pos));
        stringBuilder.append("\n");
        stringBuilder.append("stack: ").append(Arrays.stream(numbers).map(n -> n.setScale(10, RoundingMode.DOWN)).map(n -> n.stripTrailingZeros().toPlainString()).collect(Collectors.joining(" ")));
        return stringBuilder.toString();
    }
}
