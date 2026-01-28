package com.example.flagsentinelapi.repository;

import com.example.flagsentinelapi.model.Rule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RuleRepository extends JpaRepository<Rule, Long> {
}
