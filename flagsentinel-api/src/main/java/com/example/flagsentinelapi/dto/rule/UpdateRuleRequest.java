package com.example.flagsentinelapi.dto.rule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class UpdateRuleRequest {

    private String attribute;
    private String operator;
    private String value;

}
