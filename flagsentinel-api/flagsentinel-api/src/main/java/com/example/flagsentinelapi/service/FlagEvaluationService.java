package com.example.flagsentinelapi.service;

import com.example.flagsentinelapi.dto.FlagEvaluationRequest;
import com.example.flagsentinelapi.dto.FlagEvaluationResponse;
import com.example.flagsentinelapi.mapper.FeatureFlagMapper;
import com.example.flagsentinelapi.mapper.FlagEvaluationMapper;
import com.example.flagsentinelapi.model.FeatureFlag;
import com.example.flagsentinelapi.repository.FeatureFlagRepository;
import com.example.flagsentinelapi.rule.FlagEvaluator;
import org.springframework.stereotype.Service;

@Service
public class FlagEvaluationService {

    private final FeatureFlagRepository flagRepo;
    private final FlagEvaluator flagEvaluator;
    private final FlagEvaluationMapper mapper;
    private final FeatureFlagMapper flagMapper;

    public FlagEvaluationService(FlagEvaluationMapper mapper, FeatureFlagRepository flagRepo, FeatureFlagMapper flagMapper) {
        this.mapper = mapper;
        this.flagRepo = flagRepo;
        this.flagMapper = flagMapper;
        this.flagEvaluator = new FlagEvaluator();
    }

    public FlagEvaluationResponse evaluate(FlagEvaluationRequest request) {

        // 1. Buscar flag
        FeatureFlag flag = flagRepo.findByFlagCode(request.getKey()).orElse(null);
        if (flag == null) {
            return mapper.toResponse(request.getKey(), false, "Flag not found");
        }

        // 2. Delegar evaluación completa al FlagEvaluator


        boolean enabled = flagEvaluator.isEnabledFor(flagMapper.toDTO(flag), request.getAttributes());
        return mapper.toResponse(flag.getFlagCode(), enabled, enabled ? "Rules passed" : "Rules failed");
    }
}
