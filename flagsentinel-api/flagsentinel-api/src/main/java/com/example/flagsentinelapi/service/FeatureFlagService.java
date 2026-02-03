package com.example.flagsentinelapi.service;

import com.example.flagsentinelapi.dto.CreateFeatureFlagRequest;
import com.example.flagsentinelapi.dto.FeatureFlagResponse;
import com.example.flagsentinelapi.dto.UpdateFeatureFlagRequest;
import com.example.flagsentinelapi.mapper.FeatureFlagMapper;
import com.example.flagsentinelapi.model.FeatureFlag;
import com.example.flagsentinelapi.model.Rule;
import com.example.flagsentinelapi.repository.FeatureFlagRepository;
import com.example.flagsentinelapi.repository.RuleRepository;
import com.example.flagsentinelapi.websocket.WebSocketEventPublisher;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeatureFlagService {

    private final FeatureFlagRepository repo;
    private final FeatureFlagMapper mapper;
    private final RuleRepository ruleRepo;
    private final WebSocketEventPublisher ws;

    public FeatureFlagResponse create(CreateFeatureFlagRequest request) {

        if (repo.findByFlagCode(request.getFlagCode()).isPresent()) {
            throw new RuntimeException("Feature flag code already exists");
        }

        FeatureFlag flag = mapper.toEntity(request);

        if (request.getRuleCodes() != null) {
            List<Rule> rules = resolveRulesOrThrow(request.getRuleCodes());
            flag.setRules(rules);
        }

        FeatureFlag saved = repo.save(flag);
        FeatureFlagResponse response = mapper.toResponse(saved);
        ws.publishFlagUpdate(response);
        return response;
    }

    public FeatureFlagResponse update(Long id, UpdateFeatureFlagRequest request) {

        FeatureFlag flag = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Feature flag not found"));

        mapper.updateEntity(flag, request);

        if (request.getRuleCodes() != null) {
            List<Rule> rules = resolveRulesOrThrow(request.getRuleCodes());
            flag.setRules(rules);
        }

        FeatureFlag saved = repo.save(flag);
        FeatureFlagResponse response = mapper.toResponse(saved);
        ws.publishFlagUpdate(response);
        return response;
    }

    public FeatureFlagResponse getById(Long id) {
        FeatureFlag flag = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Feature flag not found"));
        return mapper.toResponse(flag);
    }

    public List<FeatureFlagResponse> getAll() {
        return repo.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public void delete(Long id) {
        FeatureFlag flag = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Feature flag not found"));
        repo.delete(flag);
        ws.publishFlagUpdate("deleted:" + id);
    }

    private List<Rule> resolveRulesOrThrow(List<Long> ruleIds) {

        List<Rule> rules = ruleRepo.findByIdIn(ruleIds);

        if (rules.size() != ruleIds.size()) {
            List<Long> foundIds = rules.stream().map(Rule::getId).toList();
            List<Long> missing = ruleIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new RuntimeException("Invalid rule IDs: " + missing);
        }

        return rules;
    }
}

