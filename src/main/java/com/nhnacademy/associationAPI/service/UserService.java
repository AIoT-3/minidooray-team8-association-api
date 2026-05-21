package com.nhnacademy.associationAPI.service;

import com.nhnacademy.associationAPI.dto.*;

public interface UserService {
    SignupResponse signup(SignupRequest request);
    LoginResponse login(LoginRequest request);
    void updateStatus(String userId, UserStatusUpdateRequest request);
    UserDto getUser(String userId);
}
