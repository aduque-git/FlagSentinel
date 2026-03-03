package org.example.flagsentinelpanel.ui.featureflags.service;

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
public class FeatureFlagsClient extends BaseApiClient {

    public FeatureFlagsClient(RestTemplate restTemplate,
                              SecurityService securityService,
                              AppProperties properties) {
        super(restTemplate, securityService, properties.get(AppPropertyKeys.BASE_URL));
    }

    public List<FeatureFlagResponse> getAll() {
        FeatureFlagResponse[] arr = exchange(
                baseUrl + "/flags",
                HttpMethod.GET,
                null,
                FeatureFlagResponse[].class
        );
        return Arrays.asList(arr);
    }

    public FeatureFlagResponse create(CreateFeatureFlagRequest body) {
        return exchange(
                baseUrl + "/flags",
                HttpMethod.POST,
                body,
                FeatureFlagResponse.class
        );
    }

    public FeatureFlagResponse update(Long id, UpdateFeatureFlagRequest body) {
        return exchange(
                baseUrl + "/flags/{id}",
                HttpMethod.PUT,
                body,
                FeatureFlagResponse.class,
                id
        );
    }

    public void delete(Long id) {
        exchange(
                baseUrl + "/flags/{id}",
                HttpMethod.DELETE,
                null,
                Void.class,
                id
        );
    }

    public PageResponse<FeatureFlagResponse> findPaged(int page, int size) {
        String url = baseUrl + "/flags/paged?page=" + page + "&size=" + size;

        return exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
    }
}
