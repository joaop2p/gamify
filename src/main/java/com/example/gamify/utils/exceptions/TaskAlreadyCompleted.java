package com.example.gamify.utils.exceptions;

public class TaskAlreadyCompleted extends RuntimeException {
    public TaskAlreadyCompleted(String message) {
        super(message);
    }
}
