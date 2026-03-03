package com.example.flagsentinelapi.service;

import com.example.flagsentinelapi.dto.bootstrap.BootstrapFeatureFlagDTO;
import com.example.flagsentinelapi.dto.flag.CreateFeatureFlagRequest;
import com.example.flagsentinelapi.dto.flag.FeatureFlagResponse;
import com.example.flagsentinelapi.dto.flag.UpdateFeatureFlagRequest;
import com.example.flagsentinelapi.exception.ConflictException;
import com.example.flagsentinelapi.exception.NotFoundException;
import com.example.flagsentinelapi.logging.ApiLogMessages;
import com.example.flagsentinelapi.logging.LogPropertiesKeys;
import com.example.flagsentinelapi.mapper.FeatureFlagMapper;
import com.example.flagsentinelapi.model.FeatureFlag;
import com.example.flagsentinelapi.model.Rule;
import com.example.flagsentinelapi.repository.FeatureFlagRepository;
import com.example.flagsentinelapi.repository.RuleRepository;
import com.example.flagsentinelapi.websocket.WebSocketEventPublisher;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeatureFlagService {

    private static final Logger log = LoggerFactory.getLogger(FeatureFlagService.class);

    private final FeatureFlagRepository repo;
    private final FeatureFlagMapper mapper;
    private final RuleRepository ruleRepo;
    private final WebSocketEventPublisher ws;

    // ---------------- CREATE ----------------
    public FeatureFlagResponse create(CreateFeatureFlagRequest request) {

        if (repo.findByFlagCode(request.getFlagCode()).isPresent()) {
            log.warn("Attempt to create duplicate feature flag code={}", request.getFlagCode());
            throw new ConflictException("Feature flag code already exists");
        }

        FeatureFlag flag = mapper.toEntity(request);

        if (request.getRuleCodes() != null) {
            List<Rule> rules = resolveRulesOrThrow(request.getRuleCodes());
            flag.setRules(rules);
        }

        FeatureFlag saved = repo.save(flag);

        log.info("Feature flag created id={} code={} enabled={}",
                saved.getId(), saved.getFlagCode(), saved.isEnabled());

        ws.publishFlagUpdate(mapper.toBootstrapDTO(saved));

        return mapper.toResponse(saved);
    }

    // ---------------- UPDATE ----------------
    public FeatureFlagResponse update(Long id, UpdateFeatureFlagRequest request) {

        FeatureFlag flag = repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Feature flag not found id={}", id);
                    return new NotFoundException("Feature flag not found");
                });

        mapper.updateEntity(flag, request);

        if (request.getRuleCodes() != null) {
            List<Rule> rules = resolveRulesOrThrow(request.getRuleCodes());
            flag.setRules(rules);
        }

        FeatureFlag saved = repo.save(flag);

        log.info("Feature flag updated id={} code={}",
                saved.getId(), saved.getFlagCode());

        ws.publishFlagUpdate(mapper.toBootstrapDTO(saved));

        return mapper.toResponse(saved);
    }

    // ---------------- GET ----------------
    public FeatureFlagResponse getById(Long id) {
        FeatureFlag flag = repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Feature flag not found id={}", id);
                    return new NotFoundException("Feature flag not found");
                });

        return mapper.toResponse(flag);
    }

    public List<FeatureFlagResponse> getAll() {
        List<FeatureFlagResponse> list = repo.findAll().stream()
                .map(mapper::toResponse)
                .toList();

        log.debug("Retrieved {} feature flags", list.size());

        return list;
    }

    public List<BootstrapFeatureFlagDTO> getAllBootstrap() {
        List<BootstrapFeatureFlagDTO> list = repo.findAll().stream()
                .map(mapper::toBootstrapDTO)
                .toList();

        log.debug("Retrieved {} bootstrap flags", list.size());

        return list;
    }

    // ---------------- DELETE ----------------
    public void delete(Long id) {

        FeatureFlag flag = repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Feature flag not found id={}", id);
                    return new NotFoundException("Feature flag not found");
                });

        repo.delete(flag);

        log.info("Feature flag deleted id={} code={}",
                id, flag.getFlagCode());

        ws.publishFlagUpdate("Flag_deleted:" + id);
    }

    // ---------------- RULE RESOLUTION ----------------
    private List<Rule> resolveRulesOrThrow(List<Long> ruleIds) {

        List<Rule> rules = ruleRepo.findByIdIn(ruleIds);

        if (rules.size() != ruleIds.size()) {

            List<Long> foundIds = rules.stream().map(Rule::getId).toList();

            List<Long> missing = ruleIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();

            log.warn("Invalid rule ids {}", missing);

            throw new NotFoundException("Invalid rule IDs: " + missing);
        }

        return rules;
    }

    // ---------------- PAGINATION ----------------
    public Page<FeatureFlagResponse> findAllPaged(Pageable pageable) {
        Page<FeatureFlagResponse> page = repo.findAll(pageable)
                .map(mapper::toResponse);

        log.debug("Feature flags page requested page={} size={} total={}",
                pageable.getPageNumber(), pageable.getPageSize(), page.getTotalElements());

        return page;
    }
}