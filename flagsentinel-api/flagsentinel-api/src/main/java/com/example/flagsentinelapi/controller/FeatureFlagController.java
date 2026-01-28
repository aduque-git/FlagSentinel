package com.example.flagsentinelapi.controller;

import com.example.flagsentinelapi.dto.CreateFeatureFlagRequest;
import com.example.flagsentinelapi.dto.FeatureFlagResponse;
import com.example.flagsentinelapi.dto.UpdateFeatureFlagRequest;
import com.example.flagsentinelapi.mapper.FeatureFlagMapper;
import com.example.flagsentinelapi.model.FeatureFlag;
import com.example.flagsentinelapi.service.FeatureFlagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/flags")
public class FeatureFlagController {

    @Autowired
    private FeatureFlagService service;


    @GetMapping
    public ResponseEntity<List<FeatureFlagResponse>> getAll() {
        List<FeatureFlag> flags = service.getAll();
        List<FeatureFlagResponse> responses = new ArrayList<>();

        for (FeatureFlag flag : flags) {
            responses.add(FeatureFlagMapper.toResponse(flag));
        }

        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/{key}")
    public ResponseEntity<FeatureFlagResponse> getByKey(@PathVariable("key") String key) {
        FeatureFlag flag = service.getByKey(key);
        if (flag == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(FeatureFlagMapper.toResponse(flag), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<FeatureFlagResponse> create(@RequestBody CreateFeatureFlagRequest request) {
        FeatureFlag flag = FeatureFlagMapper.toEntity(request);
        FeatureFlag saved = service.create(flag);

        return new ResponseEntity<>(FeatureFlagMapper.toResponse(saved), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeatureFlagResponse> update(
            @PathVariable("id") Long id,
            @RequestBody UpdateFeatureFlagRequest request
    ) {
        FeatureFlag existing = service.getById(id);
        if (existing == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        FeatureFlag updated = FeatureFlagMapper.toEntity(request, existing);
        FeatureFlag saved = service.update(id, updated);

        return new ResponseEntity<>(FeatureFlagMapper.toResponse(saved), HttpStatus.OK);
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
