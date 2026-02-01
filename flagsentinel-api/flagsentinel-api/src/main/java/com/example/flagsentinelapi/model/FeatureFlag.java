package com.example.flagsentinelapi.model;

import jakarta.persistence.*;
import lombok.*;

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
    private String key;

    @Column(nullable = false)
    private boolean enabled;

    @OneToMany(mappedBy = "featureFlag", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rule> rules = new ArrayList<>();


    public void setRules(List<Rule> rules) {
        this.rules.clear();
        if (rules != null) {
            for (Rule rule : rules) {
                addRule(rule);
            }
        }
    }

    public void addRule(Rule rule) {
        rule.setFeatureFlag(this);
        this.rules.add(rule);
    }

    public void removeRule(Rule rule) {
        rule.setFeatureFlag(null);
        this.rules.remove(rule);
    }

}
