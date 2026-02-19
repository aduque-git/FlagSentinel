package com.example.flagsentinelapi.service;

import com.example.flagsentinelapi.dto.FlagEvaluationRequest;
import com.example.flagsentinelapi.dto.FlagEvaluationResponse;
import com.example.flagsentinelapi.exception.BadRequestException;
import com.example.flagsentinelapi.mapper.FeatureFlagMapper;
import com.example.flagsentinelapi.mapper.FlagEvaluationMapper;
import com.example.flagsentinelapi.model.FeatureFlag;
import com.example.flagsentinelapi.repository.FeatureFlagRepository;
import com.example.flagsentinelapi.util.FlagEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FlagEvaluationService {

    private final FeatureFlagRepository flagRepo;
    private final FlagEvaluator flagEvaluator = new FlagEvaluator();
    private final FlagEvaluationMapper mapper;
    private final FeatureFlagMapper flagMapper;

    public FlagEvaluationResponse evaluate(FlagEvaluationRequest request) {

        // Validación mínima del request
        if (request.getKey() == null || request.getKey().isBlank()) {
            throw new BadRequestException("Flag key cannot be empty");
        }
        System.out.println("PASO 1");
        // 1. Buscar flag
        FeatureFlag flag = flagRepo.findByFlagCode(request.getKey()).orElse(null);


        System.out.println("PASO 2");
        if (flag == null) {

            System.out.println("PASO 3");
            return mapper.toResponse(request.getKey(), false, "Flag not found");
        }


        System.out.println("PASO 4");
        // 2. Evaluar reglas
        boolean enabled = flagEvaluator.isEnabledFor(
                flagMapper.toDTO(flag),
                request.getAttributes()
        );

        System.out.println("PASO 5");


        return mapper.toResponse(
                flag.getFlagCode(),
                enabled,
                enabled ? "Rules passed" : "Rules failed"
        );
    }
}
