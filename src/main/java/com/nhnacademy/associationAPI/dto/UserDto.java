package com.nhnacademy.associationAPI.dto;

import com.nhnacademy.associationAPI.user.User;

public record UserDto(
        String userId,
        String email,
        String status
) {
    public static UserDto from(User user){
        return new UserDto(user.getId(), user.getEmail(), user.getStatus().name());
    }
}
