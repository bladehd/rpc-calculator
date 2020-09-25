package com.tony.challenge.calculator.exception;

public class CannotUndoException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Cannot undo anymore";
    }
}
