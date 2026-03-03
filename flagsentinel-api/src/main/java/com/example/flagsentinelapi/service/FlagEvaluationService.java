package com.example.flagsentinelapi.service;

import com.example.flagsentinelapi.dto.flagevaluation.FlagEvaluationRequest;
import com.example.flagsentinelapi.dto.flagevaluation.FlagEvaluationResponse;
import com.example.flagsentinelapi.exception.BadRequestException;
import com.example.flagsentinelapi.mapper.FeatureFlagMapper;
import com.example.flagsentinelapi.mapper.FlagEvaluationMapper;
import com.example.flagsentinelapi.model.FeatureFlag;
import com.example.flagsentinelapi.repository.FeatureFlagRepository;
import com.example.flagsentinelapi.util.FlagEvaluator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FlagEvaluationService {

    private static final Logger log = LoggerFactory.getLogger(FlagEvaluationService.class);

    private final FeatureFlagRepository flagRepo;
    private final FlagEvaluator flagEvaluator;
    private final FlagEvaluationMapper mapper;
    private final FeatureFlagMapper flagMapper;

    public FlagEvaluationResponse evaluate(FlagEvaluationRequest request) {

        String key = request.getKey();

        // =========================
        // VALIDATION
        // =========================
        if (key == null || key.isBlank()) {
            log.warn("Flag evaluation rejected — empty key");
            throw new BadRequestException("Flag key cannot be empty");
        }

        log.debug("Evaluating feature flag key='{}'", key);

        // =========================
        // FETCH FLAG
        // =========================
        FeatureFlag flag = flagRepo.findByFlagCode(key).orElse(null);

        if (flag == null) {
            log.info("Feature flag not found key='{}'", key);
            return mapper.toResponse(key, false, "Flag not found");
        }

        // =========================
        // EVALUATION
        // =========================
        boolean enabled = flagEvaluator.isEnabledFor(
                flagMapper.toDTO(flag),
                request.getAttributes()
        );

        log.info("Feature flag evaluated key='{}' enabled='{}'", key, enabled);

        // =========================
        // RESPONSE
        // =========================
        return mapper.toResponse(
                flag.getFlagCode(),
                enabled,
                enabled ? "Rules passed" : "Rules failed"
        );
    }
}