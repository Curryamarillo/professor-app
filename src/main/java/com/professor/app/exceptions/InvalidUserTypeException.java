package com.professor.app.exceptions;

public class InvalidUserTypeException extends RuntimeException {
    public InvalidUserTypeException(String s) {
        super(s);
    }
}
