package com.gibbonsdimarco.yamec.app.controller;

import com.gibbonsdimarco.yamec.app.model.MetricsData;
import com.gibbonsdimarco.yamec.app.service.RealMetricsDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MetricsController {
    private final RealMetricsDataService metricsDataService;

    @Autowired
    public MetricsController(RealMetricsDataService metricsDataService) {
        this.metricsDataService = metricsDataService;
    }

    @GetMapping("/")
    public String getMetricsPage(Model model) {
        model.addAttribute("metrics", metricsDataService.getCurrentMetrics());
        return "index";
    }

    @GetMapping("/api/metrics")
    @ResponseBody
    public MetricsData getMetricsApi() {
        return metricsDataService.getCurrentMetrics();
    }
}