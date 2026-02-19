package com.example.flagsentinelapi.controller;

import com.example.flagsentinelapi.dto.OperatorDTO;
import com.example.flagsentinelapi.dto.RuleOperator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rules/operators")
public class RuleOperatorController {

    @GetMapping
    public List<OperatorDTO> getOperators() {
        return RuleOperator.asDtoList();
    }
}
