package com.professor.app.exceptions;

public class InvalidOldPasswordException extends RuntimeException{
    public InvalidOldPasswordException(String message) {
        super(message);
    }
}
