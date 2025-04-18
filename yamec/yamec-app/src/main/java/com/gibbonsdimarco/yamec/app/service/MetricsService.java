package com.gibbonsdimarco.yamec.app.service;

import com.gibbonsdimarco.yamec.app.data.*;

import java.util.List;

/**
 * Service interface for metrics operations
 */
public interface MetricsService {
    
    /**
     * Save a CPU metric to the database
     * @param metric The CPU metric to save
     * @return The saved metric with generated ID
     */
    SystemCpuMetric saveCpuMetric(SystemCpuMetric metric);
    
    /**
     * Save a memory metric to the database
     * @param metric The memory metric to save
     * @return The saved metric with generated ID
     */
    SystemMemoryMetric saveMemoryMetric(SystemMemoryMetric metric);
    
    /**
     * Save a disk metric to the database
     * @param metric The disk metric to save
     * @return The saved metric with generated ID
     */
    SystemDiskMetric saveDiskMetric(SystemDiskMetric metric);
    
    /**
     * Save a GPU metric to the database
     * @param metric The GPU metric to save
     * @return The saved metric with generated ID
     */
    SystemGpuMetric saveGpuMetric(SystemGpuMetric metric);
    
    /**
     * Save a NIC metric to the database
     * @param metric The NIC metric to save
     * @return The saved metric with generated ID
     */
    SystemNicMetric saveNicMetric(SystemNicMetric metric);
    
    /**
     * Get the latest CPU metrics
     * @param limit The maximum number of metrics to return
     * @return A list of the latest CPU metrics
     */
    List<SystemCpuMetric> getLatestCpuMetrics(int limit);
    
    /**
     * Get the latest memory metrics
     * @param limit The maximum number of metrics to return
     * @return A list of the latest memory metrics
     */
    List<SystemMemoryMetric> getLatestMemoryMetrics(int limit);
    
    /**
     * Get the latest disk metrics
     * @param limit The maximum number of metrics to return
     * @return A list of the latest disk metrics
     */
    List<SystemDiskMetric> getLatestDiskMetrics(int limit);
    
    /**
     * Get the latest GPU metrics
     * @param limit The maximum number of metrics to return
     * @return A list of the latest GPU metrics
     */
    List<SystemGpuMetric> getLatestGpuMetrics(int limit);
    
    /**
     * Get the latest NIC metrics
     * @param limit The maximum number of metrics to return
     * @return A list of the latest NIC metrics
     */
    List<SystemNicMetric> getLatestNicMetrics(int limit);
} 