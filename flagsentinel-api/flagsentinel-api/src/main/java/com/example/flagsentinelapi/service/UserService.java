package com.example.flagsentinelapi.service;

import com.example.flagsentinelapi.dto.CreateUserRequest;
import com.example.flagsentinelapi.dto.UpdateUserRequest;
import com.example.flagsentinelapi.dto.UserResponse;
import com.example.flagsentinelapi.mapper.UserMapper;
import com.example.flagsentinelapi.model.User;
import com.example.flagsentinelapi.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final UserMapper mapper;

    public UserService(UserRepository repo, PasswordEncoder encoder, UserMapper mapper) {
        this.repo = repo;
        this.encoder = encoder;
        this.mapper = mapper;
    }

    public UserResponse create(CreateUserRequest request) {
        if (repo.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        User user = mapper.toEntity(request, encoder.encode(request.getPassword()));
        User saved = repo.save(user);
        return mapper.toResponse(saved);
    }

    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = repo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        mapper.updateEntity(user, request);
        User saved = repo.save(user);
        return mapper.toResponse(saved);
    }

    public UserResponse getById(Long id) {
        User user = repo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return mapper.toResponse(user);
    }

    public List<UserResponse> getAll() {
        return repo.findAll().stream().map(mapper::toResponse).toList();
    }

    public void delete(Long id) {
        System.out.println("CURRO: " + id);
        User user = repo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        repo.delete(user);
    }
}
