package com.nhnacademy.associationAPI.dto;

public record UserDto(
        String userId,
        String email,
        String password,
        String status
) {}
