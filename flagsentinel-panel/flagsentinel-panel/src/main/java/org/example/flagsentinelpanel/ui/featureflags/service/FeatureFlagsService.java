package org.example.flagsentinelpanel.ui.featureflags.service;

import org.example.flagsentinelpanel.dto.CreateFeatureFlagRequest;
import org.example.flagsentinelpanel.dto.FeatureFlagResponse;
import org.example.flagsentinelpanel.dto.UpdateFeatureFlagRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeatureFlagsService {

    private final FeatureFlagsClient client;

    public FeatureFlagsService(FeatureFlagsClient client) {
        this.client = client;
    }

    public List<FeatureFlagResponse> findAll() {
        return client.getAll();
    }

    public FeatureFlagResponse create(CreateFeatureFlagRequest request) {
        return client.create(request);
    }

    public FeatureFlagResponse update(Long id, UpdateFeatureFlagRequest request) {
        return client.update(id, request);
    }

    public void delete(Long id) {
        client.delete(id);
    }
}
