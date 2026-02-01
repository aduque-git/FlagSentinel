package com.example.flagsentinelapi.config;

import com.example.flagsentinelapi.util.DatabaseCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public abstract class IntegrationTestBase {

    @Autowired(required = false)
    protected DatabaseCleaner cleaner;

    @BeforeEach
    void cleanDatabase() {
        if (cleaner != null) {
            cleaner.clear();
        }
    }
}
