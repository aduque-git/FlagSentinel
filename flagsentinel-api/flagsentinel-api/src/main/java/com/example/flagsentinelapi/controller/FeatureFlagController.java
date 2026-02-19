package com.example.flagsentinelapi.controller;

import com.example.flagsentinelapi.dto.CreateFeatureFlagRequest;
import com.example.flagsentinelapi.dto.FeatureFlagResponse;
import com.example.flagsentinelapi.dto.UpdateFeatureFlagRequest;
import com.example.flagsentinelapi.dto.UserResponse;
import com.example.flagsentinelapi.mapper.FeatureFlagMapper;
import com.example.flagsentinelapi.model.FeatureFlag;
import com.example.flagsentinelapi.service.FeatureFlagService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    /*
     * DEbo ddejarr este endpoint solo para updatear los datos de la propia flag, para añadir reglas
     *  a un flag creado tengo que crear otro endpoint
     */
    @PutMapping("/{id}")
    public ResponseEntity<FeatureFlagResponse> update(@PathVariable Long id, @RequestBody UpdateFeatureFlagRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping
    public ResponseEntity<List<FeatureFlagResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeatureFlagResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
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
