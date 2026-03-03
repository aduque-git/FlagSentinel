package com.example.flagsentinelapi.controller;

import com.example.flagsentinelapi.dto.ruleoperator.OperatorDTO;
import com.example.flagsentinelapi.dto.ruleoperator.RuleOperator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rules/operators")
public class RuleOperatorController {

    @GetMapping
    public List<OperatorDTO> getOperators() {
        return RuleOperator.asDtoList();
    }
}
