package com.nhnacademy.associationAPI.dto;

public record SignupRequest(
        String id,
        String email,
        String password
) {}
