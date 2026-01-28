package com.example.flagsentinelapi.service;

import com.example.flagsentinelapi.model.FeatureFlag;
import com.example.flagsentinelapi.repository.FeatureFlagRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeatureFlagService {

    private final FeatureFlagRepository repository;

    public FeatureFlagService(FeatureFlagRepository repository) {
        this.repository = repository;
    }

    public List<FeatureFlag> getAll() {
        return repository.findAll();
    }

    public FeatureFlag getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public FeatureFlag getByKey(String key) {
        return repository.findByKey(key);
    }

    public FeatureFlag create(FeatureFlag flag) {
        return repository.save(flag);
    }

    public FeatureFlag update(Long id, FeatureFlag updated) {
        FeatureFlag existing = repository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }

        existing.setKey(updated.getKey());
        existing.setEnabled(updated.isEnabled());
        existing.setRules(updated.getRules());

        return repository.save(existing);
    }

    public boolean delete(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }
}
