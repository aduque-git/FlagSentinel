package com.example.flagsentinelapi.service;

import com.example.flagsentinelapi.dto.FeatureFlagDTO;
import com.example.flagsentinelapi.dto.FlagEvaluationRequest;
import com.example.flagsentinelapi.dto.FlagEvaluationResponse;
import com.example.flagsentinelapi.dto.RuleDTO;
import com.example.flagsentinelapi.mapper.FeatureFlagMapper;
import com.example.flagsentinelapi.mapper.FlagEvaluationMapper;
import com.example.flagsentinelapi.model.FeatureFlag;
import com.example.flagsentinelapi.model.Rule;
import com.example.flagsentinelapi.repository.FeatureFlagRepository;
import com.example.flagsentinelapi.rule.FlagEvaluator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FlagEvaluationServiceTest {

    @Mock
    private FeatureFlagRepository flagRepo;

    @Mock
    private FlagEvaluator flagEvaluator;

    @Mock
    private FlagEvaluationMapper mapper;

    @Mock
    private FeatureFlagMapper featureFlagMapper;

    @InjectMocks
    private FlagEvaluationService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    /*@Test
    void evaluate_shouldReturnNotFound_whenFlagDoesNotExist() {
        FlagEvaluationRequest req = new FlagEvaluationRequest("unknown", Map.of());

        when(flagRepo.findByKey("unknown")).thenReturn(Optional.empty());

        FlagEvaluationResponse expected =
                new FlagEvaluationResponse("unknown", false, "Flag not found");

        when(mapper.toResponse("unknown", false, "Flag not found"))
                .thenReturn(expected);

        FlagEvaluationResponse result = service.evaluate(req);

        assertEquals(expected, result);
    }

    @Test
    void evaluate_shouldDelegateToFlagEvaluator() {
        FeatureFlag entity = new FeatureFlag();
        entity.setKey("my-flag");
        entity.setEnabled(true);

        Rule rule = new Rule();
        rule.setId(1L);
        rule.setAttribute("country");
        rule.setOperator("equals");
        rule.setValue("ES");

        entity.setRules(List.of(rule));

        FlagEvaluationRequest req = new FlagEvaluationRequest("my-flag", Map.of("country", "ES"));

        when(flagRepo.findByKey("my-flag")).thenReturn(Optional.of(entity));

        FeatureFlagDTO dto = new FeatureFlagDTO(
                "my-flag",
                true,
                List.of(new RuleDTO(1L, "country", "equals", "ES"))
        );

        when(featureFlagMapper.toDTO(entity)).thenReturn(dto);
        when(flagEvaluator.isEnabledFor(dto, req.getAttributes())).thenReturn(true);

        FlagEvaluationResponse expected =
                new FlagEvaluationResponse("my-flag", true, "Rules passed");

        when(mapper.toResponse("my-flag", true, "Rules passed"))
                .thenReturn(expected);

        FlagEvaluationResponse result = service.evaluate(req);

        assertEquals(expected, result);
        verify(flagEvaluator).isEnabledFor(dto, req.getAttributes());
    }

    /*@Test
    void evaluate_shouldReturnFailed_whenEvaluatorReturnsFalse() {
        FeatureFlag entity = new FeatureFlag();
        entity.setKey("my-flag");
        entity.setEnabled(true);
        entity.setRules(List.of());

        FlagEvaluationRequest req = new FlagEvaluationRequest("my-flag", Map.of());

        when(flagRepo.findByKey("my-flag")).thenReturn(Optional.of(entity));

        FeatureFlagDTO dto = new FeatureFlagDTO("my-flag", true, List.of());
        when(featureFlagMapper.toDTO(entity)).thenReturn(dto);

        when(flagEvaluator.isEnabledFor(dto, req.getAttributes())).thenReturn(false);

        FlagEvaluationResponse expected =
                new FlagEvaluationResponse("my-flag", false, "Rules failed");

        when(mapper.toResponse("my-flag", false, "Rules failed"))
                .thenReturn(expected);

        FlagEvaluationResponse result = service.evaluate(req);

        assertEquals(expected, result);
    }*/
}
