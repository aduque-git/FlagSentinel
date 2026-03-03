package org.example.flagsentinelpanel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateFeatureFlagRequest {

    private String flagCode;
    private boolean enabled;
    private List<Long> ruleCodes;

}
