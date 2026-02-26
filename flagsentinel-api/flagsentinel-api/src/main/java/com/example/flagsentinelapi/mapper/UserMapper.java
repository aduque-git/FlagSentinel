package com.example.flagsentinelapi.mapper;

import com.example.flagsentinelapi.dto.user.CreateUserRequest;
import com.example.flagsentinelapi.dto.user.UpdateUserRequest;
import com.example.flagsentinelapi.dto.user.UserResponse;
import com.example.flagsentinelapi.dto.user.Role;
import com.example.flagsentinelapi.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(CreateUserRequest request, String encodedPassword) {
        Role role = Role.valueOf(request.getRole().toUpperCase());
        return new User(request.getUsername(), encodedPassword, role);
    }

    public void updateEntity(User user, UpdateUserRequest request) {
        user.setUsername(request.getUsername());
        Role role = Role.valueOf(request.getRole().toUpperCase());
        user.setRole(role);
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getRole().name());
    }
}
