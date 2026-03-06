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

        log.debug(ApiLogMessages.get(
                LogPropertiesKeys.FLAG_CREATE_REQUEST,
                request.getFlagCode()
        ));

        if (repo.findByFlagCode(request.getFlagCode()).isPresent()) {
            log.warn(ApiLogMessages.get(
                    LogPropertiesKeys.FLAG_CREATE_DUPLICATE,
                    request.getFlagCode()
            ));
            throw new ConflictException("Feature flag code already exists");
        }

        FeatureFlag flag = mapper.toEntity(request);

        if (request.getRuleCodes() != null) {
            flag.setRules(resolveRulesOrThrow(request.getRuleCodes()));
        }

        FeatureFlag saved = repo.save(flag);

        log.info(ApiLogMessages.get(
                LogPropertiesKeys.FLAG_CREATE_SUCCESS,
                saved.getId(),
                saved.getFlagCode(),
                saved.isEnabled()
        ));

        ws.publishFlagUpdate(mapper.toBootstrapDTO(saved));

        return mapper.toResponse(saved);
    }

    // ---------------- UPDATE ----------------
    public FeatureFlagResponse update(Long id, UpdateFeatureFlagRequest request) {

        log.debug(ApiLogMessages.get(
                LogPropertiesKeys.FLAG_UPDATE_REQUEST,
                id
        ));

        FeatureFlag flag = repo.findById(id)
                .orElseThrow(() -> {
                    log.warn(ApiLogMessages.get(
                            LogPropertiesKeys.FLAG_NOT_FOUND,
                            id
                    ));
                    return new NotFoundException("Feature flag not found");
                });

        mapper.updateEntity(flag, request);

        if (request.getRuleCodes() != null) {
            flag.setRules(resolveRulesOrThrow(request.getRuleCodes()));
        }

        FeatureFlag saved = repo.save(flag);

        log.info(ApiLogMessages.get(
                LogPropertiesKeys.FLAG_UPDATE_SUCCESS,
                saved.getId(),
                saved.getFlagCode()
        ));

        ws.publishFlagUpdate(mapper.toBootstrapDTO(saved));

        return mapper.toResponse(saved);
    }

    // ---------------- GET ----------------
    public FeatureFlagResponse getById(Long id) {

        log.debug(ApiLogMessages.get(
                LogPropertiesKeys.FLAG_GET_REQUEST,
                id
        ));

        FeatureFlag flag = repo.findById(id)
                .orElseThrow(() -> {
                    log.warn(ApiLogMessages.get(
                            LogPropertiesKeys.FLAG_NOT_FOUND,
                            id
                    ));
                    return new NotFoundException("Feature flag not found");
                });

        return mapper.toResponse(flag);
    }

    public List<FeatureFlagResponse> getAll() {

        log.debug(ApiLogMessages.get(LogPropertiesKeys.FLAG_GET_ALL_REQUEST));

        List<FeatureFlagResponse> list = repo.findAll().stream()
                .map(mapper::toResponse)
                .toList();

        log.info(ApiLogMessages.get(
                LogPropertiesKeys.FLAG_GET_ALL_SUCCESS,
                list.size()
        ));

        return list;
    }

    public List<BootstrapFeatureFlagDTO> getAllBootstrap() {

        log.debug(ApiLogMessages.get(LogPropertiesKeys.FLAG_BOOTSTRAP_REQUEST));

        List<BootstrapFeatureFlagDTO> list = repo.findAll().stream()
                .map(mapper::toBootstrapDTO)
                .toList();

        log.info(ApiLogMessages.get(
                LogPropertiesKeys.FLAG_BOOTSTRAP_SUCCESS,
                list.size()
        ));

        return list;
    }

    // ---------------- DELETE ----------------
    public void delete(Long id) {

        log.debug(ApiLogMessages.get(
                LogPropertiesKeys.FLAG_DELETE_REQUEST,
                id
        ));

        FeatureFlag flag = repo.findById(id)
                .orElseThrow(() -> {
                    log.warn(ApiLogMessages.get(
                            LogPropertiesKeys.FLAG_NOT_FOUND,
                            id
                    ));
                    return new NotFoundException("Feature flag not found");
                });

        repo.delete(flag);

        log.info(ApiLogMessages.get(
                LogPropertiesKeys.FLAG_DELETE_SUCCESS,
                id,
                flag.getFlagCode()
        ));

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

            log.warn(ApiLogMessages.get(
                    LogPropertiesKeys.FLAG_INVALID_RULE_IDS,
                    missing
            ));

            throw new NotFoundException("Invalid rule IDs: " + missing);
        }

        return rules;
    }

    // ---------------- PAGINATION ----------------
    public Page<FeatureFlagResponse> findAllPaged(Pageable pageable) {

        log.debug(ApiLogMessages.get(
                LogPropertiesKeys.FLAG_PAGE_REQUEST,
                pageable.getPageNumber(),
                pageable.getPageSize()
        ));

        Page<FeatureFlagResponse> page = repo.findAll(pageable)
                .map(mapper::toResponse);

        log.info(ApiLogMessages.get(
                LogPropertiesKeys.FLAG_PAGE_SUCCESS,
                page.getTotalElements()
        ));

        return page;
    }
}