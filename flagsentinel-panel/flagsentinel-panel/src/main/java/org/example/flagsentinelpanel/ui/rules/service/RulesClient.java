package org.example.flagsentinelpanel.ui.rules.service;

import org.example.flagsentinelpanel.dto.CreateRuleRequest;
import org.example.flagsentinelpanel.dto.RuleResponse;
import org.example.flagsentinelpanel.dto.UpdateRuleRequest;
import org.example.flagsentinelpanel.service.SecurityService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class RulesClient {

    private final RestTemplate restTemplate;
    private final SecurityService securityService;
    private final String baseUrl;

    public RulesClient(RestTemplate restTemplate,
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

    public List<RuleResponse> getAllRules() {
        HttpEntity<Void> entity = new HttpEntity<>(buildHeaders());

        ResponseEntity<RuleResponse[]> response = restTemplate.exchange(
                baseUrl + "/rules",
                HttpMethod.GET,
                entity,
                RuleResponse[].class
        );

        return Arrays.asList(response.getBody());
    }

    public RuleResponse getRuleById(Long id) {
        HttpEntity<Void> entity = new HttpEntity<>(buildHeaders());

        ResponseEntity<RuleResponse> response = restTemplate.exchange(
                baseUrl + "/rules/{id}",
                HttpMethod.GET,
                entity,
                RuleResponse.class,
                id
        );

        return response.getBody();
    }

    public RuleResponse createRule(CreateRuleRequest body) {
        HttpEntity<CreateRuleRequest> entity = new HttpEntity<>(body, buildHeaders());

        ResponseEntity<RuleResponse> response = restTemplate.exchange(
                baseUrl + "/rules",
                HttpMethod.POST,
                entity,
                RuleResponse.class
        );

        return response.getBody();
    }

    public RuleResponse updateRule(Long id, UpdateRuleRequest body) {
        HttpEntity<UpdateRuleRequest> entity = new HttpEntity<>(body, buildHeaders());

        ResponseEntity<RuleResponse> response = restTemplate.exchange(
                baseUrl + "/rules/{id}",
                HttpMethod.PUT,
                entity,
                RuleResponse.class,
                id
        );

        return response.getBody();
    }

    public void deleteRule(Long id) {
        HttpEntity<Void> entity = new HttpEntity<>(buildHeaders());

        restTemplate.exchange(
                baseUrl + "/rules/{id}",
                HttpMethod.DELETE,
                entity,
                Void.class,
                id
        );
    }
}
