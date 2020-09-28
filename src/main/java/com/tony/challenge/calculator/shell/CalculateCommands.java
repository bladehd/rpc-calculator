package com.tony.challenge.calculator.shell;

import com.tony.challenge.calculator.service.RPNCalculateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.CommandNotFound;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.List;

/**
 * @author tony.chen
 */
@ShellComponent
public class CalculateCommands {
    @Autowired
    RPNCalculateService calculateService;

    @ShellMethod(value = "Add last two numbers in the stack together", key = "+")
    public String plus(List<String> numbers) {
        validateNumbers(numbers);
        calculateService.pushNumbersToStack(numbers);
        calculateService.plus();
        return calculateService.showStackNumbers();
    }

    @ShellMethod(value = "The second to last number subtract the last number in the stack", key = "-")
    public String minus(List<String> numbers) {
        validateNumbers(numbers);
        calculateService.pushNumbersToStack(numbers);
        calculateService.minus();
        return calculateService.showStackNumbers();
    }

    @ShellMethod(value = "ultiply by the last two numbers together", key = "*")
    public String multiply(List<String> numbers) {
        validateNumbers(numbers);
        calculateService.pushNumbersToStack(numbers);
        calculateService.multiply();
        return calculateService.showStackNumbers();
    }

    @ShellMethod(value = "The second to last number divide by the last number in the stack", key = "/")
    public String divide(List<String> numbers) {
        validateNumbers(numbers);
        calculateService.pushNumbersToStack(numbers);
        calculateService.divide();
        return calculateService.showStackNumbers();
    }

    @ShellMethod(value = "Calculate the square root of last number in the stack", key = "sqrt")
    public String sqrt(List<String> numbers) {
        validateNumbers(numbers);
        calculateService.pushNumbersToStack(numbers);
        calculateService.sqrt();
        return calculateService.showStackNumbers();
    }

    @ShellMethod(value = "Undo last operation", key = "undo")
    public String undo(List<String> numbers) {
        validateNumbers(numbers);
        calculateService.pushNumbersToStack(numbers);
        calculateService.undo();
        return calculateService.showStackNumbers();
    }


    @ShellMethod(value = "Clear calculation stack and operation stack", key = "clear")
    public String clear(List<String> numbers) {
        calculateService.clear();
        return calculateService.showStackNumbers();
    }

    @ShellMethod(value = "Default command, just push all numbers into stack", key = "default")
    public Object defaultMethod(List<String> args) {
        for (String arg : args) {
            if (!StringUtils.isNumeric(arg)) {
                return new CommandNotFound(args);
            }
        }
        calculateService.pushNumbersToStack(args);
        return calculateService.showStackNumbers();
    }

    private void validateNumbers(List<String> numbers) {
        for(String number : numbers){
            if (!StringUtils.isNumeric(number)) {
                throw new IllegalArgumentException("Only support numbers as argument");
            }
        }
    }
}