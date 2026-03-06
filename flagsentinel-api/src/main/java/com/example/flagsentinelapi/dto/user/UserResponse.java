package com.example.flagsentinelapi.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String username;
    private String role;

    public UserResponse() {}
}
