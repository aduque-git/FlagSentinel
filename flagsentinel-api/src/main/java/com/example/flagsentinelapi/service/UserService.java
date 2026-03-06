package com.example.flagsentinelapi.service;

import com.example.flagsentinelapi.dto.user.CreateUserRequest;
import com.example.flagsentinelapi.dto.user.UpdateUserRequest;
import com.example.flagsentinelapi.dto.user.UserResponse;
import com.example.flagsentinelapi.exception.BadRequestException;
import com.example.flagsentinelapi.exception.ConflictException;
import com.example.flagsentinelapi.exception.NotFoundException;
import com.example.flagsentinelapi.logging.ApiLogMessages;
import com.example.flagsentinelapi.logging.LogPropertiesKeys;
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

    public UserResponse create(CreateUserRequest request) {

        log.debug(ApiLogMessages.get(
                LogPropertiesKeys.USER_CREATE_REQUEST,
                request.getUsername()
        ));

        if (repo.findByUsername(request.getUsername()).isPresent()) {
            log.warn(ApiLogMessages.get(
                    LogPropertiesKeys.USER_CREATE_DUPLICATE,
                    request.getUsername()
            ));
            throw new ConflictException("Username already exists");
        }

        User user = mapper.toEntity(request, encoder.encode(request.getPassword()));
        User saved = repo.save(user);

        log.info(ApiLogMessages.get(
                LogPropertiesKeys.USER_CREATE_SUCCESS,
                saved.getId(),
                saved.getUsername()
        ));

        return mapper.toResponse(saved);
    }

    public UserResponse update(Long id, UpdateUserRequest request) {

        log.debug(ApiLogMessages.get(
                LogPropertiesKeys.USER_UPDATE_REQUEST,
                id
        ));

        User user = repo.findById(id)
                .orElseThrow(() -> {
                    log.warn(ApiLogMessages.get(
                            LogPropertiesKeys.USER_NOT_FOUND,
                            id
                    ));
                    return new NotFoundException("User not found");
                });

        if (request.getUsername() != null &&
                repo.existsByUsernameAndIdNot(request.getUsername(), id)) {

            log.warn(ApiLogMessages.get(
                    LogPropertiesKeys.USER_UPDATE_DUPLICATE,
                    request.getUsername()
            ));

            throw new ConflictException("Username already exists");
        }

        mapper.updateEntity(user, request);
        User saved = repo.save(user);

        log.info(ApiLogMessages.get(
                LogPropertiesKeys.USER_UPDATE_SUCCESS,
                saved.getId(),
                saved.getUsername()
        ));

        return mapper.toResponse(saved);
    }

    public UserResponse getById(Long id) {

        log.debug(ApiLogMessages.get(
                LogPropertiesKeys.USER_GET_REQUEST,
                id
        ));

        User user = repo.findById(id)
                .orElseThrow(() -> {
                    log.warn(ApiLogMessages.get(
                            LogPropertiesKeys.USER_NOT_FOUND,
                            id
                    ));
                    return new NotFoundException("User not found");
                });

        return mapper.toResponse(user);
    }

    public List<UserResponse> getAll() {

        log.debug(ApiLogMessages.get(LogPropertiesKeys.USER_GET_ALL_REQUEST));

        List<UserResponse> list = repo.findAll().stream()
                .map(mapper::toResponse)
                .toList();

        log.info(ApiLogMessages.get(
                LogPropertiesKeys.USER_GET_ALL_SUCCESS,
                list.size()
        ));

        return list;
    }

    public void delete(Long id) {

        log.debug(ApiLogMessages.get(
                LogPropertiesKeys.USER_DELETE_REQUEST,
                id
        ));

        String currentUsername = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User currentUser = repo.findByUsername(currentUsername)
                .orElseThrow(() -> {
                    log.error(ApiLogMessages.get(
                            LogPropertiesKeys.USER_AUTHENTICATED_NOT_FOUND,
                            currentUsername
                    ));
                    return new NotFoundException("Authenticated user not found");
                });

        if (currentUser.getId().equals(id)) {

            log.warn(ApiLogMessages.get(
                    LogPropertiesKeys.USER_SELF_DELETE_ATTEMPT,
                    currentUsername,
                    id
            ));

            throw new BadRequestException("No puedes borrar tu propio usuario");
        }

        User user = repo.findById(id)
                .orElseThrow(() -> {
                    log.warn(ApiLogMessages.get(
                            LogPropertiesKeys.USER_NOT_FOUND,
                            id
                    ));
                    return new NotFoundException("User not found");
                });

        repo.delete(user);

        log.info(ApiLogMessages.get(
                LogPropertiesKeys.USER_DELETE_SUCCESS,
                user.getId(),
                user.getUsername()
        ));
    }

    public Page<UserResponse> findAllPaged(Pageable pageable) {

        log.debug(ApiLogMessages.get(
                LogPropertiesKeys.USER_PAGE_REQUEST,
                pageable.getPageNumber(),
                pageable.getPageSize()
        ));

        Page<UserResponse> page = repo.findAll(pageable)
                .map(mapper::toResponse);

        log.info(ApiLogMessages.get(
                LogPropertiesKeys.USER_PAGE_SUCCESS,
                page.getTotalElements()
        ));

        return page;
    }
}