package com.example.flagsentinelapi.service;

import com.example.flagsentinelapi.model.Rule;
import org.springframework.transaction.annotation.Transactional;

import com.example.flagsentinelapi.repository.RuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class RuleService {

    private final RuleRepository repository;

    public RuleService(RuleRepository repository) {
        this.repository = repository;
    }

    public List<Rule> getAll() {
        return repository.findAll();
    }

    public Rule getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Rule create(Rule rule) {
        return repository.save(rule);
    }

    public Rule update(Long id, Rule updatedRule) {
        Rule existing = repository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setAttribute(updatedRule.getAttribute());
        existing.setOperator(updatedRule.getOperator());
        existing.setValue(updatedRule.getValue());
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
