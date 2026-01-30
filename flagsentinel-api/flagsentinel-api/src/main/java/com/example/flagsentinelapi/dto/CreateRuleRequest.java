package com.example.flagsentinelapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateRuleRequest {
    private String attribute;
    private String operator;
    private String value;
}
