package com.nhnacademy.associationAPI.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String userId) {
        super("존재하지 않는 유저: " + userId);
    }
}
