package com.example.flagsentinelapi.service;

import com.example.flagsentinelapi.dto.flag.FeatureFlagDTO;
import com.example.flagsentinelapi.dto.flagevaluation.FlagEvaluationRequest;
import com.example.flagsentinelapi.dto.flagevaluation.FlagEvaluationResponse;
import com.example.flagsentinelapi.exception.BadRequestException;
import com.example.flagsentinelapi.mapper.FeatureFlagMapper;
import com.example.flagsentinelapi.mapper.FlagEvaluationMapper;
import com.example.flagsentinelapi.model.FeatureFlag;
import com.example.flagsentinelapi.repository.FeatureFlagRepository;
import com.example.flagsentinelapi.util.FlagEvaluator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class FlagEvaluationServiceTest {

    @Mock
    private FeatureFlagRepository flagRepo;

    @Mock
    private FlagEvaluator flagEvaluator;

    @Mock
    private FlagEvaluationMapper mapper;

    @Mock
    private FeatureFlagMapper flagMapper;

    @InjectMocks
    private FlagEvaluationService service;

    // ---------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------

    @Test
    @DisplayName("Should throw BadRequestException when key is null")
    void shouldThrowWhenKeyIsNull() {

        // Given
        FlagEvaluationRequest request = new FlagEvaluationRequest();
        request.setKey(null);

        // When / Then
        assertThatThrownBy(() -> service.evaluate(request))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("Should throw BadRequestException when key is blank")
    void shouldThrowWhenKeyIsBlank() {

        // Given
        FlagEvaluationRequest request = new FlagEvaluationRequest();
        request.setKey(" ");

        // When / Then
        assertThatThrownBy(() -> service.evaluate(request))
                .isInstanceOf(BadRequestException.class);
    }

    // ---------------------------------------------------------
    // FLAG NOT FOUND
    // ---------------------------------------------------------

    @Test
    @DisplayName("Should return disabled response when feature flag does not exist")
    void shouldReturnDisabledWhenFlagNotFound() {

        // Given
        String key = "new-feature";

        FlagEvaluationRequest request = new FlagEvaluationRequest();
        request.setKey(key);

        FlagEvaluationResponse response = new FlagEvaluationResponse();

        given(flagRepo.findByFlagCode(key))
                .willReturn(Optional.empty());

        given(mapper.toResponse(key, false, "Flag not found"))
                .willReturn(response);

        // When
        FlagEvaluationResponse result = service.evaluate(request);

        // Then
        assertThat(result).isEqualTo(response);

        then(flagRepo).should().findByFlagCode(key);
        then(flagEvaluator).shouldHaveNoInteractions();
    }

    // ---------------------------------------------------------
    // FLAG ENABLED
    // ---------------------------------------------------------

    @Test
    @DisplayName("Should return enabled response when rules pass")
    void shouldReturnEnabledWhenRulesPass() {

        // Given
        String key = "checkout-redesign";

        FlagEvaluationRequest request = new FlagEvaluationRequest();
        request.setKey(key);
        request.setAttributes(Map.of("country", "ES"));

        FeatureFlag flag = new FeatureFlag();
        flag.setFlagCode(key);

        FeatureFlagDTO dto = new FeatureFlagDTO();
        FlagEvaluationResponse response = new FlagEvaluationResponse();

        given(flagRepo.findByFlagCode(key))
                .willReturn(Optional.of(flag));

        given(flagMapper.toDTO(flag))
                .willReturn(dto);

        given(flagEvaluator.isEnabledFor(dto, request.getAttributes()))
                .willReturn(true);

        given(mapper.toResponse(key, true, "Rules passed"))
                .willReturn(response);

        // When
        FlagEvaluationResponse result = service.evaluate(request);

        // Then
        assertThat(result).isEqualTo(response);

        then(flagEvaluator).should().isEnabledFor(dto, request.getAttributes());
    }

    // ---------------------------------------------------------
    // FLAG DISABLED
    // ---------------------------------------------------------

    @Test
    @DisplayName("Should return disabled response when rules fail")
    void shouldReturnDisabledWhenRulesFail() {

        // Given
        String key = "checkout-redesign";

        FlagEvaluationRequest request = new FlagEvaluationRequest();
        request.setKey(key);

        FeatureFlag flag = new FeatureFlag();
        flag.setFlagCode(key);

        FeatureFlagDTO dto = new FeatureFlagDTO();
        FlagEvaluationResponse response = new FlagEvaluationResponse();

        given(flagRepo.findByFlagCode(key))
                .willReturn(Optional.of(flag));

        given(flagMapper.toDTO(flag))
                .willReturn(dto);

        given(flagEvaluator.isEnabledFor(dto, request.getAttributes()))
                .willReturn(false);

        given(mapper.toResponse(key, false, "Rules failed"))
                .willReturn(response);

        // When
        FlagEvaluationResponse result = service.evaluate(request);

        // Then
        assertThat(result).isEqualTo(response);
    }
}