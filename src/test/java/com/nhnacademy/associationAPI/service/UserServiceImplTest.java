package com.nhnacademy.associationAPI.service;

import com.nhnacademy.associationAPI.dto.*;
import com.nhnacademy.associationAPI.exception.LoginFailedException;
import com.nhnacademy.associationAPI.exception.UserAlreadyExistsException;
import com.nhnacademy.associationAPI.exception.UserNotFoundException;
import com.nhnacademy.associationAPI.repository.UserRepository;
import com.nhnacademy.associationAPI.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setup(){
        user = new User("testId1234", "test@email.com", "encodedPw1234");
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    void signupSuccess(){
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(userRepository.existsById(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPw1234");

        SignupRequest request = new SignupRequest("testId1234", "test@email.com", "testPw1234");
        SignupResponse response = userService.signup(request);

        verify(userRepository,times(1)).existsById(anyString());
        verify(userRepository).save(captor.capture());
        verify(passwordEncoder, times(1)).encode(anyString());
        User savedUser = captor.getValue();

        assertAll(
                () -> assertEquals("testId1234", savedUser.getId()),
                () -> assertEquals("encodedPw1234", savedUser.getPassword()),
                () -> assertEquals("test@email.com", savedUser.getEmail()),
                () -> assertEquals(User.Status.JOIN, savedUser.getStatus()),
                () -> assertEquals("testId1234", response.id()),
                () -> assertEquals("JOIN", response.status())
        );
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 존재하는 아이디")
    void signupFailed(){
        SignupRequest request = new SignupRequest("testId1234", "test@email.com", "testPw1234");
        when(userRepository.existsById(anyString())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.signup(request));

        verify(userRepository,times(1)).existsById(anyString());
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void loginSuccess(){
        LoginRequest request = new LoginRequest("testId1234", "testPw1234");
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        LoginResponse response = userService.login(request);

        verify(userRepository, times(1)).findById(anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        assertEquals("testId1234", response.userId());
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 존재하지 않는 아이디")
    void loginFailedNotExists(){
        LoginRequest request = new LoginRequest("unknownId", "testPw1234");
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(LoginFailedException.class, () -> userService.login(request));

        verify(userRepository, times(1)).findById(anyString());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 패스워드 불일치")
    void loginFailedPasswordMismatch(){
        LoginRequest request = new LoginRequest("testId1234", "testPw1234");
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(LoginFailedException.class, () -> userService.login(request));

        verify(userRepository, times(1)).findById(anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("스테이터스 업데이트 성공 테스트")
    void updateStatusSuccess(){
        UserStatusUpdateRequest request = new UserStatusUpdateRequest("DORMANT");
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));

        assertEquals(User.Status.JOIN, user.getStatus());

        assertDoesNotThrow(() -> userService.updateStatus("testId1234", request));

        assertEquals(User.Status.DORMANT, user.getStatus());

        verify(userRepository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("스테이터스 업데이트 실패 테스트 - 존재하지 않는 유저")
    void updateStatusFailed1(){
        UserStatusUpdateRequest request = new UserStatusUpdateRequest("DORMANT");
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateStatus("unknownId", request));

        verify(userRepository, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("스테이터스 업데이트 실패 테스트 - 잘못된 스테이터스 형식")
    void updateStatusFailed2(){
        UserStatusUpdateRequest request = new UserStatusUpdateRequest("unknown status");

        assertThrows(IllegalArgumentException.class, () -> userService.updateStatus("unknownId", request));

        verify(userRepository, never()).findById(anyString());
    }

    @Test
    @DisplayName("회원 정보 검색 성공 테스트")
    void getUserSuccess(){
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));

        UserDto savedUser = userService.getUser("testId1234");

        assertAll(
                () -> assertEquals("testId1234", savedUser.userId()),
                () -> assertEquals("test@email.com", savedUser.email()),
                () -> assertEquals("JOIN", savedUser.status())
        );
    }

    @Test
    @DisplayName("회원 정보 검색 실패 테스트")
    void getUserFailed(){
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUser("unknownId"));
    }
}
