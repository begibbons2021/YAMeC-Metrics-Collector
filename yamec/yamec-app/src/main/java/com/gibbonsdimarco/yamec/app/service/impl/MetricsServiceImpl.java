package com.gibbonsdimarco.yamec.app.service.impl;

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
    private final ApplicationRepository applicationRepository;
    private final ApplicationMetricRepository applicationMetricRepository;

    @Autowired
    public MetricsServiceImpl(
            SystemCpuMetricRepository cpuMetricRepository,
            SystemMemoryMetricRepository memoryMetricRepository,
            SystemDiskMetricRepository diskMetricRepository,
            SystemGpuMetricRepository gpuMetricRepository,
            SystemNicMetricRepository nicMetricRepository,
            ApplicationRepository applicationRepository,
            ApplicationMetricRepository applicationMetricRepository) {
        this.cpuMetricRepository = cpuMetricRepository;
        this.memoryMetricRepository = memoryMetricRepository;
        this.diskMetricRepository = diskMetricRepository;
        this.gpuMetricRepository = gpuMetricRepository;
        this.nicMetricRepository = nicMetricRepository;
        this.applicationRepository = applicationRepository;
        this.applicationMetricRepository = applicationMetricRepository;
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
    public Application saveApplication(Application appInfo) {
        return applicationRepository.save(appInfo);
    }

    @Override
    public ApplicationMetric saveApplicationMetric(ApplicationMetric metric) {
        return applicationMetricRepository.save(metric);
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

    @Override
    public List<Application> getAllApplications(int limit) {
        return applicationRepository.findAll(
                        PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp")))
                .getContent();
    }

    @Override
    public List<ApplicationMetric> getApplicationMetrics(int limit) {
        return applicationMetricRepository.findAll(
                        PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp")))
                .getContent();
    }

    @Override
    public List<ApplicationMetric> getApplicationMetrics(Application application, int limit) {
        return applicationMetricRepository.findAll(
                        PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp")))
                .getContent();
    }
} 