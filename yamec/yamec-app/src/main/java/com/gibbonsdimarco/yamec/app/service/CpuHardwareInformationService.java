package com.gibbonsdimarco.yamec.app.service;

import com.gibbonsdimarco.yamec.app.config.Granularity;
import com.gibbonsdimarco.yamec.app.data.*;
import com.gibbonsdimarco.yamec.app.repository.CpuHardwareInformationRepository;
import com.gibbonsdimarco.yamec.app.repository.GranularityRepository;
import com.gibbonsdimarco.yamec.app.repository.SystemCpuMetricRepository;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class CpuHardwareInformationService {

    private final CpuHardwareInformationRepository cpuRepository;
    private final GranularityRepository granularityRepository;
    private final SystemCpuMetricRepository systemCpuMetricRepository;

    @Autowired
    public CpuHardwareInformationService(CpuHardwareInformationRepository cpuRepository, GranularityRepository granularityRepository, SystemCpuMetricRepository systemCpuMetricRepository) {
        this.cpuRepository = cpuRepository;
        this.granularityRepository = granularityRepository;
        this.systemCpuMetricRepository = systemCpuMetricRepository;
    }

    // Assuming a record timespan of 1
    public java.util.List<SystemCpuMetric> aggregateCpuMetrics(java.util.List<SystemCpuMetric> cpuMetrics,
                                                                      java.sql.Timestamp startTime,
                                                                      int duration,
                                                                      String granularityLabel) {

//        long recordTimespan = 1;
        // Error handling/edge cases in case this function is improperly called
        if (duration < 1) {
            throw new IllegalArgumentException("Duration must be greater than 0");
        }

        else if (cpuMetrics == null || cpuMetrics.isEmpty()) {
            return null;
        }

        long startTimeAsLong = startTime.getTime();

        // Use a map to group metrics by the CPU ID
        java.util.List<UUID> recordedCpuIds = new java.util.ArrayList<>();
        java.util.HashMap<UUID, double[]> cpuUtilizationTotalMap = new java.util.HashMap<>();
        java.util.HashMap<UUID, Double> cpuUtilizationMaxMap = new java.util.HashMap<>();
        java.util.HashMap<UUID, Double> cpuUtilizationMinMap = new java.util.HashMap<>();


        // Go through all processes related to this application and get the total resource use
        // by the application per second
        for (SystemCpuMetric currentMetric : cpuMetrics) {

            // Fail-safe in case a metric is not assigned a CPU (can't properly group the metrics)
            if (currentMetric.getCpu() == null)
            {
                continue;
            }

            // Fail-safe in case a metric is not given a timestamp (Can't accurately get metrics from it)
            if (currentMetric.getTimestamp() == null) {
                continue;
            }

            UUID cpuId = currentMetric.getCpu().getId();

            // Get average, max, and min metrics
            double averageUtilization = currentMetric.getAverageUtilization();
            double maxUtilization = currentMetric.getMaxUtilization();
            double minUtilization = currentMetric.getMinUtilization();


            long timestampAsLong = currentMetric.getTimestamp().getTime();

            // Used to map process data to the second number of the data
            int secondsSinceStartTime = (int)((timestampAsLong - startTimeAsLong)/1000);


            // Fail-safe if a record outside the duration is included
            if (secondsSinceStartTime > duration - 1) {
                continue;
            }

            // Create key maps for all values if one doesn't exist for a specific CPU
            if (!recordedCpuIds.contains(cpuId)) {
                recordedCpuIds.add(cpuId);
                cpuUtilizationTotalMap.put(cpuId, new double[duration]);
                cpuUtilizationMaxMap.put(cpuId, 0.0);
                cpuUtilizationMinMap.put(cpuId, Double.MAX_VALUE);
            }

            // Add to running totals and extract maximums and minimums
            cpuUtilizationTotalMap.get(cpuId)[secondsSinceStartTime] = averageUtilization;
            if (maxUtilization > cpuUtilizationMaxMap.get(cpuId)) {
                cpuUtilizationMaxMap.put(cpuId, maxUtilization);
            }
            if (minUtilization < cpuUtilizationMinMap.get(cpuId)) {
                cpuUtilizationMinMap.put(cpuId, minUtilization);
            }

        }

        java.util.List<SystemCpuMetric> metricsToRecord = new java.util.ArrayList<>();

        for (UUID cpuId : recordedCpuIds) {

            // Data containers for metrics
            double averageCpuUsage = 0;
            double maxCpuUsage = cpuUtilizationMaxMap.get(cpuId);
            double minCpuUsage = cpuUtilizationMinMap.get(cpuId);

            for (int secondNum = 0; secondNum < duration; secondNum++) {
                // Sum total usages for all valid processes and applications
                averageCpuUsage += cpuUtilizationTotalMap.get(cpuId)[secondNum];
            }

            // Take average of each metric based on the duration
            averageCpuUsage /= duration;

            SystemCpuMetric currentMetric = new SystemCpuMetric(cpuRepository.getReferenceById(cpuId),
                    startTime, duration,
                    granularityRepository.getByLabel(granularityLabel).getId(),
                    averageCpuUsage,
                    maxCpuUsage,
                    minCpuUsage);

            currentMetric.setCpu(cpuRepository.getReferenceById(cpuId));

            // Create Metric object
            metricsToRecord.add(currentMetric);
        }

        return metricsToRecord;

    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public CpuHardwareInformation saveCpuInformation(CpuHardwareInformation cpuInfo) {

        if (cpuInfo == null) {
            return null;
        }

        CpuHardwareInformation matchingConfiguration
                = cpuRepository.findMatchingCpuHardwareInformation(cpuInfo);

        // Returns the matching hardware object if it exists; otherwise, it saves it to the database
        // Note: CPU hardware for a SKU doesn't just change, but info like virtualization of available
        // logical processors might!
        return Objects.requireNonNullElseGet(matchingConfiguration,
                () -> cpuRepository.save(cpuInfo));
    }

    /**
     * <p>Records information about the current system CPU usage state the database from the
     * <code>List<SystemCpu></code> objects passed by parameter for a given timespan expressed by
     * the <code>startTime</code> and <code>duration</code> parameters.</p>
     *
     *
     * @param cpuMetrics The list of <code>SystemCpuMetric</code> objects to record to the
     *                       database
     * @param startTime The starting timestamp of the process metrics collected
     * @param duration The number of seconds which the process metrics were collected for
     * @return The list of <code>SystemCpuMetric</code> entities created
     */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public java.util.List<SystemCpuMetric> saveCpuMetrics(java.util.List<SystemCpuMetric> cpuMetrics,
                                                          java.sql.Timestamp startTime,
                                                          int duration) {

        if (duration <= 0) {
            throw new IllegalArgumentException("duration must be greater than 0");
        }

        if (cpuMetrics == null || cpuMetrics.isEmpty() ) {
            return null;
        }

//        // The collection to be sent to the database to complete the rest of the transaction
        java.util.List<SystemCpuMetric> validMetrics
                = aggregateCpuMetrics(cpuMetrics, startTime, duration, "HIGH");

        // Commit Application Metrics and return entities with the committed IDs
        return systemCpuMetricRepository.saveAllAndFlush(validMetrics);

    }

    public java.util.List<CpuHardwareInformation> getStoredCpuInformation() {
        return cpuRepository.findAll(
                org.springframework.data.domain.PageRequest.of(0, 255,
                        org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,
                                "id")))
                .getContent();
    }

    public java.util.List<CpuHardwareInformation> getStoredCpuInformation(int pageNumber) {
        return cpuRepository.findAll(
                        org.springframework.data.domain.PageRequest.of(pageNumber, 255,
                                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,
                                        "id")))
                .getContent();
    }


    public java.util.List<SystemCpuMetric>
                getLatestCpuMetrics(java.util.List<CpuHardwareInformation> cpuDevices) {
        if (cpuDevices == null || cpuDevices.isEmpty()) {
            return null;
        }

        List<SystemCpuMetric> cpuMetrics = new java.util.ArrayList<>();

        for (CpuHardwareInformation cpuHardwareInformation : cpuDevices) {
            SystemCpuMetric cpuMetric
                    = systemCpuMetricRepository.getNewestByCpuId(cpuHardwareInformation.getId());
            if (cpuMetric != null) {
                cpuMetrics.add(cpuMetric);
            }
        }

        return cpuMetrics;
    }

    public SystemCpuMetric getLatestMetric() {
        return systemCpuMetricRepository.getNewest();
    }


    public java.util.List<SystemCpuMetric> getStoredCpuMetrics(Timestamp startTime, Timestamp endTime) {
        return systemCpuMetricRepository.findAllByTimestampBetween(
                startTime, endTime);
    }

    public java.util.List<SystemCpuMetric> getStoredCpuMetrics(Timestamp startTime, Timestamp endTime, int pageNumber) {
        return systemCpuMetricRepository.findAllByTimestampBetween(
                startTime, endTime,
                    PageRequest.of(pageNumber, 255,
                                                            Sort.by(Sort.Direction.DESC, "timestamp"))
        );
    }


    // Other service methods as needed
}