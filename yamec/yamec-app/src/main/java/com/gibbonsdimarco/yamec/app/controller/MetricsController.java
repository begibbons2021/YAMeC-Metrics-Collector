package com.gibbonsdimarco.yamec.app.controller;

import com.gibbonsdimarco.yamec.app.model.ApplicationMetricsData;
import com.gibbonsdimarco.yamec.app.model.MetricsData;
import com.gibbonsdimarco.yamec.app.model.mock.MockMetricsDataService;
import com.gibbonsdimarco.yamec.app.service.ApplicationMetricsAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

@Controller
public class MetricsController {
    private final MockMetricsDataService metricsDataService;
    private final ApplicationMetricsAdapter applicationMetricsAdapter;

    @Autowired
    public MetricsController(MockMetricsDataService metricsDataService, 
                            ApplicationMetricsAdapter applicationMetricsAdapter) {
        this.metricsDataService = metricsDataService;
        this.applicationMetricsAdapter = applicationMetricsAdapter;
    }

    @GetMapping("/")
    public String getMetricsPage(Model model) {
        model.addAttribute("metrics", metricsDataService.getCurrentMetrics());
        return "index";
    }

    @GetMapping("/applications")
    public String getApplicationMetricsPage(Model model) {
        model.addAttribute("appMetrics", applicationMetricsAdapter.getCurrentApplicationMetrics());
        return "applications";
    }

    @GetMapping("/api/metrics")
    @ResponseBody
    public MetricsData getMetricsApi() {
        return metricsDataService.getCurrentMetrics();
    }

    @GetMapping("/api/applications")
    @ResponseBody
    public ApplicationMetricsData.ApplicationMetricsDataList getApplicationMetricsApi() {
        return applicationMetricsAdapter.getCurrentApplicationMetrics();
    }

    @GetMapping("/api/applications/{id}")
    @ResponseBody
    public ApplicationMetricsData getApplicationMetricsById(@PathVariable("id") UUID id) {
        return applicationMetricsAdapter.getApplicationMetricsById(id);
    }
}