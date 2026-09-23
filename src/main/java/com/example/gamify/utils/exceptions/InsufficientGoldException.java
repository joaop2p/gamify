package com.example.gamify.utils.exceptions;

public class InsufficientGoldException extends RuntimeException {
    public InsufficientGoldException(String message) {
        super(message);
    }
}
