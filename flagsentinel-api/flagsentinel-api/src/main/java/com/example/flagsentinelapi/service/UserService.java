package com.example.flagsentinelapi.service;

import com.example.flagsentinelapi.dto.user.CreateUserRequest;
import com.example.flagsentinelapi.dto.user.UpdateUserRequest;
import com.example.flagsentinelapi.dto.user.UserResponse;
import com.example.flagsentinelapi.exception.BadRequestException;
import com.example.flagsentinelapi.exception.ConflictException;
import com.example.flagsentinelapi.exception.NotFoundException;
import com.example.flagsentinelapi.mapper.UserMapper;
import com.example.flagsentinelapi.model.User;
import com.example.flagsentinelapi.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
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
            throw new ConflictException("Username already exists");
        }

        User user = mapper.toEntity(request, encoder.encode(request.getPassword()));
        User saved = repo.save(user);

        return mapper.toResponse(saved);
    }

    public UserResponse update(Long id, UpdateUserRequest request) {

        User user = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        mapper.updateEntity(user, request);
        User saved = repo.save(user);

        return mapper.toResponse(saved);
    }

    public UserResponse getById(Long id) {

        User user = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return mapper.toResponse(user);
    }

    public List<UserResponse> getAll() {
        return repo.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public void delete(Long id) {

        // 1. Obtener el username del usuario autenticado
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Obtener el usuario autenticado desde la BD
        User currentUser = repo.findByUsername(currentUsername)
                .orElseThrow(() -> new NotFoundException("Authenticated user not found"));

        // 3. Impedir que se borre a sí mismo
        if (currentUser.getId().equals(id)) {
            throw new BadRequestException("No puedes borrar tu propio usuario");
        }

        // 4. Borrar el usuario objetivo
        User user = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        repo.delete(user);
    }

    public Page<UserResponse> findAllPaged(Pageable pageable) {
        return repo.findAll(pageable)
                .map(mapper::toResponse);
    }


}
