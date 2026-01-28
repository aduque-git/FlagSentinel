package com.example.flagsentinelapi.repository;

import com.example.flagsentinelapi.model.FeatureFlag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeatureFlagRepository extends JpaRepository<FeatureFlag, Long> {

    FeatureFlag findByKey(String key);
}
