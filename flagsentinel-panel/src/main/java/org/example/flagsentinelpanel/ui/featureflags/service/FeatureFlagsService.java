package org.example.flagsentinelpanel.ui.featureflags.service;

import org.example.flagsentinelpanel.dto.*;
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

    public PageResponse<FeatureFlagResponse> findPaged(int page, int size) {return client.findPaged(page, size); }
}
