package com.nhnacademy.associationAPI.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Valid
public record SignupRequest(
        @NotBlank
        @Pattern(regexp = ".*[A-Za-z].*")
        @Pattern(regexp = ".*\\d.*")
        @Size(max = 50, min = 8)
        String id,

        @NotBlank
        @Email
        @Size(max = 100)
        String email,

        @NotBlank
        @Pattern(regexp = ".*[A-Za-z].*")
        @Pattern(regexp = ".*\\d.*")
        @Size(max = 255, min = 8)
        String password
) {}
