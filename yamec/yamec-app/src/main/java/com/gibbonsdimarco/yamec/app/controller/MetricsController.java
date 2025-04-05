package com.gibbonsdimarco.yamec.app.controller;

import com.gibbonsdimarco.yamec.app.model.mock.MockMetricsDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MetricsController {
    private final MockMetricsDataService mockMetricsDataService;

    @Autowired
    public MetricsController(MockMetricsDataService mockMetricsDataService) {
        this.mockMetricsDataService = mockMetricsDataService;
    }

    @GetMapping("/")
    public String getMetricsPage(Model model) {
        model.addAttribute("metrics", mockMetricsDataService.getCurrentMetrics());
        return "index";
    }

    @GetMapping("/api/metrics")
    @ResponseBody
    public Object getMetricsApi() {
        return mockMetricsDataService.getCurrentMetrics();
    }
}