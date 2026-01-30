package com.example.flagsentinelapi.service;

import com.example.flagsentinelapi.dto.CreateFeatureFlagRequest;
import com.example.flagsentinelapi.dto.FeatureFlagResponse;
import com.example.flagsentinelapi.dto.UpdateFeatureFlagRequest;
import com.example.flagsentinelapi.mapper.FeatureFlagMapper;
import com.example.flagsentinelapi.model.FeatureFlag;
import com.example.flagsentinelapi.repository.FeatureFlagRepository;
import com.example.flagsentinelapi.websocket.WebSocketEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeatureFlagService {

    private final FeatureFlagRepository repo;
    private final FeatureFlagMapper mapper;
    private final WebSocketEventPublisher ws;

    public FeatureFlagResponse create(CreateFeatureFlagRequest request) {
        if (repo.findByKey(request.getKey()).isPresent()) {
            throw new RuntimeException("Feature flag key already exists");
        }
        FeatureFlag flag = mapper.toEntity(request);
        FeatureFlag saved = repo.save(flag);

        FeatureFlagResponse response = mapper.toResponse(saved);
        ws.publishFlagUpdate(response);
        return response;
    }

    public FeatureFlagResponse update(Long id, UpdateFeatureFlagRequest request) {
        FeatureFlag flag = repo.findById(id).orElseThrow(() -> new RuntimeException("Feature flag not found"));
        mapper.updateEntity(flag, request);
        FeatureFlag saved = repo.save(flag);

        FeatureFlagResponse response = mapper.toResponse(saved);
        ws.publishFlagUpdate(response);
        return response;
    }

    public FeatureFlagResponse getById(Long id) {
        FeatureFlag flag = repo.findById(id).orElseThrow(() -> new RuntimeException("Feature flag not found"));
        return mapper.toResponse(flag);
    }

    public List<FeatureFlagResponse> getAll() {
        return repo.findAll().stream().map(mapper::toResponse).toList();
    }

    public void delete(Long id) {
        FeatureFlag flag = repo.findById(id).orElseThrow(() -> new RuntimeException("Feature flag not found"));
        repo.delete(flag);
        ws.publishFlagUpdate("deleted:" + id);
    }
}
