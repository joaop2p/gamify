package com.example.gamify.utils.exceptions;

public class ItemNotOwnedException extends RuntimeException {
    public ItemNotOwnedException(String message) {
        super(message);
    }
}
