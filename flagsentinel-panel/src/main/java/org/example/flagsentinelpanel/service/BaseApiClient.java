package org.example.flagsentinelpanel.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.flagsentinelpanel.exceptions.ApiClientException;
import org.example.flagsentinelpanel.util.AppProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
public abstract class BaseApiClient {

    protected final RestTemplate restTemplate;
    protected final SecurityService securityService;
    protected final String baseUrl;

    public BaseApiClient(RestTemplate restTemplate,
                         SecurityService securityService,
                         String baseUrl) {
        this.restTemplate = restTemplate;
        this.securityService = securityService;
        this.baseUrl = baseUrl;
    }

    protected HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String token = securityService.getToken();
        if (token != null && !token.isBlank()) {
            headers.set("Authorization", "Bearer " + token);
        }

        return headers;
    }

    protected <T> T exchange(String url, HttpMethod method, Object body, Class<T> responseType, Object... uriVars) {

        try {
            HttpEntity<?> entity = new HttpEntity<>(body, buildHeaders());

            ResponseEntity<T> response = restTemplate.exchange(
                    url,
                    method,
                    entity,
                    responseType,
                    uriVars
            );

            return response.getBody();

        } catch (HttpStatusCodeException ex) {

            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode json = mapper.readTree(ex.getResponseBodyAsString());
                String message = json.has("message") ? json.get("message").asText() : "Unexpected API error";
                throw new ApiClientException(message);

            } catch (JsonProcessingException parseError) {
                throw new ApiClientException("Unexpected API error");
            }
        }
    }

    public <T> T exchange(String url, HttpMethod method, Object body, ParameterizedTypeReference<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(securityService.getToken());

        HttpEntity<?> entity = (body != null)
                ? new HttpEntity<>(body, headers)
                : new HttpEntity<>(headers);

        ResponseEntity<T> response = restTemplate.exchange(
                url,
                method,
                entity,
                responseType
        );

        return response.getBody();
    }

}
