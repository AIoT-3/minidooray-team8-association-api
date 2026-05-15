package com.nhnacademy.associationAPI.dto;

public record LoginRequest(
        String userId,
        String password
) {}
