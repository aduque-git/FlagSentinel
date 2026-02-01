package com.example.flagsentinelapi.service;

import com.example.flagsentinelapi.dto.CreateUserRequest;
import com.example.flagsentinelapi.dto.UpdateUserRequest;
import com.example.flagsentinelapi.dto.UserResponse;
import com.example.flagsentinelapi.mapper.UserMapper;
import com.example.flagsentinelapi.model.Role;
import com.example.flagsentinelapi.model.User;
import com.example.flagsentinelapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository repo;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------
    @Test
    void create_shouldSaveUser_whenUsernameNotExists() {
        CreateUserRequest req = new CreateUserRequest("aaron", "password123", "ADMIN");

        when(repo.findByUsername("aaron")).thenReturn(Optional.empty());
        when(encoder.encode("password123")).thenReturn("encodedPass");

        User entity = new User("aaron", "encodedPass", Role.ADMIN);
        User saved = new User("aaron", "encodedPass", Role.ADMIN);
        saved.setId(1L);

        UserResponse response = new UserResponse(1L, "aaron", "ADMIN");

        when(mapper.toEntity(req, "encodedPass")).thenReturn(entity);
        when(repo.save(entity)).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        UserResponse result = service.create(req);

        assertEquals(response, result);
        verify(repo).save(entity);
        verify(encoder).encode("password123");
    }

    @Test
    void create_shouldThrow_whenUsernameExists() {
        CreateUserRequest req = new CreateUserRequest("aaron", "pass", "ADMIN");

        when(repo.findByUsername("aaron")).thenReturn(Optional.of(new User()));

        assertThrows(RuntimeException.class, () -> service.create(req));
    }

    // ---------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------
    @Test
    void update_shouldModifyUser() {
        UpdateUserRequest req = new UpdateUserRequest("newName", "USER");

        User existing = new User("oldName", "pass", Role.ADMIN);
        existing.setId(1L);

        User saved = new User("newName", "pass", Role.USER);
        saved.setId(1L);

        UserResponse response = new UserResponse(1L, "newName", "USER");

        when(repo.findById(1L)).thenReturn(Optional.of(existing));

        doAnswer(invocation -> {
            existing.setUsername(req.getUsername());
            existing.setRole(Role.valueOf(req.getRole().toUpperCase()));
            return null;
        }).when(mapper).updateEntity(existing, req);

        when(repo.save(existing)).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        UserResponse result = service.update(1L, req);

        assertEquals(response, result);
        verify(repo).save(existing);
    }

    @Test
    void update_shouldThrow_whenUserNotFound() {
        UpdateUserRequest req = new UpdateUserRequest("newName", "USER");

        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.update(99L, req));
    }

    // ---------------------------------------------------------
    // GET BY ID
    // ---------------------------------------------------------
    @Test
    void getById_shouldReturnUserResponse() {
        User entity = new User("aaron", "pass", Role.ADMIN);
        entity.setId(1L);

        UserResponse response = new UserResponse(1L, "aaron", "ADMIN");

        when(repo.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        UserResponse result = service.getById(1L);

        assertEquals(response, result);
    }

    @Test
    void getById_shouldThrow_whenNotFound() {
        when(repo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.getById(1L));
    }

    // ---------------------------------------------------------
    // GET ALL
    // ---------------------------------------------------------
    @Test
    void getAll_shouldReturnListOfResponses() {
        User u1 = new User("aaron", "pass", Role.ADMIN);
        u1.setId(1L);

        UserResponse dto1 = new UserResponse(1L, "aaron", "ADMIN");

        when(repo.findAll()).thenReturn(List.of(u1));
        when(mapper.toResponse(u1)).thenReturn(dto1);

        List<UserResponse> result = service.getAll();

        assertEquals(1, result.size());
        assertEquals(dto1, result.get(0));
    }

    // ---------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------
    @Test
    void delete_shouldRemoveUser() {
        User entity = new User("aaron", "pass", Role.ADMIN);
        entity.setId(1L);

        when(repo.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(1L);

        verify(repo).delete(entity);
    }

    @Test
    void delete_shouldThrow_whenNotFound() {
        when(repo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.delete(1L));
    }
}
