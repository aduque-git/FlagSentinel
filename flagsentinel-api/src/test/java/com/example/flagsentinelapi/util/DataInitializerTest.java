package com.example.flagsentinelapi.util;

import com.example.flagsentinelapi.model.FeatureFlag;
import com.example.flagsentinelapi.model.Rule;
import com.example.flagsentinelapi.repository.FeatureFlagRepository;
import com.example.flagsentinelapi.repository.RuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private RuleRepository ruleRepo;

    @Mock
    private FeatureFlagRepository flagRepo;

    @InjectMocks
    private DataInitializer initializer;

    @Test
    void shouldInitializeDataWhenDatabaseIsEmpty() throws Exception {

        when(ruleRepo.count()).thenReturn(0L);
        when(flagRepo.count()).thenReturn(0L);

        Rule savedRule = new Rule();
        savedRule.setId(1L);

        FeatureFlag savedFlag = new FeatureFlag();
        savedFlag.setId(1L);

        when(ruleRepo.save(any(Rule.class))).thenReturn(savedRule);
        when(flagRepo.save(any(FeatureFlag.class))).thenReturn(savedFlag);

        initializer.run();

        verify(ruleRepo).save(any(Rule.class));
        verify(flagRepo).save(any(FeatureFlag.class));
    }

    @Test
    void shouldSkipInitializationWhenRulesExist() throws Exception {

        when(ruleRepo.count()).thenReturn(1L);
        when(flagRepo.count()).thenReturn(0L);

        initializer.run();

        verify(ruleRepo, never()).save(any());
        verify(flagRepo, never()).save(any());
    }

    @Test
    void shouldSkipInitializationWhenFlagsExist() throws Exception {

        when(ruleRepo.count()).thenReturn(0L);
        when(flagRepo.count()).thenReturn(1L);

        initializer.run();

        verify(ruleRepo, never()).save(any());
        verify(flagRepo, never()).save(any());
    }
}