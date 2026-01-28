package com.example.flagsentinelapi.controller;

import com.example.flagsentinelapi.dto.CreateRuleRequest;
import com.example.flagsentinelapi.dto.RuleResponse;
import com.example.flagsentinelapi.dto.UpdateRuleRequest;
import com.example.flagsentinelapi.mapper.RuleMapper;
import com.example.flagsentinelapi.model.Rule;
import com.example.flagsentinelapi.service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/rules")
public class RuleController {

    @Autowired
    private RuleService service;

    public RuleController(RuleService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<RuleResponse>> getAll() {
        List<Rule> rules = service.getAll();
        List<RuleResponse> responses = new ArrayList<>();

        for (Rule rule : rules) {
            responses.add(RuleMapper.toResponse(rule));
        }

        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<RuleResponse> create(@RequestBody CreateRuleRequest request) {
        Rule rule = RuleMapper.toEntity(request);
        Rule saved = service.create(rule);

        return new ResponseEntity<>(RuleMapper.toResponse(saved), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RuleResponse> update(
            @PathVariable("id") Long id,
            @RequestBody UpdateRuleRequest request
    ) {
        Rule existing = service.getById(id);
        if (existing == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Rule updated = RuleMapper.toEntity(request);
        updated.setId(id);

        Rule saved = service.update(id, updated);

        return new ResponseEntity<>(RuleMapper.toResponse(saved), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        boolean deleted = service.delete(id);
        if (!deleted) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
