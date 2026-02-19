package org.example.flagsentinelpanel.ui.users.service;

import org.example.flagsentinelpanel.config.AppPropertyKeys;
import org.example.flagsentinelpanel.dto.*;
import org.example.flagsentinelpanel.service.BaseApiClient;
import org.example.flagsentinelpanel.service.SecurityService;
import org.example.flagsentinelpanel.util.AppProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class UsersClient extends BaseApiClient {

    public UsersClient(RestTemplate restTemplate,
                       SecurityService securityService,
                       AppProperties properties) {
        super(restTemplate, securityService, properties.get(AppPropertyKeys.BASE_URL));
    }

    public List<UserResponse> getAllUsers() {
        UserResponse[] arr = exchange(
                baseUrl + "/users",
                HttpMethod.GET,
                null,
                UserResponse[].class
        );
        return Arrays.asList(arr);
    }

    public UserResponse createUser(CreateUserRequest body) {
        return exchange(
                baseUrl + "/users",
                HttpMethod.POST,
                body,
                UserResponse.class
        );
    }

    public UserResponse updateUser(Long id, UpdateUserRequest body) {
        return exchange(
                baseUrl + "/users/{id}",
                HttpMethod.PUT,
                body,
                UserResponse.class,
                id
        );
    }

    public void deleteUser(Long id) {
        exchange(
                baseUrl + "/users/{id}",
                HttpMethod.DELETE,
                null,
                Void.class,
                id
        );
    }



    public PageResponse<UserResponse> findPaged(int page, int size) {
        String url = baseUrl + "/users/paged?page=" + page + "&size=" + size;

        return exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
    }
}
