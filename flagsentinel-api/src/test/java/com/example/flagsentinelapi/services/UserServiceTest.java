package com.example.flagsentinelapi.services;

import com.example.flagsentinelapi.dto.user.*;
import com.example.flagsentinelapi.exception.*;
import com.example.flagsentinelapi.mapper.UserMapper;
import com.example.flagsentinelapi.model.User;
import com.example.flagsentinelapi.repository.UserRepository;

import com.example.flagsentinelapi.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repo;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserService service;

    // =========================================================
    // CREATE
    // =========================================================

    @Test
    void create_shouldThrowConflict_whenUsernameExists() {

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("admin");

        when(repo.findByUsername("admin"))
                .thenReturn(Optional.of(new User()));

        assertThrows(ConflictException.class,
                () -> service.create(request));

        verify(repo, never()).save(any());
    }

    @Test
    void create_shouldSaveAndReturnResponse_whenValid() {

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("admin");
        request.setPassword("123");

        User user = new User();
        user.setUsername("admin");

        User saved = new User();
        saved.setId(1L);
        saved.setUsername("admin");

        UserResponse response = new UserResponse();
        response.setId(1L);

        when(repo.findByUsername("admin")).thenReturn(Optional.empty());
        when(encoder.encode("123")).thenReturn("encoded");
        when(mapper.toEntity(request, "encoded")).thenReturn(user);
        when(repo.save(user)).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        UserResponse result = service.create(request);

        assertEquals(1L, result.getId());
        verify(repo).save(user);
    }

    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void update_shouldThrowNotFound_whenUserNotExists() {

        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.update(1L, new UpdateUserRequest()));
    }

    @Test
    void update_shouldThrowConflict_whenUsernameDuplicated() {

        User user = new User();
        user.setId(1L);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("new");

        when(repo.findById(1L)).thenReturn(Optional.of(user));
        when(repo.existsByUsernameAndIdNot("new", 1L)).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> service.update(1L, request));
    }

    @Test
    void update_shouldSave_whenValid() {

        User user = new User();
        user.setId(1L);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("new");

        UserResponse response = new UserResponse();
        response.setId(1L);

        when(repo.findById(1L)).thenReturn(Optional.of(user));
        when(repo.existsByUsernameAndIdNot("new", 1L)).thenReturn(false);
        when(repo.save(user)).thenReturn(user);
        when(mapper.toResponse(user)).thenReturn(response);

        UserResponse result = service.update(1L, request);

        assertEquals(1L, result.getId());
        verify(repo).save(user);
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @Test
    void getById_shouldThrowNotFound_whenMissing() {

        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.getById(1L));
    }

    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void delete_shouldThrowBadRequest_whenSelfDelete() {

        Authentication authentication = mock(Authentication.class);
        SecurityContext context = mock(SecurityContext.class);

        when(authentication.getName()).thenReturn("admin");
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        User current = new User();
        current.setId(1L);
        current.setUsername("admin");

        when(repo.findByUsername("admin")).thenReturn(Optional.of(current));

        assertThrows(BadRequestException.class,
                () -> service.delete(1L));
    }

    @Test
    void delete_shouldDelete_whenValid() {

        Authentication authentication = mock(Authentication.class);
        SecurityContext context = mock(SecurityContext.class);

        when(authentication.getName()).thenReturn("admin");
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        User current = new User();
        current.setId(1L);
        current.setUsername("admin");

        User target = new User();
        target.setId(2L);

        when(repo.findByUsername("admin")).thenReturn(Optional.of(current));
        when(repo.findById(2L)).thenReturn(Optional.of(target));

        service.delete(2L);

        verify(repo).delete(target);
    }

    // =========================================================
    // PAGED
    // =========================================================

    @Test
    void findAllPaged_shouldReturnPage() {

        User user = new User();
        user.setId(1L);

        UserResponse response = new UserResponse();
        response.setId(1L);

        Page<User> page = new PageImpl<>(List.of(user), PageRequest.of(0,10),1);

        when(repo.findAll(any(Pageable.class))).thenReturn(page);
        when(mapper.toResponse(user)).thenReturn(response);

        Page<UserResponse> result = service.findAllPaged(PageRequest.of(0,10));

        assertEquals(1, result.getTotalElements());
    }
}