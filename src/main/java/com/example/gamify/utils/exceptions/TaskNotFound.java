package com.example.gamify.utils.exceptions;

public class TaskNotFound extends RuntimeException {
    public TaskNotFound(String message) {
        super(message);
    }
}
