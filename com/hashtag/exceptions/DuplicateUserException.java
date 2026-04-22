package com.hashtag.exceptions;

public class DuplicateUserException extends Exception {
    public DuplicateUserException(String username) {
        super("User already exists: " + username);
    }
}