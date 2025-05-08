package com.gibbonsdimarco.yamec.app.service;

import com.gibbonsdimarco.yamec.app.data.Application;
import com.gibbonsdimarco.yamec.app.data.ApplicationMetric;
import com.gibbonsdimarco.yamec.app.model.ApplicationMetricsData;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

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
     * Returns all Application metrics data per application aggregated over the timespan specified by the
     * startTime and endTime parameters in a JSON-ready object format
     * @param startTime The <code>Timestamp</code> which application data to be returned starts
     * @param endTime The <code>Timestamp</code> which application data to be returned ends
     * @return An <code>ApplicationMetricsDataList</code> containing the collection of applications and their
     *          metrics data
     */
    @Transactional
    public ApplicationMetricsData.ApplicationMetricsDataList getHistoricalApplicationMetrics(Timestamp startTime,
                                                                                             Timestamp endTime) {
        HashMap<Application, List<ApplicationMetric>> applicationMetricsMap = new HashMap<>();

        List<ApplicationMetric> validMetrics = applicationDataService.getStoredApplicationMetrics(startTime, endTime);

        // Move metrics to hash map
        for (ApplicationMetric metric : validMetrics) {
            if (!applicationMetricsMap.containsKey(metric.getApplication())) {
                applicationMetricsMap.put(metric.getApplication(), new ArrayList<>());
            }
            applicationMetricsMap.get(metric.getApplication()).add(metric);
        }

        ApplicationMetricsData.ApplicationMetricsDataList result = new ApplicationMetricsData.ApplicationMetricsDataList();


        for (Application application : applicationMetricsMap.keySet()) {
            // Aggregate data for the application over the timespan selected

            int numApplications = applicationMetricsMap.get(application).size();
            // Create buffers for application data collected for an application over the timespan
            double totalCpuUsage = 0;
            double maxCpuUsage = 0.0;
            double minCpuUsage = Double.MAX_VALUE;

            long totalPhysicalMemoryUsed = 0;
            long maxPhysicalMemoryUsed = Long.MIN_VALUE;
            long minPhysicalMemoryUsed = Long.MAX_VALUE;

            long totalVirtualMemoryUsed = 0;
            long maxVirtualMemoryUsed = Long.MIN_VALUE;
            long minVirtualMemoryUsed = Long.MAX_VALUE;

            for (ApplicationMetric metric : applicationMetricsMap.get(application)) {
                totalCpuUsage += metric.getAvgCpuUsage();
                totalPhysicalMemoryUsed += metric.getAvgPhysicalMemoryUsed();
                totalVirtualMemoryUsed += metric.getAvgVirtualMemoryUsed();

                if (metric.getMaxCpuUsage() > maxCpuUsage) {
                    maxCpuUsage = metric.getMaxCpuUsage();
                }

                if (metric.getMinCpuUsage() < minCpuUsage) {
                    minCpuUsage = metric.getMinCpuUsage();
                }

                if (metric.getMaxPhysicalMemoryUsed() > maxPhysicalMemoryUsed) {
                    maxPhysicalMemoryUsed = metric.getMaxPhysicalMemoryUsed();
                }

                if (metric.getMinPhysicalMemoryUsed() < minPhysicalMemoryUsed) {
                    minPhysicalMemoryUsed = metric.getMinPhysicalMemoryUsed();
                }

                if (metric.getMaxVirtualMemoryUsed() > maxVirtualMemoryUsed) {
                    maxVirtualMemoryUsed = metric.getMaxVirtualMemoryUsed();
                }

                if (metric.getMinVirtualMemoryUsed() < minVirtualMemoryUsed) {
                    minVirtualMemoryUsed = metric.getMinVirtualMemoryUsed();
                }
            }

            // Set up entry in ApplicationMetricsDataList for this Application
            ApplicationMetricsData appData = new ApplicationMetricsData();

            appData.setId(application.getId());
            appData.setApplicationName(application.getApplicationName());
            appData.setAvgCpuUsage(totalCpuUsage / (double)numApplications);
            appData.setAvgPhysicalMemoryUsed((long)Math.ceil(totalPhysicalMemoryUsed / (double)numApplications));
            appData.setAvgVirtualMemoryUsed((long)Math.ceil(totalVirtualMemoryUsed / (double)numApplications));
            appData.setMaxCpuUsage(maxCpuUsage);
            appData.setMaxPhysicalMemoryUsed(maxPhysicalMemoryUsed);
            appData.setMaxVirtualMemoryUsed(maxVirtualMemoryUsed);
            appData.setMinCpuUsage(minCpuUsage);
            appData.setMinPhysicalMemoryUsed(minPhysicalMemoryUsed);
            appData.setMinVirtualMemoryUsed(minVirtualMemoryUsed);

            // Add to return list
            result.addApplication(appData);

        }
        // All ApplicationMetrics are aggregated and ready to return

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