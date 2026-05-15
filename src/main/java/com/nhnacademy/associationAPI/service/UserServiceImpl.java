package com.nhnacademy.associationAPI.service;

import com.nhnacademy.associationAPI.dto.*;
import com.nhnacademy.associationAPI.exception.LoginFailedException;
import com.nhnacademy.associationAPI.exception.UserAlreadyExistsException;
import com.nhnacademy.associationAPI.exception.UserNotFoundException;
import com.nhnacademy.associationAPI.repository.UserRepository;
import com.nhnacademy.associationAPI.user.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(final UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public SignupResponse signup(SignupRequest request) {
        if(userRepository.existsById(request.id())){
            throw new UserAlreadyExistsException(request.id());
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        User user = new User(request.id(), request.email(), encodedPassword);
        User savedUser = userRepository.save(user);

        return SignupResponse.from(savedUser);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new LoginFailedException("로그인 실패: 존재하지 않는 아이디"));

        if(!passwordEncoder.matches(request.password(), user.getPassword())){
            throw new LoginFailedException("로그인 실패: 패스워드 불일치");
        }
        return LoginResponse.from(user);
    }

    @Override
    public void updateStatus(String userId, UserStatusUpdateRequest request) {
        User.Status updateStatus = User.Status.fromString(request.status());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        user.setStatus(updateStatus);
    }

    @Override
    public UserDto getUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return UserDto.from(user);
    }
}
