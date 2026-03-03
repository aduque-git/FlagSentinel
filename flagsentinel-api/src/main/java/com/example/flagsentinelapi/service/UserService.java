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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final UserMapper mapper;

    public UserService(UserRepository repo, PasswordEncoder encoder, UserMapper mapper) {
        this.repo = repo;
        this.encoder = encoder;
        this.mapper = mapper;
    }

    // ---------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------
    public UserResponse create(CreateUserRequest request) {

        log.debug("User create requested username='{}'", request.getUsername());

        if (repo.findByUsername(request.getUsername()).isPresent()) {
            log.warn("User creation failed — username already exists '{}'", request.getUsername());
            throw new ConflictException("Username already exists");
        }

        User user = mapper.toEntity(request, encoder.encode(request.getPassword()));
        User saved = repo.save(user);

        log.info("User created id={} username='{}'", saved.getId(), saved.getUsername());

        return mapper.toResponse(saved);
    }

    // ---------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------
    public UserResponse update(Long id, UpdateUserRequest request) {

        log.debug("User update requested id={}", id);

        User user = repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("User update failed — id={} not found", id);
                    return new NotFoundException("User not found");
                });

        // Validación de username duplicado
        if (request.getUsername() != null &&
                repo.existsByUsernameAndIdNot(request.getUsername(), id)) {

            log.warn("User update failed — username '{}' already exists", request.getUsername());
            throw new ConflictException("Username already exists");
        }

        mapper.updateEntity(user, request);
        User saved = repo.save(user);

        log.info("User updated id={} username='{}'", saved.getId(), saved.getUsername());

        return mapper.toResponse(saved);
    }

    // ---------------------------------------------------------
    // GET BY ID
    // ---------------------------------------------------------
    public UserResponse getById(Long id) {

        log.debug("User getById requested id={}", id);

        User user = repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("User getById failed — id={} not found", id);
                    return new NotFoundException("User not found");
                });

        return mapper.toResponse(user);
    }

    // ---------------------------------------------------------
    // GET ALL
    // ---------------------------------------------------------
    public List<UserResponse> getAll() {

        log.debug("User getAll requested");

        List<UserResponse> list = repo.findAll().stream()
                .map(mapper::toResponse)
                .toList();

        log.info("User list retrieved total={}", list.size());

        return list;
    }

    // ---------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------
    public void delete(Long id) {

        log.debug("User delete requested id={}", id);

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = repo.findByUsername(currentUsername)
                .orElseThrow(() -> {
                    log.error("Authenticated user '{}' not found in DB", currentUsername);
                    return new NotFoundException("Authenticated user not found");
                });

        // Impedir auto-borrado
        if (currentUser.getId().equals(id)) {
            log.warn("User '{}' attempted to delete own account id={}", currentUsername, id);
            throw new BadRequestException("No puedes borrar tu propio usuario");
        }

        User user = repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("User delete failed — id={} not found", id);
                    return new NotFoundException("User not found");
                });

        repo.delete(user);

        log.info("User deleted id={} username='{}'", user.getId(), user.getUsername());
    }

    // ---------------------------------------------------------
    // PAGED
    // ---------------------------------------------------------
    public Page<UserResponse> findAllPaged(Pageable pageable) {

        log.debug("User paged retrieval requested pageable={}", pageable);

        Page<UserResponse> page = repo.findAll(pageable)
                .map(mapper::toResponse);

        log.info("User paged retrieval completed totalElements={}", page.getTotalElements());

        return page;
    }
}