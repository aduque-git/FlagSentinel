package com.example.flagsentinelapi.util;

import com.example.flagsentinelapi.dto.user.Role;
import com.example.flagsentinelapi.logging.ApiLogMessages;
import com.example.flagsentinelapi.logging.LogPropertiesKeys;
import com.example.flagsentinelapi.model.User;
import com.example.flagsentinelapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        long userCount = userRepository.count();

        if (userCount > 0) {
            log.debug(ApiLogMessages.get(
                    LogPropertiesKeys.ADMIN_INIT_SKIPPED,
                    userCount
            ));
            return;
        }

        log.info(ApiLogMessages.get(LogPropertiesKeys.ADMIN_INIT_START));

        User admin = new User();
        admin.setUsername(adminUsername);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole(Role.ADMIN);

        userRepository.save(admin);

        log.info(ApiLogMessages.get(
                LogPropertiesKeys.ADMIN_INIT_CREATED,
                adminUsername
        ));
    }
}