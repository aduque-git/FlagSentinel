package org.example.flagsentinelpanel.ui.users.service;

import org.example.flagsentinelpanel.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsersService {

    private final UsersClient client;

    public UsersService(UsersClient client) {
        this.client = client;
    }

    public List<UserResponse> findAll() {
        return client.getAllUsers();
    }

    public UserResponse create(CreateUserRequest request) {
        return client.createUser(request);
    }

    public UserResponse update(Long id, UpdateUserRequest request) {
        return client.updateUser(id, request);
    }

    public void delete(Long id) {
        client.deleteUser(id);
    }

    public PageResponse<UserResponse> findPaged(int page, int size) {return client.findPaged(page, size); }
}
