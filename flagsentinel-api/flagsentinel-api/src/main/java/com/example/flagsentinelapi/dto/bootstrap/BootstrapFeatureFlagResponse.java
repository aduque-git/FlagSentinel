package com.example.flagsentinelapi.dto.bootstrap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class BootstrapFeatureFlagResponse {
    private List<BootstrapFeatureFlagDTO> flags;
}
