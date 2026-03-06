package com.example.flagsentineldemoapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardPageController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard.html"; // Spring lo busca en /static o /public
    }
}
