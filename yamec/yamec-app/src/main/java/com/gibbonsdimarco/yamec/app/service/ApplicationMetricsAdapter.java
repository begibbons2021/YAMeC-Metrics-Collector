package com.gibbonsdimarco.yamec.app.service;

import com.gibbonsdimarco.yamec.app.data.Application;
import com.gibbonsdimarco.yamec.app.data.ApplicationMetric;
import com.gibbonsdimarco.yamec.app.model.ApplicationMetricsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Adapter service that converts JPA entities to data objects for the view layer
 */
@Service
public class ApplicationMetricsAdapter {

    private final ApplicationDataService applicationDataService;

    @Autowired
    public ApplicationMetricsAdapter(ApplicationDataService applicationDataService) {
        this.applicationDataService = applicationDataService;
    }



    /**
     * Converts the real application data to the format expected by the views
     * @return List of application metrics data sorted by CPU usage (highest first)
     */
    public ApplicationMetricsData.ApplicationMetricsDataList getCurrentApplicationMetrics() {
        ApplicationMetricsData.ApplicationMetricsDataList result = new ApplicationMetricsData.ApplicationMetricsDataList();

        // Get all applications with their latest metrics
        Map<Application, ApplicationMetric> applicationsWithMetrics =
                applicationDataService.getAllApplicationsWithLatestMetrics();

        // Convert each entry to ApplicationMetricsData and add to a list for sorting
        java.util.List<ApplicationMetricsData> appDataList = new java.util.ArrayList<>();

        for (Map.Entry<Application, ApplicationMetric> entry : applicationsWithMetrics.entrySet()) {
            Application app = entry.getKey();
            ApplicationMetric metric = entry.getValue();

            // Skip entries without metrics
            if (metric == null) continue;

            // Skip the "Idle" application
            if (app.getApplicationName().equals("Idle")) continue;

            ApplicationMetricsData appData = new ApplicationMetricsData();
            appData.setId(app.getId());
            appData.setApplicationName(app.getApplicationName());

            // Set metrics
            appData.setAvgCpuUsage(metric.getAvgCpuUsage());
            appData.setAvgPhysicalMemoryUsed(metric.getAvgPhysicalMemoryUsed());
            appData.setAvgVirtualMemoryUsed(metric.getAvgVirtualMemoryUsed());
            appData.setMaxCpuUsage(metric.getMaxCpuUsage());
            appData.setMaxPhysicalMemoryUsed(metric.getMaxPhysicalMemoryUsed());
            appData.setMaxVirtualMemoryUsed(metric.getMaxVirtualMemoryUsed());
            appData.setMinCpuUsage(metric.getMinCpuUsage());
            appData.setMinPhysicalMemoryUsed(metric.getMinPhysicalMemoryUsed());
            appData.setMinVirtualMemoryUsed(metric.getMinVirtualMemoryUsed());

            appDataList.add(appData);
        }

        // Sort the list by average CPU usage in descending order
        appDataList.sort((a, b) -> {
            if (a.getAvgCpuUsage() == 0 && b.getAvgCpuUsage() == 0) {
                return 0;
            } else if (a.getAvgCpuUsage() == 0) {
                return 1; // Null values come last
            } else if (b.getAvgCpuUsage() == 0) {
                return -1; // Null values come last
            }
            return Double.compare(b.getAvgCpuUsage(), a.getAvgCpuUsage()); // Descending order
        });

        // Add the sorted applications to the result
        for (ApplicationMetricsData appData : appDataList) {
            result.addApplication(appData);
        }

        return result;
    }

    /**
     * Get metrics for a specific application by ID
     * @param id Application ID
     * @return Application metrics data
     */
    public ApplicationMetricsData getApplicationMetricsById(UUID id) {
        // Get the application with its metrics
        Map.Entry<Application, List<ApplicationMetric>> entry =
                applicationDataService.getApplicationWithMetricsById(id);

        // Return null if not found
        if (entry == null) return null;

        Application app = entry.getKey();
        List<ApplicationMetric> metrics = entry.getValue();

        // Create the data object
        ApplicationMetricsData appData = new ApplicationMetricsData();
        appData.setId(app.getId());
        appData.setApplicationName(app.getApplicationName());

        // If we have metrics, use the latest one
        if (!metrics.isEmpty()) {
            // Find the most recent metric
            ApplicationMetric latestMetric = metrics.getFirst(); // Assuming sorted by timestamp desc

            // Set metrics
            appData.setAvgCpuUsage(latestMetric.getAvgCpuUsage());
            appData.setAvgPhysicalMemoryUsed(latestMetric.getAvgPhysicalMemoryUsed());
            appData.setAvgVirtualMemoryUsed(latestMetric.getAvgVirtualMemoryUsed());
            appData.setMaxCpuUsage(latestMetric.getMaxCpuUsage());
            appData.setMaxPhysicalMemoryUsed(latestMetric.getMaxPhysicalMemoryUsed());
            appData.setMaxVirtualMemoryUsed(latestMetric.getMaxVirtualMemoryUsed());
            appData.setMinCpuUsage(latestMetric.getMinCpuUsage());
            appData.setMinPhysicalMemoryUsed(latestMetric.getMinPhysicalMemoryUsed());
            appData.setMinVirtualMemoryUsed(latestMetric.getMinVirtualMemoryUsed());
        }

        return appData;
    }
}