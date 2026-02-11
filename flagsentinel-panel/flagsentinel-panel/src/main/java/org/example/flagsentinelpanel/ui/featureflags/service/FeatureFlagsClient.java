package org.example.flagsentinelpanel.ui.featureflags.service;

import org.example.flagsentinelpanel.dto.CreateFeatureFlagRequest;
import org.example.flagsentinelpanel.dto.FeatureFlagResponse;
import org.example.flagsentinelpanel.dto.UpdateFeatureFlagRequest;
import org.example.flagsentinelpanel.service.SecurityService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class FeatureFlagsClient {

    private final RestTemplate restTemplate;
    private final SecurityService securityService;
    private final String baseUrl;

    public FeatureFlagsClient(RestTemplate restTemplate,
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

    public List<FeatureFlagResponse> getAll() {
        HttpEntity<Void> entity = new HttpEntity<>(buildHeaders());

        ResponseEntity<FeatureFlagResponse[]> response = restTemplate.exchange(
                baseUrl + "/flags",
                HttpMethod.GET,
                entity,
                FeatureFlagResponse[].class
        );

        return Arrays.asList(response.getBody());
    }

    public FeatureFlagResponse create(CreateFeatureFlagRequest body) {
        HttpEntity<CreateFeatureFlagRequest> entity = new HttpEntity<>(body, buildHeaders());

        ResponseEntity<FeatureFlagResponse> response = restTemplate.exchange(
                baseUrl + "/flags",
                HttpMethod.POST,
                entity,
                FeatureFlagResponse.class
        );

        return response.getBody();
    }

    public FeatureFlagResponse update(Long id, UpdateFeatureFlagRequest body) {
        HttpEntity<UpdateFeatureFlagRequest> entity = new HttpEntity<>(body, buildHeaders());

        ResponseEntity<FeatureFlagResponse> response = restTemplate.exchange(
                baseUrl + "/flags/{id}",
                HttpMethod.PUT,
                entity,
                FeatureFlagResponse.class,
                id
        );

        return response.getBody();
    }

    public void delete(Long id) {
        HttpEntity<Void> entity = new HttpEntity<>(buildHeaders());

        restTemplate.exchange(
                baseUrl + "/flags/{id}",
                HttpMethod.DELETE,
                entity,
                Void.class,
                id
        );
    }
}
