package com.gibbonsdimarco.yamec.app.service;

import com.gibbonsdimarco.yamec.app.data.*;
import com.gibbonsdimarco.yamec.app.repository.CpuHardwareInformationRepository;
import com.gibbonsdimarco.yamec.app.repository.GranularityRepository;
import com.gibbonsdimarco.yamec.app.repository.MemoryHardwareInformationRepository;
import com.gibbonsdimarco.yamec.app.repository.SystemMemoryMetricRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class MemoryHardwareInformationService {

    private final SystemMemoryMetricRepository memoryMetricRepository;
    private final MemoryHardwareInformationRepository memoryHardwareInformationRepository;
    private final GranularityRepository granularityRepository;

    @Autowired
    public MemoryHardwareInformationService(SystemMemoryMetricRepository memoryMetricRepository,
                                            MemoryHardwareInformationRepository memoryHardwareInformationRepository,
                                            GranularityRepository granularityRepository) {
        this.memoryMetricRepository = memoryMetricRepository;
        this.memoryHardwareInformationRepository = memoryHardwareInformationRepository;
        this.granularityRepository = granularityRepository;
    }


    // Assuming a record timespan of 1
    public java.util.List<SystemMemoryMetric> aggregateMemoryMetrics(java.util.List<SystemMemoryMetric> memoryMetrics,
                                                               java.sql.Timestamp startTime,
                                                               int duration,
                                                               String granularityLabel) {

//        long recordTimespan = 1;
        // Error handling/edge cases in case this function is improperly called
        if (duration < 1) {
            throw new IllegalArgumentException("Duration must be greater than 0");
        }

        else if (memoryMetrics == null) {
            return null;
        }

        long startTimeAsLong = startTime.getTime();

        // Use a map to group metrics by the
        java.util.List<UUID> recordedMemoryIds = new java.util.ArrayList<>();
        java.util.HashMap<UUID, Integer> numValidMemoryMetrics = new java.util.HashMap<>();

        java.util.HashMap<UUID, long[]> physicalMemoryUsedTotalMap = new java.util.HashMap<>();
        java.util.HashMap<UUID, Long> physicalMemoryUsedMaxMap = new java.util.HashMap<>();
        java.util.HashMap<UUID, Long> physicalMemoryUsedMinMap = new java.util.HashMap<>();
        java.util.HashMap<UUID, Boolean> physicalMemoryUsedUnsignedMap = new java.util.HashMap<>();

        java.util.HashMap<UUID, long[]> virtualMemoryUsedTotalMap = new java.util.HashMap<>();
        java.util.HashMap<UUID, Long> virtualMemoryUsedMaxMap = new java.util.HashMap<>();
        java.util.HashMap<UUID, Long> virtualMemoryUsedMinMap = new java.util.HashMap<>();
        java.util.HashMap<UUID, Boolean> virtualMemoryUsedUnsignedMap = new java.util.HashMap<>();


        // Go through all processes related to this application and get the total resource use
        // by the application per second
        for (SystemMemoryMetric currentMetric : memoryMetrics) {

            // Fail-safe in case a metric is not assigned a Memory Hardware metric (can't properly group the metrics)
            if (currentMetric.getMemory() == null)
            {
                continue;
            }

            // Fail-safe in case a metric is not given a timestamp (Can't accurately get metrics from it)
            if (currentMetric.getTimestamp() == null) {
                continue;
            }

            UUID memoryId = currentMetric.getMemory().getId();

            // Get average, max, and min metrics
            long averagePhysicalMemoryUsed = currentMetric.getAveragePhysicalUtilization();
            long maxPhysicalMemoryUsed = currentMetric.getMaxPhysicalUtilization();
            long minPhysicalMemoryUsed = currentMetric.getMinPhysicalUtilization();

            long averageVirtualMemoryUsed = currentMetric.getAverageVirtualUtilization();
            long maxVirtualMemoryUsed = currentMetric.getMaxVirtualUtilization();
            long minVirtualMemoryUsed = currentMetric.getMinVirtualUtilization();

            boolean unsignedPhysicalMemory = currentMetric.isPhysicalUtilizationUnsigned();
            boolean unsignedVirtualMemory = currentMetric.isVirtualUtilizationUnsigned();

            long timestampAsLong = currentMetric.getTimestamp().getTime();

            // Used to map process data to the second number of the data
            int secondsSinceStartTime = (int)((timestampAsLong - startTimeAsLong)/1000);


            // Fail-safe if a record outside the duration is included
            if (secondsSinceStartTime > duration - 1) {
                continue;
            }

            // Create key maps for all values if one doesn't exist for a specific CPU
            if (!recordedMemoryIds.contains(memoryId)) {
                recordedMemoryIds.add(memoryId);
                numValidMemoryMetrics.put(memoryId, 0);
                physicalMemoryUsedTotalMap.put(memoryId, new long[duration]);
                physicalMemoryUsedMaxMap.put(memoryId, Long.MIN_VALUE);
                physicalMemoryUsedMinMap.put(memoryId, Long.MAX_VALUE);

                virtualMemoryUsedTotalMap.put(memoryId, new long[duration]);
                virtualMemoryUsedMaxMap.put(memoryId, Long.MIN_VALUE);
                virtualMemoryUsedMinMap.put(memoryId, Long.MAX_VALUE);

                physicalMemoryUsedUnsignedMap.put(memoryId, false);
                virtualMemoryUsedUnsignedMap.put(memoryId, false);
            }

            // If no memory is being used, skip this metric
            if (averagePhysicalMemoryUsed == 0
                    && averageVirtualMemoryUsed == 0
                    && maxPhysicalMemoryUsed == 0
                    && maxVirtualMemoryUsed == 0
                    && minPhysicalMemoryUsed == 0
                    && minVirtualMemoryUsed == 0) {
                continue;
            }

            // This is a valid metric, and the timestamp
            numValidMemoryMetrics.put(memoryId, numValidMemoryMetrics.get(memoryId) + 1);

            // Add to running totals and extract maximums and minimums
            physicalMemoryUsedTotalMap.get(memoryId)[secondsSinceStartTime] = averagePhysicalMemoryUsed;
            if (physicalMemoryUsedMaxMap.get(memoryId) < maxPhysicalMemoryUsed) {
                physicalMemoryUsedMaxMap.put(memoryId, maxPhysicalMemoryUsed);
            }
            if (physicalMemoryUsedMinMap.get(memoryId) > minPhysicalMemoryUsed) {
                physicalMemoryUsedMinMap.put(memoryId, minPhysicalMemoryUsed);
            }

            virtualMemoryUsedTotalMap.get(memoryId)[secondsSinceStartTime] = averageVirtualMemoryUsed;
            if (virtualMemoryUsedMaxMap.get(memoryId) < maxVirtualMemoryUsed) {
                virtualMemoryUsedMaxMap.put(memoryId, maxVirtualMemoryUsed);
            }
            if (virtualMemoryUsedMinMap.get(memoryId) > minVirtualMemoryUsed) {
                virtualMemoryUsedMinMap.put(memoryId, minVirtualMemoryUsed);
            }

            // Set unsigned boolean values to true if even one value is reported as true
            if (unsignedPhysicalMemory) {
                physicalMemoryUsedUnsignedMap.put(memoryId, true);
            }

            if (unsignedVirtualMemory) {
                virtualMemoryUsedUnsignedMap.put(memoryId, true);
            }

        }

        java.util.List<SystemMemoryMetric> metricsToRecord = new java.util.ArrayList<>();

        for (UUID memoryId : recordedMemoryIds) {

            // Data containers for metrics
            long averagePhysicalMemoryUsage = 0;
            long maxPhysicalMemoryUsage = physicalMemoryUsedMaxMap.get(memoryId);
            long minPhysicalMemoryUsage = physicalMemoryUsedMinMap.get(memoryId);

            long averageVirtualMemoryUsage = 0;
            long maxVirtualMemoryUsage = virtualMemoryUsedMaxMap.get(memoryId);
            long minVirtualMemoryUsage = virtualMemoryUsedMinMap.get(memoryId);

            boolean unsignedPhysicalMemory = physicalMemoryUsedUnsignedMap.get(memoryId);
            boolean unsignedVirtualMemory = virtualMemoryUsedUnsignedMap.get(memoryId);

            int countMetrics = numValidMemoryMetrics.get(memoryId);

            for (int secondNum = 0; secondNum < duration; secondNum++) {
                // Sum total usages for all valid processes and applications
                averagePhysicalMemoryUsage += physicalMemoryUsedTotalMap.get(memoryId)[secondNum];
                averageVirtualMemoryUsage += virtualMemoryUsedTotalMap.get(memoryId)[secondNum];
            }

            // Take average of each metric based on the duration
            averagePhysicalMemoryUsage /= countMetrics;
            averageVirtualMemoryUsage /= countMetrics;

            SystemMemoryMetric currentMetric = new SystemMemoryMetric(startTime, duration,
                    granularityRepository.getByLabel(granularityLabel).getId(),
                    averagePhysicalMemoryUsage,
                    maxPhysicalMemoryUsage,
                    minPhysicalMemoryUsage,
                    averageVirtualMemoryUsage,
                    maxVirtualMemoryUsage,
                    minVirtualMemoryUsage,
                    unsignedPhysicalMemory,
                    unsignedVirtualMemory);

            currentMetric.setMemory(memoryHardwareInformationRepository.getReferenceById(memoryId));

            // Create Metric object
            metricsToRecord.add(currentMetric);
        }

        return metricsToRecord;

    }

    /**
     * <p>Records information about the current system CPU usage state the database from the
     * <code>List<SystemCpu></code> objects passed by parameter for a given timespan expressed by
     * the <code>startTime</code> and <code>duration</code> parameters.</p>
     *
     *
     * @param memoryMetrics The list of <code>SystemMemoryMetric</code> objects to record to the
     *                       database
     * @param startTime The starting timestamp of the process metrics collected
     * @param duration The number of seconds which the process metrics were collected for
     * @return The list of <code>SystemMemoryMetric</code> entities created
     */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public java.util.List<SystemMemoryMetric> saveMemoryMetrics(java.util.List<SystemMemoryMetric> memoryMetrics,
                                                          java.sql.Timestamp startTime,
                                                          int duration) {

        if (duration <= 0) {
            throw new IllegalArgumentException("duration must be greater than 0");
        }

//        // The collection to be sent to the database to complete the rest of the transaction
        java.util.List<SystemMemoryMetric> validMetrics
                = aggregateMemoryMetrics(memoryMetrics, startTime, duration, "HIGH");

        // Commit Application Metrics and return entities with the committed IDs
        return memoryMetricRepository.saveAllAndFlush(validMetrics);

    }




    @Transactional
    public MemoryHardwareInformation saveMemoryInformation(MemoryHardwareInformation memoryInformation) {

        MemoryHardwareInformation matchingConfiguration
                = memoryHardwareInformationRepository.findMatchingMemoryHardwareInformation(memoryInformation);

        // Returns the matching hardware object if it exists; otherwise, it saves it to the database
        return Objects.requireNonNullElseGet(matchingConfiguration,
                () -> memoryHardwareInformationRepository.save(memoryInformation));

    }

    public java.util.List<MemoryHardwareInformation> getStoredMemoryInformation() {
        return memoryHardwareInformationRepository.findAll(
                org.springframework.data.domain.PageRequest.of(0, 255,
                        org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,
                                "id")))
                .getContent();
    }

    public java.util.List<MemoryHardwareInformation> getStoredMemoryInformation(int pageNumber) {
        return memoryHardwareInformationRepository.findAll(
                        org.springframework.data.domain.PageRequest.of(pageNumber, 255,
                                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,
                                        "id")))
                .getContent();
    }


    public List<SystemMemoryMetric>
                getLatestMemoryMetrics(List<MemoryHardwareInformation> memoryDevices) {
        List<SystemMemoryMetric> memoryMetrics = new ArrayList<>();

        for (MemoryHardwareInformation memoryHardwareInformation : memoryDevices) {
            SystemMemoryMetric memoryMetric
                    = memoryMetricRepository.getNewestByMemoryId(memoryHardwareInformation.getId());
            if (memoryMetric != null) {
                memoryMetrics.add(memoryMetric);
            }
        }

        return memoryMetrics;
    }

    // Other service methods as needed
}