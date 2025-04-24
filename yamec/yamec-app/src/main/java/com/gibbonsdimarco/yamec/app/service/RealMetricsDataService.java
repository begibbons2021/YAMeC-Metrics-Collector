package com.gibbonsdimarco.yamec.app.service;

import com.gibbonsdimarco.yamec.app.YamecApplication;
import com.gibbonsdimarco.yamec.app.data.*;
import com.gibbonsdimarco.yamec.app.jni.SystemMonitorManagerJNI;
import com.gibbonsdimarco.yamec.app.model.MetricsData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for collecting and storing real system metrics
 */
@Service
public class RealMetricsDataService {

    private final MetricsService metricsService;
    private MetricsData currentMetrics;

    private final Logger logger = LoggerFactory.getLogger(RealMetricsDataService.class);

    @Autowired
    public RealMetricsDataService(MetricsService metricsService) {
        this.metricsService = metricsService;
        this.currentMetrics = new MetricsData();
    }

    /**
     * Collect and save metrics every minute
     */
    @Scheduled(fixedRate = 60000) // 60 seconds
    public void collectAndSaveMetrics() {
        // In a real implementation, this would collect actual system metrics
        // For now, we'll just save the current metrics
        
        // Save CPU metrics
        SystemCpuMetric cpuMetric = new SystemCpuMetric("CPU", currentMetrics.getCpuUsage());
        metricsService.saveCpuMetric(cpuMetric);
        
        // Save memory metrics
        SystemMemoryMetric memoryMetric = new SystemMemoryMetric(
                currentMetrics.getTotalMemory() - currentMetrics.getFreeMemory(),
                currentMetrics.getTotalMemory(),
                (double) (currentMetrics.getTotalMemory() - currentMetrics.getFreeMemory()) / currentMetrics.getTotalMemory() * 100,
                true,
                true
        );
        metricsService.saveMemoryMetric(memoryMetric);
        
        // Save disk metrics
        SystemDiskMetric diskMetric = new SystemDiskMetric(
                "Disk",
                (double) currentMetrics.getDiskUsed() / currentMetrics.getDiskTotal() * 100,
                currentMetrics.getDiskUsed(),
                currentMetrics.getDiskFree(),
                0.1, // Average time to transfer (placeholder)
                true,
                true
        );
        metricsService.saveDiskMetric(diskMetric);
        
        // Save network metrics
        SystemNicMetric nicMetric = new SystemNicMetric(
                "Network",
                currentMetrics.getNetworkSent() + currentMetrics.getNetworkReceived(), // Total bandwidth
                currentMetrics.getNetworkSent(),
                currentMetrics.getNetworkReceived(),
                true, // nicBandwidthIsUnsigned
                true, // bytesSentIsUnsigned
                true  // bytesReceivedIsUnsigned
        );
        metricsService.saveNicMetric(nicMetric);
    }

    /**
     * Get the current metrics
     * @return The current metrics
     */
    public MetricsData getCurrentMetrics() {
        return currentMetrics;
    }

    /**
     * Update the current metrics
     * @param metrics The new metrics
     */
    public void updateMetrics(MetricsData metrics) {
        this.currentMetrics = metrics;
    }
} 