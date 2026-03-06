package com.example.flagsentinelapi.service;

import com.example.flagsentinelapi.logging.ApiLogMessages;
import com.example.flagsentinelapi.logging.LogPropertiesKeys;
import com.example.flagsentinelapi.model.User;
import com.example.flagsentinelapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository repo;

    public CustomUserDetailsService(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.debug(ApiLogMessages.get(
                LogPropertiesKeys.AUTH_LOAD_USER_REQUEST,
                username
        ));

        User user = repo.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn(ApiLogMessages.get(
                            LogPropertiesKeys.AUTH_LOAD_USER_NOT_FOUND,
                            username
                    ));
                    return new UsernameNotFoundException("Invalid credentials");
                });

        log.info(ApiLogMessages.get(
                LogPropertiesKeys.AUTH_LOAD_USER_SUCCESS,
                user.getUsername(),
                user.getRole()
        ));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}