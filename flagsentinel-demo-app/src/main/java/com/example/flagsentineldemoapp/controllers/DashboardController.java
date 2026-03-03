package com.example.flagsentineldemoapp.controllers;

import com.example.flagsentineldemoapp.model.DashboardEvent;
import com.example.flagsentineldemoapp.service.DashboardService;
import org.example.dto.BootstrapFeatureFlagDTO;
import org.example.dto.BootstrapRuleResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/dashboard", produces = "application/json")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/flags")
    public Collection<BootstrapFeatureFlagDTO> flags() {
        return dashboardService.getFlags().values();
    }

    @GetMapping("/rules")
    public Collection<BootstrapRuleResponse> rules() {
        return dashboardService.getRules().values();
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        return Map.of(
                "connected", dashboardService.isConnected(),
                "timestamp", System.currentTimeMillis()
        );
    }

    @GetMapping("/events")
    public List<DashboardEvent> events() {
        return dashboardService.getLastEvents(50);
    }
}
