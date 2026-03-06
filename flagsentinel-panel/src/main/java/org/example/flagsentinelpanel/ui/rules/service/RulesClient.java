package org.example.flagsentinelpanel.ui.rules.service;

import org.example.flagsentinelpanel.config.AppPropertyKeys;
import org.example.flagsentinelpanel.dto.CreateRuleRequest;
import org.example.flagsentinelpanel.dto.PageResponse;
import org.example.flagsentinelpanel.dto.RuleResponse;
import org.example.flagsentinelpanel.dto.UpdateRuleRequest;
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
public class RulesClient extends BaseApiClient {

    public RulesClient(RestTemplate restTemplate,
                       SecurityService securityService,
                       AppProperties properties) {
        super(restTemplate, securityService, properties.get(AppPropertyKeys.BASE_URL));
    }

    public List<RuleResponse> getAllRules() {
        RuleResponse[] arr = exchange(
                baseUrl + "/rules",
                HttpMethod.GET,
                null,
                RuleResponse[].class
        );
        return Arrays.asList(arr);
    }

    public RuleResponse getRuleById(Long id) {
        return exchange(
                baseUrl + "/rules/{id}",
                HttpMethod.GET,
                null,
                RuleResponse.class,
                id
        );
    }

    public RuleResponse createRule(CreateRuleRequest body) {
        return exchange(
                baseUrl + "/rules",
                HttpMethod.POST,
                body,
                RuleResponse.class
        );
    }

    public RuleResponse updateRule(Long id, UpdateRuleRequest body) {
        return exchange(
                baseUrl + "/rules/{id}",
                HttpMethod.PUT,
                body,
                RuleResponse.class,
                id
        );
    }

    public void deleteRule(Long id) {
        exchange(
                baseUrl + "/rules/{id}",
                HttpMethod.DELETE,
                null,
                Void.class,
                id
        );
    }

    public PageResponse<RuleResponse> findPaged(int page, int size) {
        String url = baseUrl + "/rules/paged?page=" + page + "&size=" + size;

        return exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
    }


}
