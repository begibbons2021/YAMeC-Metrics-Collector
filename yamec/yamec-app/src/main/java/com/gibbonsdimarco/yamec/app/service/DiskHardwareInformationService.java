package com.gibbonsdimarco.yamec.app.service;

import com.gibbonsdimarco.yamec.app.data.*;
import com.gibbonsdimarco.yamec.app.repository.DiskHardwareInformationRepository;
import com.gibbonsdimarco.yamec.app.repository.GranularityRepository;
import com.gibbonsdimarco.yamec.app.repository.SystemDiskMetricRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class DiskHardwareInformationService {

    private final SystemDiskMetricRepository diskMetricRepository;
    private final DiskHardwareInformationRepository diskHardwareInformationRepository;
    private final GranularityRepository granularityRepository;

    @Autowired
    public DiskHardwareInformationService(SystemDiskMetricRepository diskMetricRepository,
                                          DiskHardwareInformationRepository diskHardwareInformationRepository,
                                          GranularityRepository granularityRepository) {
        this.diskMetricRepository = diskMetricRepository;
        this.diskHardwareInformationRepository = diskHardwareInformationRepository;
        this.granularityRepository = granularityRepository;
    }


    // Assuming a record timespan of 1
    public java.util.List<SystemDiskMetric> aggregateDiskMetrics(java.util.List<SystemDiskMetric> diskMetrics,
                                                                     java.sql.Timestamp startTime,
                                                                     int duration,
                                                                     String granularityLabel) {

//        long recordTimespan = 1;
        // Error handling/edge cases in case this function is improperly called
        if (duration < 1) {
            throw new IllegalArgumentException("Duration must be greater than 0");
        }

        if (diskMetrics == null || diskMetrics.isEmpty() ) {
            return null;
        }


        long startTimeAsLong = startTime.getTime();

        // Use a map to group metrics by the device ID
        java.util.List<UUID> recordedDiskIds = new java.util.ArrayList<>();
        java.util.HashMap<UUID, Integer> numValidDiskMetrics = new java.util.HashMap<>();

        java.util.HashMap<UUID, double[]> utilizationTotalMap = new java.util.HashMap<>();
        java.util.HashMap<UUID, Double> utilizationMaxMap = new java.util.HashMap<>();
        java.util.HashMap<UUID, Double> utilizationMinMap = new java.util.HashMap<>();

        java.util.HashMap<UUID, long[]> readBandwidthTotalMap = new java.util.HashMap<>();
        java.util.HashMap<UUID, Long> readBandwidthMaxMap = new java.util.HashMap<>();
        java.util.HashMap<UUID, Long> readBandwidthMinMap = new java.util.HashMap<>();
        java.util.HashMap<UUID, Boolean> readBandwidthUnsignedMap = new java.util.HashMap<>();

        java.util.HashMap<UUID, long[]> writeBandwidthTotalMap = new java.util.HashMap<>();
        java.util.HashMap<UUID, Long> writeBandwidthMaxMap = new java.util.HashMap<>();
        java.util.HashMap<UUID, Long> writeBandwidthMinMap = new java.util.HashMap<>();
        java.util.HashMap<UUID, Boolean> writeBandwidthUnsignedMap = new java.util.HashMap<>();

        java.util.HashMap<UUID, double[]> timeToTransferTotalMap = new java.util.HashMap<>();
        java.util.HashMap<UUID, Double> timeToTransferMaxMap = new java.util.HashMap<>();
        java.util.HashMap<UUID, Double> timeToTransferMinMap = new java.util.HashMap<>();


        // Go through all processes related to this application and get the total resource use
        // by the application per second
        for (SystemDiskMetric currentMetric : diskMetrics) {

            // Fail-safe in case a metric is not assigned a Memory Hardware metric (can't properly group the metrics)
            if (currentMetric.getDisk() == null)
            {
                continue;
            }

            // Fail-safe in case a metric is not given a timestamp (Can't accurately get metrics from it)
            if (currentMetric.getTimestamp() == null) {
                continue;
            }

            UUID diskId = currentMetric.getDisk().getId();

            // Get average, max, and min metrics
            double averageUtilization = currentMetric.getAvgUtilization();
            double maxUtilization = currentMetric.getMaxUtilization();
            double minUtilization = currentMetric.getMinUtilization();

            long averageReadBandwidth = currentMetric.getAvgReadBandwidth();
            long maxReadBandwidth = currentMetric.getMaxReadBandwidth();
            long minReadBandwidth = currentMetric.getMinReadBandwidth();

            long averageWriteBandwidth = currentMetric.getAvgWriteBandwidth();
            long maxWriteBandwidth = currentMetric.getMaxWriteBandwidth();
            long minWriteBandwidth = currentMetric.getMinWriteBandwidth();

            boolean unsignedReadBandwidth = currentMetric.isReadBandwidthUnsigned();
            boolean unsignedWriteBandwidth = currentMetric.isWriteBandwidthUnsigned();

            double averageTimeToTransfer = currentMetric.getAvgTimeToTransfer();
            double maxTimeToTransfer = currentMetric.getMaxTimeToTransfer();
            double minTimeToTransfer = currentMetric.getMinTimeToTransfer();

            long timestampAsLong = currentMetric.getTimestamp().getTime();

            // Used to map process data to the second number of the data
            int secondsSinceStartTime = (int)((timestampAsLong - startTimeAsLong)/1000);


            // Fail-safe if a record outside the duration is included
            if (secondsSinceStartTime > duration - 1) {
                continue;
            }

            // Create key maps for all values if one doesn't exist for a specific CPU
            if (!recordedDiskIds.contains(diskId)) {
                recordedDiskIds.add(diskId);
                numValidDiskMetrics.put(diskId, 0);

                utilizationTotalMap.put(diskId, new double[duration]);
                utilizationMaxMap.put(diskId, 0.0);
                utilizationMinMap.put(diskId, Double.MAX_VALUE);

                readBandwidthTotalMap.put(diskId, new long[duration]);
                readBandwidthMaxMap.put(diskId, Long.MIN_VALUE);
                readBandwidthMinMap.put(diskId, Long.MAX_VALUE);

                writeBandwidthTotalMap.put(diskId, new long[duration]);
                writeBandwidthMaxMap.put(diskId, Long.MIN_VALUE);
                writeBandwidthMinMap.put(diskId, Long.MAX_VALUE);

                timeToTransferTotalMap.put(diskId, new double[duration]);
                timeToTransferMaxMap.put(diskId, 0.0);
                timeToTransferMinMap.put(diskId, Double.MAX_VALUE);

                readBandwidthUnsignedMap.put(diskId, false);
                writeBandwidthUnsignedMap.put(diskId, false);
            }


            // This is a valid metric, and the timestamp
            numValidDiskMetrics.put(diskId, numValidDiskMetrics.get(diskId) + 1);

            // Add to running totals and extract maximums and minimums
            utilizationTotalMap.get(diskId)[secondsSinceStartTime] = averageUtilization;
            if (utilizationMaxMap.get(diskId) < maxUtilization) {
                utilizationMaxMap.put(diskId, maxUtilization);
            }
            if (utilizationMinMap.get(diskId) > minUtilization) {
                utilizationMinMap.put(diskId, minUtilization);
            }

            // Add to running totals and extract maximums and minimums
            readBandwidthTotalMap.get(diskId)[secondsSinceStartTime] = averageReadBandwidth;
            if (readBandwidthMaxMap.get(diskId) < maxReadBandwidth) {
                readBandwidthMaxMap.put(diskId, maxReadBandwidth);
            }
            if (readBandwidthMinMap.get(diskId) > minReadBandwidth) {
                readBandwidthMinMap.put(diskId, minReadBandwidth);
            }

            // Add to running totals and extract maximums and minimums
            writeBandwidthTotalMap.get(diskId)[secondsSinceStartTime] = averageWriteBandwidth;
            if (writeBandwidthMaxMap.get(diskId) < maxWriteBandwidth) {
                writeBandwidthMaxMap.put(diskId, maxWriteBandwidth);
            }
            if (writeBandwidthMinMap.get(diskId) > minWriteBandwidth) {
                writeBandwidthMinMap.put(diskId, minWriteBandwidth);
            }

            // Add to running totals and extract maximums and minimums
            timeToTransferTotalMap.get(diskId)[secondsSinceStartTime] = averageTimeToTransfer;
            if (timeToTransferMaxMap.get(diskId) < maxTimeToTransfer) {
                timeToTransferMaxMap.put(diskId, maxTimeToTransfer);
            }
            if (timeToTransferMinMap.get(diskId) > minTimeToTransfer) {
                timeToTransferMinMap.put(diskId, minTimeToTransfer);
            }

            // Set unsigned boolean values to true if even one value is reported as true
            if (unsignedReadBandwidth) {
                readBandwidthUnsignedMap.put(diskId, true);
            }

            if (unsignedWriteBandwidth) {
                writeBandwidthUnsignedMap.put(diskId, true);
            }

        }

        java.util.List<SystemDiskMetric> metricsToRecord = new java.util.ArrayList<>();

        for (UUID diskId : recordedDiskIds) {



            // Data containers for metrics
            double averageUtilization = 0;
            double maxUtilization = utilizationMaxMap.get(diskId);
            double minUtilization = utilizationMinMap.get(diskId);

            long averageReadBandwidth = 0;
            long maxReadBandwidth = readBandwidthMaxMap.get(diskId);
            long minReadBandwidth = readBandwidthMinMap.get(diskId);

            long averageWriteBandwidth = 0;
            long maxWriteBandwidth = writeBandwidthMaxMap.get(diskId);
            long minWriteBandwidth = writeBandwidthMinMap.get(diskId);

            double averageTimeToTransfer = 0;
            double maxTimeToTransfer = timeToTransferMaxMap.get(diskId);
            double minTimeToTransfer = timeToTransferMinMap.get(diskId);

            boolean unsignedReadBandwidth = readBandwidthUnsignedMap.get(diskId);
            boolean unsignedWriteBandwidth = writeBandwidthUnsignedMap.get(diskId);

            int countMetrics = numValidDiskMetrics.get(diskId);

            for (int secondNum = 0; secondNum < duration; secondNum++) {
                // Sum total usages for all valid processes and applications
                averageUtilization += utilizationTotalMap.get(diskId)[secondNum];
                averageReadBandwidth += readBandwidthTotalMap.get(diskId)[secondNum];
                averageWriteBandwidth += writeBandwidthTotalMap.get(diskId)[secondNum];
                averageTimeToTransfer += timeToTransferTotalMap.get(diskId)[secondNum];
            }

            // Take average of each metric based on the duration
            averageUtilization /= countMetrics;
            averageReadBandwidth /= countMetrics;
            averageWriteBandwidth /= countMetrics;
            averageTimeToTransfer /= countMetrics;

            SystemDiskMetric currentMetric = new SystemDiskMetric(startTime, duration,
                    granularityRepository.getByLabel(granularityLabel).getId(),
                    averageUtilization,
                    maxUtilization,
                    minUtilization,
                    averageReadBandwidth,
                    maxReadBandwidth,
                    minReadBandwidth,
                    averageWriteBandwidth,
                    maxWriteBandwidth,
                    minWriteBandwidth,
                    averageTimeToTransfer,
                    maxTimeToTransfer,
                    minTimeToTransfer,
                    unsignedReadBandwidth,
                    unsignedWriteBandwidth);

            currentMetric.setDisk(diskHardwareInformationRepository.getReferenceById(diskId));

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
     * @param diskMetrics The list of <code>SystemDiskMetric</code> objects to record to the
     *                       database
     * @param startTime The starting timestamp of the process metrics collected
     * @param duration The number of seconds which the process metrics were collected for
     * @return The list of <code>SystemDiskMetric</code> entities created
     */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public java.util.List<SystemDiskMetric> saveDiskMetrics(java.util.List<SystemDiskMetric> diskMetrics,
                                                            java.sql.Timestamp startTime,
                                                            int duration,
                                                            java.util.List<DiskHardwareInformation> currentDisks) {

        java.util.List<SystemDiskMetric> validMetrics
                = new java.util.ArrayList<>();

        // As long as we don't support an OS other than Windows, we should return null for others
        // We can't properly handle the device names right now to get the disk numbers
        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
            return null;
        }

        if (duration <= 0) {
            throw new IllegalArgumentException("duration must be greater than 0");
        }

        if (diskMetrics == null || diskMetrics.isEmpty() ) {
            return null;
        }

        // Potential issue?
        // If we collect metrics every second and collect hardware data every minute,
        // drive letters and numbers could change!
        for (SystemDiskMetric metric : diskMetrics) {
            // Identify the disk if there is not one already associated with the metric directly
            if (metric.getDisk() == null) {
                // Skip metrics without a device/friendly name
                if (metric.getDeviceName().trim().isEmpty()) {
                    continue;
                }

                String[] deviceNameSplit = metric.getDeviceName().split(" ");
                Long diskNumber = null;

                // Attempt to get the disk number from the metric collected
                try {
                    diskNumber = Long.parseLong(deviceNameSplit[0]);
                } catch (NumberFormatException e) {
                    // Skip if it can't be extracted
                    continue;
                }

                // Map the metric to a disk based on which disks are connected to the system
                // and their partitions
                for (DiskHardwareInformation disk : currentDisks) {
                    java.util.List<String> partitions = disk.getPartitions();
                    boolean hasMatchingPartitions = true;

                    // Check for matching disk number
                    if (disk.getDiskNumber() == diskNumber) {

                        // Check each partition on the disk to ensure the disk metric lists all of them
                        // Since PhysicalDisk doesn't get the friendly name of disks on Windows,
                        // this is an extra check to ensure metrics match up
                        for (int diskNumIndex  = 1; diskNumIndex < deviceNameSplit.length; diskNumIndex++) {
                            if (!partitions.contains(deviceNameSplit[diskNumIndex].substring(0, 1))) {
                                hasMatchingPartitions = false;
                                break;
                            }
                        }

                        // If all partitions match, then we can set the disk of the metric
                        if (hasMatchingPartitions) {
                            metric.setDisk(disk);
                        }

                    }

                }
            }

            // If there is a matching disk, we're safe to record to the database
            if (metric.getDisk() != null) {
                validMetrics.add(metric);
            }
        }

        // We have no metrics to report if validMetrics is null
        if (validMetrics.isEmpty()) {
            return null;
        }

//        // The collection to be sent to the database to complete the rest of the transaction
        validMetrics
                = aggregateDiskMetrics(diskMetrics, startTime, duration, "HIGH");

        // Commit Application Metrics and return entities with the committed IDs
        return diskMetricRepository.saveAllAndFlush(validMetrics);

    }





    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public java.util.List<DiskHardwareInformation>
            saveDiskInformation(java.util.List<DiskHardwareInformation> diskInformation) {

        java.util.ArrayList<DiskHardwareInformation> disksToSave = new java.util.ArrayList<>();

        if (diskInformation == null || diskInformation.isEmpty() ) {
            return null;
        }

        for (DiskHardwareInformation diskHardwareInformation : diskInformation) {
            // Query for all disks detected based on the unique ID
            DiskHardwareInformation matchingConfiguration
                    = diskHardwareInformationRepository.findByUniqueId(diskHardwareInformation.getUniqueId());

            // Update pre-existing disks and add new ones to the database
            if (matchingConfiguration != null) {
                // Set the ID of this object so that the existing data on this Disk updates
                diskHardwareInformation.setId(matchingConfiguration.getId());
            }
            disksToSave.add(diskHardwareInformation);
        }

        // Save all changes to the database
        return diskHardwareInformationRepository.saveAllAndFlush(disksToSave);

    }

    public java.util.List<DiskHardwareInformation> getStoredDiskInformation() {
        return diskHardwareInformationRepository.findAll(
                org.springframework.data.domain.PageRequest.of(0, 255,
                        org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,
                                "id")))
                .getContent();
    }

    public java.util.List<DiskHardwareInformation> getStoredDiskInformation(int pageNumber) {
        return diskHardwareInformationRepository.findAll(
                        org.springframework.data.domain.PageRequest.of(pageNumber, 255,
                                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,
                                        "id")))
                .getContent();
    }

    public java.util.List<SystemDiskMetric>
                getLatestDiskMetrics(java.util.List<DiskHardwareInformation> diskDevices) {
        List<SystemDiskMetric> diskMetrics = new java.util.ArrayList<>();

        if (diskDevices == null || diskDevices.isEmpty() ) {
            return null;
        }

        for (DiskHardwareInformation diskHardwareInformation : diskDevices) {
            SystemDiskMetric diskMetric
                    = diskMetricRepository.getNewestByDiskId(diskHardwareInformation.getId());
            if (diskMetric != null) {
                diskMetrics.add(diskMetric);
            }
        }

        return diskMetrics;
    }

    /**
     * Returns all disks with their latest metrics
     * This is useful for dashboards that only need the most recent metric for each disk
     *
     * @return Map of disk objects to their latest metric
     */
    public java.util.Map<DiskHardwareInformation, SystemDiskMetric> getAllDisksWithLatestMetrics() {
        // Get all disks
        java.util.List<DiskHardwareInformation> disks = diskHardwareInformationRepository.findAll();

        // Initialize result map
        java.util.Map<DiskHardwareInformation, SystemDiskMetric> disksWithLatestMetrics = new java.util.HashMap<>();

        // For each disk, get its latest metric
        for (DiskHardwareInformation disk : disks) {
            // Find the latest metric for this disk
            SystemDiskMetric metric = diskMetricRepository.findNewestByDiskId(
                    disk.getId());

            // Add to result map if metrics exist
            if (metric != null) {
                disksWithLatestMetrics.put(disk, metric);
            }
        }

        return disksWithLatestMetrics;
    }


    public java.util.List<SystemDiskMetric> getStoredDiskMetrics(Timestamp startTime, Timestamp endTime) {
        return diskMetricRepository.findAllByTimestampBetween(
                startTime, endTime);
    }

    public java.util.List<SystemDiskMetric> getStoredDiskMetrics(Timestamp startTime, Timestamp endTime, int pageNumber) {
        return diskMetricRepository.findAllByTimestampBetween(
                startTime, endTime,
                        PageRequest.of(pageNumber, 255,
                        Sort.by(Sort.Direction.DESC, "timestamp"))
        );
    }

    // Other service methods as needed
}