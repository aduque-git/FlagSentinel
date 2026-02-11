package org.example.flagsentinelpanel.ui.users.service;

import org.example.flagsentinelpanel.dto.CreateUserRequest;
import org.example.flagsentinelpanel.dto.UpdateUserRequest;
import org.example.flagsentinelpanel.dto.UserResponse;
import org.example.flagsentinelpanel.service.SecurityService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class UsersClient {

    private final RestTemplate restTemplate;
    private final SecurityService securityService;
    private final String baseUrl;

    public UsersClient(RestTemplate restTemplate,
                       SecurityService securityService,
                       @Value("${api.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.securityService = securityService;
        this.baseUrl = baseUrl;
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String token = securityService.getToken();
        if (token != null && !token.isBlank()) {
            headers.set("Authorization", "Bearer " + token);
        }

        return headers;
    }

    public List<UserResponse> getAllUsers() {
        HttpEntity<Void> entity = new HttpEntity<>(buildHeaders());

        ResponseEntity<UserResponse[]> response = restTemplate.exchange(
                baseUrl + "/users",
                HttpMethod.GET,
                entity,
                UserResponse[].class
        );

        return Arrays.asList(response.getBody());
    }

    public UserResponse createUser(CreateUserRequest body) {
        HttpEntity<CreateUserRequest> entity = new HttpEntity<>(body, buildHeaders());

        ResponseEntity<UserResponse> response = restTemplate.exchange(
                baseUrl + "/users",
                HttpMethod.POST,
                entity,
                UserResponse.class
        );

        return response.getBody();
    }

    public UserResponse updateUser(Long id, UpdateUserRequest body) {
        HttpEntity<UpdateUserRequest> entity = new HttpEntity<>(body, buildHeaders());

        ResponseEntity<UserResponse> response = restTemplate.exchange(
                baseUrl + "/users/{id}",
                HttpMethod.PUT,
                entity,
                UserResponse.class,
                id
        );

        return response.getBody();
    }

    public void deleteUser(Long id) {
        HttpEntity<Void> entity = new HttpEntity<>(buildHeaders());

        restTemplate.exchange(
                baseUrl + "/users/{id}",
                HttpMethod.DELETE,
                entity,
                Void.class,
                id
        );
    }
}
