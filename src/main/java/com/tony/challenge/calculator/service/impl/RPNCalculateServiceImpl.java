package com.tony.challenge.calculator.service.impl;

import com.tony.challenge.calculator.exception.CannotUndoException;
import com.tony.challenge.calculator.exception.ParameterNotSufficientException;
import com.tony.challenge.calculator.service.RPNCalculateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

@Service
public class RPNCalculateServiceImpl implements RPNCalculateService {
    private static Stack<BigDecimal> numberStack = new Stack();
    private static Stack<Stack<BigDecimal>> historyStack = new Stack();//TODO Not a good design, waste memory

    @Override
    public BigDecimal plus() {
        saveHistory();
        validateStackSize(2);
        BigDecimal[] twoNumbers = popTopTwoNumber();
        BigDecimal result = twoNumbers[0].add(twoNumbers[1]);
        pushNumberToStack(result);
        return result;
    }

    @Override
    public BigDecimal minus() {
        saveHistory();
        validateStackSize(2);
        BigDecimal[] twoNumbers = popTopTwoNumber();
        BigDecimal result = twoNumbers[1].subtract(twoNumbers[0]);
        pushNumberToStack(result);
        return result;
    }

    @Override
    public BigDecimal multiply() {
        saveHistory();
        validateStackSize(2);
        BigDecimal[] twoNumbers = popTopTwoNumber();
        BigDecimal result = twoNumbers[0].multiply(twoNumbers[1]);
        pushNumberToStack(result);
        return result;
    }

    @Override
    public BigDecimal divide() {
        saveHistory();
        validateStackSize(2);
        BigDecimal[] twoNumbers = popTopTwoNumber();
        BigDecimal result = twoNumbers[1].divide(twoNumbers[0]);
        pushNumberToStack(result);
        return result;
    }

    @Override
    public BigDecimal sqrt() {
        saveHistory();
        validateStackSize(1);
        BigDecimal number = popTopNumber();
        BigDecimal result = number.sqrt(MathContext.DECIMAL64);
        pushNumberToStack(result);
        return result;
    }

    @Override
    public void undo() {
        if (historyStack.size() == 0) {
            throw new CannotUndoException();
        }
        numberStack = historyStack.pop();
    }

    @Override
    public void clear() {
        numberStack.clear();
        historyStack.clear();
    }

    private void saveHistory() {
        //Only support undo 10 times
        if (historyStack.size() == 10) {
            historyStack.remove(9);
        }
        Stack stack = new Stack();
        stack.addAll(numberStack);
        historyStack.push(stack);
    }

    @Override
    public BigDecimal[] getAllNumbersFromStack() {
        return numberStack.toArray(new BigDecimal[0]);
    }

    /**
     * Convert to 10 decimal places
     * should be formatted as plain decimal strings
     *
     * @return
     */
    @Override
    public String showStackNumbers() {
        BigDecimal[] numbers = getAllNumbersFromStack();
        return "stack: " + Arrays.stream(numbers).map(n -> n.setScale(10, RoundingMode.DOWN)).map(n -> n.stripTrailingZeros().toPlainString()).collect(Collectors.joining(" "));
    }

    private BigDecimal[] popTopTwoNumber() {
        BigDecimal first = numberStack.pop();
        BigDecimal second = numberStack.pop();
        return new BigDecimal[]{first, second};
    }

    private BigDecimal popTopNumber() {
        return numberStack.pop();
    }

    @Override
    public void pushNumbersToStack(List<String> numbers) {
        for (String s : numbers) {
            saveHistory();
            BigDecimal n = new BigDecimal(s);
            n.setScale(15, RoundingMode.DOWN);
            pushNumberToStack(n);
        }
    }

    private void pushNumberToStack(BigDecimal number) {
        numberStack.push(number);
    }

    private void validateStackSize(int size) {
        if (numberStack.size() < size) {
            throw new ParameterNotSufficientException(numberStack.toArray(new BigDecimal[]{}));
        }
    }
}
