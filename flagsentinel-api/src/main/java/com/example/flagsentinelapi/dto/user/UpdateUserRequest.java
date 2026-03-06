package com.example.flagsentinelapi.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdateUserRequest {
    private String username;
    private String role;

    public UpdateUserRequest() {
    }
}
