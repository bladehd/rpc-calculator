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

    @ShellMethod(value = "Add two integers together.", key = "+")
    public String plus(List<String> numbers) {
        validateNumbers(numbers);
        calculateService.pushNumbersToStack(numbers);
        calculateService.plus();
        return calculateService.showStackNumbers();
    }

    @ShellMethod(value = "Add two integers together.", key = "-")
    public String minus(List<String> numbers) {
        validateNumbers(numbers);
        calculateService.pushNumbersToStack(numbers);
        calculateService.minus();
        return calculateService.showStackNumbers();
    }

    @ShellMethod(value = "Add two integers together.", key = "*")
    public String multiply(List<String> numbers) {
        validateNumbers(numbers);
        calculateService.pushNumbersToStack(numbers);
        calculateService.multiply();
        return calculateService.showStackNumbers();
    }

    @ShellMethod(value = "Add two integers together.", key = "/")
    public String divide(List<String> numbers) {
        validateNumbers(numbers);
        calculateService.pushNumbersToStack(numbers);
        calculateService.divide();
        return calculateService.showStackNumbers();
    }

    @ShellMethod(value = "Add two integers together.", key = "sqrt")
    public String sqrt(List<String> numbers) {
        validateNumbers(numbers);
        calculateService.pushNumbersToStack(numbers);
        calculateService.sqrt();
        return calculateService.showStackNumbers();
    }

    @ShellMethod(value = "Add two integers together.", key = "undo")
    public String undo(List<String> numbers) {
        validateNumbers(numbers);
        calculateService.pushNumbersToStack(numbers);
        calculateService.undo();
        return calculateService.showStackNumbers();
    }


    @ShellMethod(value = "Add two integers together.", key = "clear")
    public String clear(List<String> numbers) {
        calculateService.clear();
        return calculateService.showStackNumbers();
    }

    @ShellMethod(value = "Add two integers together.", key = "default")
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