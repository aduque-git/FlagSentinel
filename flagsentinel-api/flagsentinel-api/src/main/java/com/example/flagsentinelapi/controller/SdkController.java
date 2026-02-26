package com.example.flagsentinelapi.controller;

import com.example.flagsentinelapi.dto.bootstrap.BootstrapFeatureFlagDTO;
import com.example.flagsentinelapi.dto.bootstrap.BootstrapFeatureFlagResponse;
import com.example.flagsentinelapi.dto.bootstrap.BootstrapRuleResponse;
import com.example.flagsentinelapi.service.FeatureFlagService;
import com.example.flagsentinelapi.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bootstrap")
@RequiredArgsConstructor
public class SdkController {

    private final FeatureFlagService featureFlagService;
    private final RuleService ruleService;

    @GetMapping("/flags")
    public ResponseEntity<BootstrapFeatureFlagResponse> bootstrapFlags() {
        List<BootstrapFeatureFlagDTO> flags = featureFlagService.getAllBootstrap();
        BootstrapFeatureFlagResponse dto = new BootstrapFeatureFlagResponse(flags);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/rules")
    public ResponseEntity<List<BootstrapRuleResponse>> bootstrapRules() {
        List<BootstrapRuleResponse> rules = ruleService.getAllBootstrap();
        return ResponseEntity.ok(rules);
    }
}
