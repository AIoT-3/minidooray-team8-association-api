package com.nhnacademy.associationAPI.dto;

import com.nhnacademy.associationAPI.user.User;

public record SignupResponse(
        String id,
        String status
) {
    public static SignupResponse from(User user){
        return new SignupResponse(user.getId(), user.getStatus().name());
    }
}
