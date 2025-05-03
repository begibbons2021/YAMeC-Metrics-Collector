package com.gibbonsdimarco.yamec.app.service.impl;

import com.gibbonsdimarco.yamec.app.config.Granularity;
import com.gibbonsdimarco.yamec.app.config.GranularityConfig;
import com.gibbonsdimarco.yamec.app.data.*;
import com.gibbonsdimarco.yamec.app.repository.*;
import com.gibbonsdimarco.yamec.app.service.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of the MetricsService interface
 */
@Service
@Transactional
public class MetricsServiceImpl implements MetricsService {

    private final SystemCpuMetricRepository cpuMetricRepository;
    private final SystemMemoryMetricRepository memoryMetricRepository;
    private final SystemDiskMetricRepository diskMetricRepository;
    private final SystemGpuMetricRepository gpuMetricRepository;
    private final SystemNicMetricRepository nicMetricRepository;
    private final GranularityRepository granularityRepository;
    private final GranularityConfigRepository granularityConfigRepository;

    @Autowired
    public MetricsServiceImpl(
            SystemCpuMetricRepository cpuMetricRepository,
            SystemMemoryMetricRepository memoryMetricRepository,
            SystemDiskMetricRepository diskMetricRepository,
            SystemGpuMetricRepository gpuMetricRepository,
            SystemNicMetricRepository nicMetricRepository,
            GranularityRepository granularityRepository,
            GranularityConfigRepository granularityConfigRepository) {
        this.cpuMetricRepository = cpuMetricRepository;
        this.memoryMetricRepository = memoryMetricRepository;
        this.diskMetricRepository = diskMetricRepository;
        this.gpuMetricRepository = gpuMetricRepository;
        this.nicMetricRepository = nicMetricRepository;
        this.granularityRepository = granularityRepository;
        this.granularityConfigRepository = granularityConfigRepository;

        setupGranularityLevels();
    }

    @jakarta.transaction.Transactional(jakarta.transaction.Transactional.TxType.REQUIRES_NEW)
    public void setupGranularityLevels() {
        long granularityLevels = granularityRepository.count();

        if (granularityLevels == 0) {
            Granularity highGranularity = new Granularity("HIGH");
            GranularityConfig highGranularityConfig = new GranularityConfig();
            highGranularityConfig.setRecordTimespan(60); // 1 minute
            highGranularityConfig.setTimeToAge(604800); // 7 days
            highGranularityConfig.setGranularity(highGranularity);
            highGranularity.setGranularityConfig(highGranularityConfig);
            highGranularity = granularityRepository.save(highGranularity);

            Granularity lowGranularity = new Granularity("LOW");
            GranularityConfig lowGranularityConfig = new GranularityConfig();
            lowGranularityConfig.setRecordTimespan(300); // 5 minute
            lowGranularityConfig.setTimeToAge(2592000); // 30 days
            lowGranularityConfig.setGranularity(lowGranularity);
            lowGranularity.setGranularityConfig(lowGranularityConfig);
            lowGranularity = granularityRepository.save(lowGranularity);
        }
    }

    @Override
    public SystemCpuMetric saveCpuMetric(SystemCpuMetric metric) {
        return cpuMetricRepository.save(metric);
    }

    @Override
    public SystemMemoryMetric saveMemoryMetric(SystemMemoryMetric metric) {
        return memoryMetricRepository.save(metric);
    }

    @Override
    public SystemDiskMetric saveDiskMetric(SystemDiskMetric metric) {
        return diskMetricRepository.save(metric);
    }

    @Override
    public SystemGpuMetric saveGpuMetric(SystemGpuMetric metric) {
        return gpuMetricRepository.save(metric);
    }

    @Override
    public SystemNicMetric saveNicMetric(SystemNicMetric metric) {
        return nicMetricRepository.save(metric);
    }

    @Override
    public List<SystemCpuMetric> getLatestCpuMetrics(int limit) {
        return cpuMetricRepository.findAll(
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp")))
                .getContent();
    }

    @Override
    public List<SystemMemoryMetric> getLatestMemoryMetrics(int limit) {
        return memoryMetricRepository.findAll(
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp")))
                .getContent();
    }

    @Override
    public List<SystemDiskMetric> getLatestDiskMetrics(int limit) {
        return diskMetricRepository.findAll(
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp")))
                .getContent();
    }

    @Override
    public List<SystemGpuMetric> getLatestGpuMetrics(int limit) {
        return gpuMetricRepository.findAll(
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp")))
                .getContent();
    }

    @Override
    public List<SystemNicMetric> getLatestNicMetrics(int limit) {
        return nicMetricRepository.findAll(
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp")))
                .getContent();
    }
} 