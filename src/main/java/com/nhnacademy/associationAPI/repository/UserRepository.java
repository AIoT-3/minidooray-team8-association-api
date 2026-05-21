package com.nhnacademy.associationAPI.repository;

import com.nhnacademy.associationAPI.dto.UserDto;
import com.nhnacademy.associationAPI.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
}
