package com.gibbonsdimarco.yamec.app.model.mock;

import com.gibbonsdimarco.yamec.app.model.MetricsData;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class MockMetricsDataService {
    private final Random random = new Random();

    public MetricsData getCurrentMetrics() {
        MetricsData metrics = new MetricsData();

        // Set mock values
        metrics.setCpuUsage(random.nextDouble() * 100);
        metrics.setTotalMemory(16 * 1024 * 1024 * 1024L); // 16GB
        metrics.setUsedMemory((long) (metrics.getTotalMemory() * random.nextDouble()));
        metrics.setFreeMemory(metrics.getTotalMemory() - metrics.getUsedMemory());
        
        // Add disk metrics
        metrics.setDiskTotal(500 * 1024 * 1024 * 1024L); // 500GB
        metrics.setDiskUsed((long) (metrics.getDiskTotal() * (0.3 + random.nextDouble() * 0.4))); // 30-70% used
        metrics.setDiskFree(metrics.getDiskTotal() - metrics.getDiskUsed());
        
        // Add network metrics
        metrics.setNetworkSent(random.nextLong(100 * 1024 * 1024)); // Up to 100MB sent
        metrics.setNetworkReceived(random.nextLong(200 * 1024 * 1024)); // Up to 200MB received

        return metrics;
    }
}