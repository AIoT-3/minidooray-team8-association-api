package com.nhnacademy.associationAPI.controller;

import com.nhnacademy.associationAPI.dto.*;
import com.nhnacademy.associationAPI.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/accounts")
@RestController
public class AccountController {
    private final UserService userService;

    public AccountController(final UserService userService){
        this.userService = userService;
    }

    @PostMapping("/signup")
    public SignupResponse signup(@Valid @RequestBody SignupRequest request){
        return userService.signup(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request){
        return userService.login(request);
    }

    @GetMapping("/users/{userId}")
    public UserDto getUser(@PathVariable String userId){
        return userService.getUser(userId);
    }

    @PutMapping("/users/{userId}/status")
    public ResponseEntity<Void> updateStatus(@RequestBody UserStatusUpdateRequest request,
                                       @PathVariable String userId){
        userService.updateStatus(userId, request);
        return ResponseEntity.noContent().build();
    }
}
