package com.tony.challenge.calculator.service;

import org.springframework.shell.standard.ShellMethod;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author tony.chen
 */
public interface RPNCalculateService {

    /**
     * perform on the top two items from the stack
     * @return
     */
    BigDecimal plus();

    /**
     * perform on the top two items from the stack
     * @return
     */
    BigDecimal minus();

    /**
     * perform on the top two items from the stack
     * @return
     */
    BigDecimal multiply();

    /**
     * perform on the top two items from the stack
     * @return
     */
    BigDecimal divide();

    /**
     * Performs a square root on the top item from the stack
     * @param
     */
    BigDecimal sqrt();

    /**
     * The ‘undo’ operator undoes the previous operation. “undo undo” will undo the previo us two operations
     * Only support undo 10 times to avoid oom
     */
    void undo();

    /**
     * removes all items from the stack
     */
    void clear();

    BigDecimal[] getAllNumbersFromStack();

    void pushNumbersToStack(List<String> numbers);

    String showStackNumbers();
}
