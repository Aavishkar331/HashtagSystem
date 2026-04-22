package com.hashtag.exceptions;

public class InvalidPostException extends Exception {
    public InvalidPostException(String message) {
        super("Invalid post: " + message);
    }
}