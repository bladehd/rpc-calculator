package com.tony.challenge.calculator;

import com.tony.challenge.calculator.exception.CannotUndoException;
import com.tony.challenge.calculator.exception.ParameterNotSufficientException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.shell.Input;
import org.springframework.shell.Shell;


@SpringBootTest
class MultiCommandShellTests {
    @Autowired
    private Shell shell;

    /**
     * Mock shell interaction
     */
    @MockBean(name = "interactiveApplicationRunner")
    private ApplicationRunner applicationRunner;

    @BeforeEach
    void cleanStack() {
        String input = "clear";
        String expected = "stack: ";
        String actualResult = (String) shell.evaluate(new TestInput(input));
        Assertions.assertEquals(expected, actualResult, "Failed to clear the stack");
    }

    @Test()
    void testCannotUndo() {
        String input = "undo";
        Object result = shell.evaluate(new TestInput(input));
        Assertions.assertTrue(result instanceof CannotUndoException);
    }

    private class TestInput implements Input {
        private String text;

        public TestInput(String text) {
            this.text = text;
        }

        @Override
        public String rawText() {
            return this.text;
        }
    }

    @Test
    void testExample1() {
        String input = "5 2";
        String expected = "stack: 5 2";
        String actualResult = (String) shell.evaluate(new TestInput(input));
        Assertions.assertEquals(expected, actualResult);
    }

    @Test
    void testExample2() {
        String input = "2 sqrt";
        String expected = "stack: 1.4142135623";
        String actualResult = (String) shell.evaluate(new TestInput(input));
        Assertions.assertEquals(expected, actualResult);
        input = "clear 9 sqrt";
        expected = "stack: 3";
        actualResult = (String) shell.evaluate(new TestInput(input));
        Assertions.assertEquals(expected, actualResult);
    }

    @Test
    void testExample3() {
        String input = "5 2 -";
        String expected = "stack: 3";
        String actualResult = (String) shell.evaluate(new TestInput(input));
        Assertions.assertEquals(expected, actualResult);
        input = "3 -";
        expected = "stack: 0";
        actualResult = (String) shell.evaluate(new TestInput(input));
        Assertions.assertEquals(expected, actualResult);
        input = "clear";
        expected = "stack: ";
        actualResult = (String) shell.evaluate(new TestInput(input));
        Assertions.assertEquals(expected, actualResult);
    }

    @Test
    void testExample4() {
        String input = "5 4 3 2";
        String expected = "stack: 5 4 3 2";
        String actualResult = (String) shell.evaluate(new TestInput(input));
        Assertions.assertEquals(expected, actualResult);
        input = "undo undo *";
        expected = "stack: 20";
        actualResult = (String) shell.evaluate(new TestInput(input));
        Assertions.assertEquals(expected, actualResult);
        input = "5 *";
        expected = "stack: 100";
        actualResult = (String) shell.evaluate(new TestInput(input));
        Assertions.assertEquals(expected, actualResult);
        input = "undo";
        expected = "stack: 20 5";
        actualResult = (String) shell.evaluate(new TestInput(input));
        Assertions.assertEquals(expected, actualResult);
    }

    @Test
    void testExample5() {
        String input = "7 12 2 /";
        String expected = "stack: 7 6";
        String actualResult = (String) shell.evaluate(new TestInput(input));
        Assertions.assertEquals(expected, actualResult);
        input = "*";
        expected = "stack: 42";
        actualResult = (String) shell.evaluate(new TestInput(input));
        Assertions.assertEquals(expected, actualResult);
        input = "4 /";
        expected = "stack: 10.5";
        actualResult = (String) shell.evaluate(new TestInput(input));
        Assertions.assertEquals(expected, actualResult);
    }

    @Test
    void testExample6() {
        String input = "1 2 3 4 5";
        String expected = "stack: 1 2 3 4 5";
        String actualResult = (String) shell.evaluate(new TestInput(input));
        Assertions.assertEquals(expected, actualResult);
        input = "*";
        expected = "stack: 1 2 3 20";
        actualResult = (String) shell.evaluate(new TestInput(input));
        Assertions.assertEquals(expected, actualResult);
        input = "clear 3 4 -";
        expected = "stack: -1";
        actualResult = (String) shell.evaluate(new TestInput(input));
        Assertions.assertEquals(expected, actualResult);
    }

    @Test
    void testExample7() {
        String input = "1 2 3 4 5";
        String expected = "stack: 1 2 3 4 5";
        String actualResult = (String) shell.evaluate(new TestInput(input));
        Assertions.assertEquals(expected, actualResult);
        input = "* * * *";
        expected = "stack: 120";
        actualResult = (String) shell.evaluate(new TestInput(input));
        Assertions.assertEquals(expected, actualResult);
    }

    @Test
    void testExample8() {
        String input = "1 2 3 * 5 + * * 6 5";
        String expected = "operator * (position: 15): insufficient parameters\nstack: 11";
        Object actualResult = shell.evaluate(new TestInput(input));
        Assertions.assertTrue(actualResult instanceof ParameterNotSufficientException);
        Assertions.assertEquals(expected, ((ParameterNotSufficientException) actualResult).getMessage());
    }
}
