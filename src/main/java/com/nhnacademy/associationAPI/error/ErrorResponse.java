package com.nhnacademy.associationAPI.error;

public record ErrorResponse (
        int status,
        String message,
        String path
){}