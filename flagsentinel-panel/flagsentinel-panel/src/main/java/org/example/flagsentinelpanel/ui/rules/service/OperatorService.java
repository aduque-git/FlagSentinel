package org.example.flagsentinelpanel.ui.rules.service;

import org.example.flagsentinelpanel.config.AppPropertyKeys;
import org.example.flagsentinelpanel.dto.OperatorOption;
import org.example.flagsentinelpanel.service.BaseApiClient;
import org.example.flagsentinelpanel.service.SecurityService;
import org.example.flagsentinelpanel.util.AppProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class OperatorService extends BaseApiClient {

    public OperatorService(RestTemplate restTemplate,
                           SecurityService securityService,
                           AppProperties properties) {
        super(restTemplate, securityService, properties.get(AppPropertyKeys.BASE_URL));
    }

    public List<OperatorOption> findAll() {
        String url = baseUrl + "/rules/operators";
        OperatorOption[] result = exchange(url, HttpMethod.GET, null, OperatorOption[].class);
        return Arrays.asList(result);
    }
}
