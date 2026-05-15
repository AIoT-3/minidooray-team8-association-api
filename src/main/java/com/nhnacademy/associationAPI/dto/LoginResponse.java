package com.nhnacademy.associationAPI.dto;

import com.nhnacademy.associationAPI.user.User;

public record LoginResponse(
        String userId
) {
    public static LoginResponse from(User user){
        return new LoginResponse(user.getId());
    }
}
