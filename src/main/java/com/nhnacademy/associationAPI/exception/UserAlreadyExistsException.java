package com.nhnacademy.associationAPI.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String userId) {
        super("이미 존재하는 유저: " + userId);
    }
}
