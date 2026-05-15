package com.nhnacademy.associationAPI.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@Valid
public record LoginRequest(
        @NotBlank
        String userId,
        @NotBlank
        String password
) {}
