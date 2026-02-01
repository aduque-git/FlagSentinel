package com.example.flagsentinelapi.controller;

import com.example.flagsentinelapi.dto.FlagEvaluationRequest;
import com.example.flagsentinelapi.dto.FlagEvaluationResponse;
import com.example.flagsentinelapi.service.FlagEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/flags")
@RequiredArgsConstructor
public class FlagEvaluationController {

    private final FlagEvaluationService service;

    @PostMapping("/evaluate")
    public ResponseEntity<FlagEvaluationResponse> evaluate(@RequestBody FlagEvaluationRequest request) {
        return ResponseEntity.ok(service.evaluate(request));
    }
}


