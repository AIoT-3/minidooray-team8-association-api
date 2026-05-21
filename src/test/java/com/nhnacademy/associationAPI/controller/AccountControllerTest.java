package com.nhnacademy.associationAPI.controller;

import com.nhnacademy.associationAPI.config.WebSecurityConfig;
import com.nhnacademy.associationAPI.dto.*;
import com.nhnacademy.associationAPI.exception.LoginFailedException;
import com.nhnacademy.associationAPI.exception.UserAlreadyExistsException;
import com.nhnacademy.associationAPI.exception.UserNotFoundException;
import com.nhnacademy.associationAPI.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = AccountController.class,
includeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfig.class)})
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("signup 성공 테스트")
    void signupSuccess() throws Exception {
        SignupRequest request = new SignupRequest("testId1234", "test@email.com", "testPw1234");
        SignupResponse response = new SignupResponse("testId1234", "JOIN");

        when(userService.signup(any(SignupRequest.class))).thenReturn(response);

        mockMvc.perform(post("/accounts/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("testId1234"))
                .andExpect(jsonPath("$.status").value("JOIN"));
    }

    @Test
    @DisplayName("signup 실패 테스트")
    void signupFailed() throws Exception{
        SignupRequest request = new SignupRequest("testId1234", "test@email.com", "testPw1234");

        when(userService.signup(any(SignupRequest.class))).thenThrow(new UserAlreadyExistsException(request.id()));

        mockMvc.perform(post("/accounts/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(409))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("이미 존재하는 유저: testId1234"))
                .andExpect(jsonPath("$.path").value("/accounts/signup"));
    }

    @Test
    @DisplayName("login 성공 테스트")
    void loginSuccess() throws Exception{
        LoginRequest request = new LoginRequest("testId1234", "testPw1234");
        LoginResponse response = new LoginResponse("testId1234");

        when(userService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/accounts/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("testId1234"));
    }

    @Test
    @DisplayName("login 실패 테스트")
    void loginFailed() throws Exception{
        LoginRequest request = new LoginRequest("testId1234", "testPw1234");

        when(userService.login(any(LoginRequest.class))).thenThrow(new LoginFailedException("로그인 실패: 존재하지 않는 아이디"));

        mockMvc.perform(post("/accounts/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("로그인 실패: 존재하지 않는 아이디"))
                .andExpect(jsonPath("$.path").value("/accounts/login"));
    }

    @Test
    @DisplayName("updateStatus 성공 테스트")
    void updateStatusSuccess() throws Exception{
        UserStatusUpdateRequest request = new UserStatusUpdateRequest("DORMANT");

        mockMvc.perform(put("/accounts/users/testId1234/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("updateStatus 실패 테스트")
    void updateStatusFailed() throws Exception{
        UserStatusUpdateRequest request = new UserStatusUpdateRequest("DORMANT");

        doThrow(new IllegalArgumentException())
                .when(userService).updateStatus(anyString(), any(UserStatusUpdateRequest.class));

        mockMvc.perform(put("/accounts/users/testId1234/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("입력 형식 오류"))
                .andExpect(jsonPath("$.path").value("/accounts/users/testId1234/status"));
    }

    @Test
    @DisplayName("getUser 성공 테스트")
    void getUserSuccess() throws Exception{
        UserDto response = new UserDto("testId1234", "test@email.com", "JOIN");
        when(userService.getUser(anyString())).thenReturn(response);

        mockMvc.perform(get("/accounts/users/testId1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("testId1234"))
                .andExpect(jsonPath("$.email").value("test@email.com"))
                .andExpect(jsonPath("$.status").value("JOIN"));
    }

    @Test
    @DisplayName("getUser 실패 테스트")
    void getUserFailed() throws Exception{
        when(userService.getUser(anyString())).thenThrow(new UserNotFoundException("testId1234"));

        mockMvc.perform(get("/accounts/users/testId1234"))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("존재하지 않는 유저: testId1234"))
                .andExpect(jsonPath("$.path").value("/accounts/users/testId1234"));
    }

    @Test
    @DisplayName("회원가입 validation 테스트1")
    void signupValidation1() throws Exception{
        SignupRequest request = new SignupRequest("invalidId", "test@email.com", "testPw1234");

        mockMvc.perform(post("/accounts/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("입력 형식 오류"))
                .andExpect(jsonPath("$.path").value("/accounts/signup"));
    }

    @Test
    @DisplayName("회원가입 validation 테스트2")
    void signupValidation2() throws Exception{
        SignupRequest request = new SignupRequest("testId1234", "invalidEmail", "testPw1234");

        mockMvc.perform(post("/accounts/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("입력 형식 오류"))
                .andExpect(jsonPath("$.path").value("/accounts/signup"));
    }

    @Test
    @DisplayName("회원가입 validation 테스트3")
    void signupValidation3() throws Exception{
        SignupRequest request = new SignupRequest("testId1234", "test@email.com", "invalidPw");

        mockMvc.perform(post("/accounts/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("입력 형식 오류"))
                .andExpect(jsonPath("$.path").value("/accounts/signup"));
    }
}
