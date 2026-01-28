package com.example.flagsentinelapi.controller;

import com.example.flagsentinelapi.dto.FlagEvaluationResponse;
import com.example.flagsentinelapi.service.FlagEvaluationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/flags")
public class FlagEvaluationController {

    private final FlagEvaluationService evaluationService;

    public FlagEvaluationController(FlagEvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @GetMapping("/{key}/evaluate")
    public ResponseEntity<FlagEvaluationResponse> evaluate(
            @PathVariable("key") String key,
            @RequestParam Map<String, String> params
    ) {
        Map<String, Object> context = new HashMap<>(params);

        boolean enabled = evaluationService.evaluate(key, context);

        return new ResponseEntity<>(
                new FlagEvaluationResponse(enabled),
                HttpStatus.OK
        );
    }
}
