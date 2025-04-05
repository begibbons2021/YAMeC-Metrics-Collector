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

        return metrics;
    }
}