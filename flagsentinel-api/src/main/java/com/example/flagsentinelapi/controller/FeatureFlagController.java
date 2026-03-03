package com.example.flagsentinelapi.controller;

import com.example.flagsentinelapi.dto.bootstrap.BootstrapFeatureFlagDTO;
import com.example.flagsentinelapi.dto.flag.CreateFeatureFlagRequest;
import com.example.flagsentinelapi.dto.flag.FeatureFlagResponse;
import com.example.flagsentinelapi.dto.flag.UpdateFeatureFlagRequest;
import com.example.flagsentinelapi.service.FeatureFlagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flags")
@RequiredArgsConstructor
public class FeatureFlagController {

    private final FeatureFlagService service;

    @PostMapping
    public ResponseEntity<FeatureFlagResponse> create(@RequestBody CreateFeatureFlagRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeatureFlagResponse> update(@PathVariable Long id,
                                                      @RequestBody UpdateFeatureFlagRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeatureFlagResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<FeatureFlagResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/bootstrap")
    public ResponseEntity<List<BootstrapFeatureFlagDTO>> getBootstrap() {
        return ResponseEntity.ok(service.getAllBootstrap());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/paged")
    public Page<FeatureFlagResponse> getPaged(Pageable pageable) {
        return service.findAllPaged(pageable);
    }

}