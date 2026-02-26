package com.example.flagsentinelapi.dto.rule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RuleResponse {

    private Long id;
    private String attribute;
    private String operator;
    private String value;

}
