package com.example.flagsentinelapi.controller;

import com.example.flagsentinelapi.dto.flagevaluation.FlagEvaluationRequest;
import com.example.flagsentinelapi.dto.flagevaluation.FlagEvaluationResponse;
import com.example.flagsentinelapi.service.FlagEvaluationService;
import com.example.flagsentinelapi.logging.LogPropertiesKeys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/flags")
@RequiredArgsConstructor
public class FlagEvaluationController {

    private static final Logger log = LoggerFactory.getLogger(FlagEvaluationController.class);

    private final FlagEvaluationService service;

    @PostMapping("/evaluate")
    public ResponseEntity<FlagEvaluationResponse> evaluate(@RequestBody FlagEvaluationRequest request) {
        log.debug("{} - incoming request for key='{}'", LogPropertiesKeys.FLAG_REQUEST_RECEIVED,
                request != null ? request.getKey() : "null");
        return ResponseEntity.ok(service.evaluate(request));
    }
}


