package com.example.flagsentinelapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "feature_flags")
public class FeatureFlag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String flagCode;

    @Column(nullable = false)
    private boolean enabled;

    @ManyToMany
    @JoinTable(name = "feature_flag_rules", joinColumns = @JoinColumn(name = "feature_flag_id"),
            inverseJoinColumns = @JoinColumn(name = "rule_id"))
    private List<Rule> rules = new ArrayList<>();


    public void setRules(List<Rule> newRules) {
        if (newRules == null) {
            return;
        }
        this.rules.removeIf(rule -> !newRules.contains(rule));

        for (Rule rule : newRules) {
            if (!this.rules.contains(rule)) {
                this.rules.add(rule);
                rule.getFeatureFlags().add(this);
            }
        }
    }

    public void addRule(Rule rule) {
        if (!this.rules.contains(rule)) {
            this.rules.add(rule);
            rule.getFeatureFlags().add(this);
        }
    }

    public void removeRule(Rule rule) {
        if (this.rules.contains(rule)) {
            this.rules.remove(rule);
            rule.getFeatureFlags().remove(this);
        }
    }

}
