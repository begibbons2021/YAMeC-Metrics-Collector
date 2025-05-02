package com.gibbonsdimarco.yamec.app.model.mock;

import com.gibbonsdimarco.yamec.app.model.ApplicationMetricsData;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
public class MockApplicationMetricsDataService {
    private final Random random = new Random();
    private final List<String> mockApplicationNames = Arrays.asList(
            "Chrome", "Firefox", "VSCode", "IntelliJ IDEA", "Spotify", 
            "Discord", "Slack", "Steam", "Microsoft Word", "Excel"
    );

    // Map to store consistent IDs for each application
    private final Map<String, UUID> applicationIds = new HashMap<>();

    // Initialize application IDs
    {
        for (String appName : mockApplicationNames) {
            applicationIds.put(appName, UUID.randomUUID());
        }
    }

    public ApplicationMetricsData.ApplicationMetricsDataList getCurrentApplicationMetrics() {
        ApplicationMetricsData.ApplicationMetricsDataList metricsList = new ApplicationMetricsData.ApplicationMetricsDataList();

        // Generate mock data for each application
        for (String appName : mockApplicationNames) {
            ApplicationMetricsData metrics = new ApplicationMetricsData();

            // Set application info
            metrics.setId(applicationIds.get(appName));
            metrics.setApplicationName(appName);

            // Set average metrics
            double avgCpuUsage = random.nextDouble() * 30; // 0-30%
            long avgPhysicalMemory = (50 + random.nextInt(450)) * 1024 * 1024L; // 50-500 MB
            long avgVirtualMemory = avgPhysicalMemory * 2; // Twice physical memory

            metrics.setAvgCpuUsage(avgCpuUsage);
            metrics.setAvgPhysicalMemoryUsed(avgPhysicalMemory);
            metrics.setAvgVirtualMemoryUsed(avgVirtualMemory);

            // Set maximum metrics (slightly higher than average)
            metrics.setMaxCpuUsage(avgCpuUsage * (1.2 + random.nextDouble() * 0.3)); // 20-50% higher
            metrics.setMaxPhysicalMemoryUsed((long)(avgPhysicalMemory * (1.2 + random.nextDouble() * 0.3)));
            metrics.setMaxVirtualMemoryUsed((long)(avgVirtualMemory * (1.2 + random.nextDouble() * 0.3)));

            // Set minimum metrics (slightly lower than average)
            metrics.setMinCpuUsage(avgCpuUsage * (0.5 + random.nextDouble() * 0.3)); // 50-80% of average
            metrics.setMinPhysicalMemoryUsed((long)(avgPhysicalMemory * (0.5 + random.nextDouble() * 0.3)));
            metrics.setMinVirtualMemoryUsed((long)(avgVirtualMemory * (0.5 + random.nextDouble() * 0.3)));

            metricsList.addApplication(metrics);
        }

        return metricsList;
    }

    public ApplicationMetricsData getApplicationMetricsById(UUID id) {
        // Find the application name associated with the given ID
        String appName = null;
        for (Map.Entry<String, UUID> entry : applicationIds.entrySet()) {
            if (entry.getValue().equals(id)) {
                appName = entry.getKey();
                break;
            }
        }

        // If no matching application found, return random application data
        if (appName == null) {
            appName = mockApplicationNames.get(random.nextInt(mockApplicationNames.size()));
        }

        ApplicationMetricsData metrics = new ApplicationMetricsData();

        // Set application info
        metrics.setId(id);
        metrics.setApplicationName(appName);

        // Set average metrics
        double avgCpuUsage = random.nextDouble() * 30; // 0-30%
        long avgPhysicalMemory = (50 + random.nextInt(450)) * 1024 * 1024L; // 50-500 MB
        long avgVirtualMemory = avgPhysicalMemory * 2; // Twice physical memory

        metrics.setAvgCpuUsage(avgCpuUsage);
        metrics.setAvgPhysicalMemoryUsed(avgPhysicalMemory);
        metrics.setAvgVirtualMemoryUsed(avgVirtualMemory);

        // Set maximum metrics (slightly higher than average)
        metrics.setMaxCpuUsage(avgCpuUsage * (1.2 + random.nextDouble() * 0.3)); // 20-50% higher
        metrics.setMaxPhysicalMemoryUsed((long)(avgPhysicalMemory * (1.2 + random.nextDouble() * 0.3)));
        metrics.setMaxVirtualMemoryUsed((long)(avgVirtualMemory * (1.2 + random.nextDouble() * 0.3)));

        // Set minimum metrics (slightly lower than average)
        metrics.setMinCpuUsage(avgCpuUsage * (0.5 + random.nextDouble() * 0.3)); // 50-80% of average
        metrics.setMinPhysicalMemoryUsed((long)(avgPhysicalMemory * (0.5 + random.nextDouble() * 0.3)));
        metrics.setMinVirtualMemoryUsed((long)(avgVirtualMemory * (0.5 + random.nextDouble() * 0.3)));

        return metrics;
    }
}
