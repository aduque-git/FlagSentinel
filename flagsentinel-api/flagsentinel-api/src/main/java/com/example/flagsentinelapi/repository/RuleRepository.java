package com.example.flagsentinelapi.repository;

import com.example.flagsentinelapi.model.Rule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RuleRepository extends JpaRepository<Rule, Long> {
    List<Rule> findByIdIn(List<Long> codes);
}
