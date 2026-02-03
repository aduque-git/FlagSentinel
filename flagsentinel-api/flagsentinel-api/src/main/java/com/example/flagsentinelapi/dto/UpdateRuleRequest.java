package com.example.flagsentinelapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.RequiredTypes;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class UpdateRuleRequest {

    private String attribute;
    private String operator;
    private String value;

}
