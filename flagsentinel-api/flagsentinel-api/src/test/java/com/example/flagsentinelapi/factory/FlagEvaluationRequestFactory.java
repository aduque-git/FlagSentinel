package com.example.flagsentinelapi.factory;

import com.example.flagsentinelapi.dto.FlagEvaluationRequest;

import java.util.HashMap;
import java.util.Map;

public class FlagEvaluationRequestFactory {

    public static FlagEvaluationRequest createRequest(String key) {
        Map<String, String> context = new HashMap<>();
        context.put("country", "ES");
        return new FlagEvaluationRequest(key, context);
    }
}
