package com.example.gamify.utils.exceptions;

public class MissionAlreadyCompleted extends RuntimeException {
    public MissionAlreadyCompleted(String message) {
        super(message);
    }
}
