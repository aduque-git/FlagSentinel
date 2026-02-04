package org.example.flagsentinelpanel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RuleDTO {
    private Long id;
    private String attribute;
    private String operator;
    private String value;
}
