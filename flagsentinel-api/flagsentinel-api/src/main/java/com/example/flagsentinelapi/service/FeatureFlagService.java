package com.example.flagsentinelapi.service;

import com.example.flagsentinelapi.dto.bootstrap.BootstrapFeatureFlagDTO;
import com.example.flagsentinelapi.dto.flag.CreateFeatureFlagRequest;
import com.example.flagsentinelapi.dto.flag.FeatureFlagDTO;
import com.example.flagsentinelapi.dto.flag.FeatureFlagResponse;
import com.example.flagsentinelapi.dto.flag.UpdateFeatureFlagRequest;
import com.example.flagsentinelapi.exception.ConflictException;
import com.example.flagsentinelapi.exception.NotFoundException;
import com.example.flagsentinelapi.mapper.FeatureFlagMapper;
import com.example.flagsentinelapi.model.FeatureFlag;
import com.example.flagsentinelapi.model.Rule;
import com.example.flagsentinelapi.repository.FeatureFlagRepository;
import com.example.flagsentinelapi.repository.RuleRepository;
import com.example.flagsentinelapi.websocket.WebSocketEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeatureFlagService {

    private final FeatureFlagRepository repo;
    private final FeatureFlagMapper mapper;
    private final RuleRepository ruleRepo;
    private final WebSocketEventPublisher ws;

    // ---------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------
    public FeatureFlagResponse create(CreateFeatureFlagRequest request) {

        // Validación de negocio → conflicto
        if (repo.findByFlagCode(request.getFlagCode()).isPresent()) {
            throw new ConflictException("Feature flag code already exists");
        }

        FeatureFlag flag = mapper.toEntity(request);

        if (request.getRuleCodes() != null) {
            List<Rule> rules = resolveRulesOrThrow(request.getRuleCodes());
            flag.setRules(rules);
        }

        FeatureFlag saved = repo.save(flag);
        FeatureFlagResponse response = mapper.toResponse(saved);

        ws.publishFlagUpdate(mapper.toBootstrapDTO(saved));
        return response;
    }

    // ---------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------
    public FeatureFlagResponse update(Long id, UpdateFeatureFlagRequest request) {

        FeatureFlag flag = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Feature flag not found"));

        mapper.updateEntity(flag, request);

        if (request.getRuleCodes() != null) {
            List<Rule> rules = resolveRulesOrThrow(request.getRuleCodes());
            flag.setRules(rules);
        }

        FeatureFlag saved = repo.save(flag);
        FeatureFlagResponse response = mapper.toResponse(saved);

        ws.publishFlagUpdate(mapper.toBootstrapDTO(saved));
        return response;
    }

    // ---------------------------------------------------------
    // GET BY ID
    // ---------------------------------------------------------
    public FeatureFlagResponse getById(Long id) {

        FeatureFlag flag = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Feature flag not found"));

        return mapper.toResponse(flag);
    }

    // ---------------------------------------------------------
    // GET ALL
    // ---------------------------------------------------------
    public List<FeatureFlagResponse> getAll() {
        return repo.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    // ---------------------------------------------------------
    // GET ALL BOOTSTRAP
    // ---------------------------------------------------------
    public List<BootstrapFeatureFlagDTO> getAllBootstrap() {
        return repo.findAll().stream()
                .map(mapper::toBootstrapDTO)
                .toList();
    }

    // ---------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------
    public void delete(Long id) {

        FeatureFlag flag = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Feature flag not found"));

        repo.delete(flag);

        ws.publishFlagUpdate("Flag_deleted:" + id);
    }

    // ---------------------------------------------------------
    // RULE RESOLUTION
    // ---------------------------------------------------------
    private List<Rule> resolveRulesOrThrow(List<Long> ruleIds) {

        List<Rule> rules = ruleRepo.findByIdIn(ruleIds);

        if (rules.size() != ruleIds.size()) {

            List<Long> foundIds = rules.stream().map(Rule::getId).toList();

            List<Long> missing = ruleIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();

            throw new NotFoundException("Invalid rule IDs: " + missing);
        }

        return rules;
    }

    public Page<FeatureFlagResponse> findAllPaged(Pageable pageable) {
        return repo.findAll(pageable)
                .map(mapper::toResponse);
    }
}
