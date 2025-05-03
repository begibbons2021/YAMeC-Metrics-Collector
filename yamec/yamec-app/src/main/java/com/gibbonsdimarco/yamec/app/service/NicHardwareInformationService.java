package com.gibbonsdimarco.yamec.app.service;

import com.gibbonsdimarco.yamec.app.data.NicHardwareInformation;
import com.gibbonsdimarco.yamec.app.data.SystemNicMetric;
import com.gibbonsdimarco.yamec.app.repository.GranularityRepository;
import com.gibbonsdimarco.yamec.app.repository.NicHardwareInformationRepository;
import com.gibbonsdimarco.yamec.app.repository.SystemNicMetricRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
public class NicHardwareInformationService {

    private final SystemNicMetricRepository nicMetricRepository;
    private final NicHardwareInformationRepository nicHardwareInformationRepository;
    private final GranularityRepository granularityRepository;

    @Autowired
    public NicHardwareInformationService(SystemNicMetricRepository nicMetricRepository,
                                         NicHardwareInformationRepository nicHardwareInformationRepository, GranularityRepository granularityRepository) {
        this.nicMetricRepository = nicMetricRepository;
        this.nicHardwareInformationRepository = nicHardwareInformationRepository;
        this.granularityRepository = granularityRepository;
    }


    // Assuming a record timespan of 1
    public List<SystemNicMetric> aggregateNicMetrics(List<SystemNicMetric> nicMetrics,
                                                     Timestamp startTime,
                                                     int duration,
                                                     String granularityLabel) {

        // Error handling/edge cases in case this function is improperly called
        if (duration < 1) {
            throw new IllegalArgumentException("Duration must be greater than 0");
        }

        else if (nicMetrics == null) {
            return null;
        }

        long startTimeAsLong = startTime.getTime();

        // Use a map to group metrics by the device ID
        List<UUID> recordedDiskIds = new ArrayList<>();
        HashMap<UUID, Integer> numValidNicMetrics = new HashMap<>();

        HashMap<UUID, long[]> sendBandwidthTotalMap = new HashMap<>();
        HashMap<UUID, Long> sendBandwidthMaxMap = new HashMap<>();
        HashMap<UUID, Long> sendBandwidthMinMap = new HashMap<>();
        HashMap<UUID, Boolean> sendBandwidthUnsignedMap = new HashMap<>();

        HashMap<UUID, long[]> receiveBandwidthTotalMap = new HashMap<>();
        HashMap<UUID, Long> receiveBandwidthMaxMap = new HashMap<>();
        HashMap<UUID, Long> receiveBandwidthMinMap = new HashMap<>();
        HashMap<UUID, Boolean> receiveBandwidthUnsignedMap = new HashMap<>();


        // Go through all processes related to this application and get the total resource use
        // by the application per second
        for (SystemNicMetric currentMetric : nicMetrics) {

            // Fail-safe in case a metric is not assigned a Memory Hardware metric (can't properly group the metrics)
            if (currentMetric.getNic() == null)
            {
                continue;
            }

            // Fail-safe in case a metric is not given a timestamp (Can't accurately get metrics from it)
            if (currentMetric.getTimestamp() == null) {
                continue;
            }

            UUID nicId = currentMetric.getNic().getId();

            // Get average, max, and min metrics
            long averageSendBandwidth = currentMetric.getAvgSendBandwidth();
            long maxSendBandwidth = currentMetric.getMaxSendBandwidth();
            long minSendBandwidth = currentMetric.getMinSendBandwidth();

            long averageReceiveBandwidth = currentMetric.getAvgReceiveBandwidth();
            long maxReceiveBandwidth = currentMetric.getMaxReceiveBandwidth();
            long minReceiveBandwidth = currentMetric.getMinReceiveBandwidth();

            boolean unsignedSendBandwidth = currentMetric.isSendBandwidthUnsigned();
            boolean unsignedReceiveBandwidth = currentMetric.isReceiveBandwidthUnsigned();

            long timestampAsLong = currentMetric.getTimestamp().getTime();

            // Used to map process data to the second number of the data
            int secondsSinceStartTime = (int)((timestampAsLong - startTimeAsLong)/1000);


            // Fail-safe if a record outside the duration is included
            if (secondsSinceStartTime > duration - 1) {
                continue;
            }

            // Create key maps for all values if one doesn't exist for a specific CPU
            if (!recordedDiskIds.contains(nicId)) {
                recordedDiskIds.add(nicId);
                numValidNicMetrics.put(nicId, 0);

                sendBandwidthTotalMap.put(nicId, new long[duration]);
                sendBandwidthMaxMap.put(nicId, Long.MIN_VALUE);
                sendBandwidthMinMap.put(nicId, Long.MAX_VALUE);

                receiveBandwidthTotalMap.put(nicId, new long[duration]);
                receiveBandwidthMaxMap.put(nicId, Long.MIN_VALUE);
                receiveBandwidthMinMap.put(nicId, Long.MAX_VALUE);

                sendBandwidthUnsignedMap.put(nicId, false);
                receiveBandwidthUnsignedMap.put(nicId, false);
            }

//            // If the disk is not being used at all, skip this metric
//            if (averageUtilization == 0
//                    && maxUtilization == 0
//                    && minUtilization == 0
//                    && averageReadBandwidth == 0
//                    && maxReadBandwidth == 0
//                    && minReadBandwidth == 0
//                    && averageWriteBandwidth == 0
//                    && maxWriteBandwidth == 0
//                    && minWriteBandwidth == 0
//                    && averageTimeToTransfer == 0
//                    && maxTimeToTransfer == 0
//                    && minTimeToTransfer == 0) {
//                continue;
//            }

            // This is a valid metric, and the timestamp
            numValidNicMetrics.put(nicId, numValidNicMetrics.get(nicId) + 1);

            // Add to running totals and extract maximums and minimums
            sendBandwidthTotalMap.get(nicId)[secondsSinceStartTime] = averageSendBandwidth;
            if (sendBandwidthMaxMap.get(nicId) < maxSendBandwidth) {
                sendBandwidthMaxMap.put(nicId, maxSendBandwidth);
            }
            if (sendBandwidthMinMap.get(nicId) > minSendBandwidth) {
                sendBandwidthMinMap.put(nicId, minSendBandwidth);
            }

            // Add to running totals and extract maximums and minimums
            receiveBandwidthTotalMap.get(nicId)[secondsSinceStartTime] = averageReceiveBandwidth;
            if (receiveBandwidthMaxMap.get(nicId) < maxReceiveBandwidth) {
                receiveBandwidthMaxMap.put(nicId, maxReceiveBandwidth);
            }
            if (receiveBandwidthMinMap.get(nicId) > minReceiveBandwidth) {
                receiveBandwidthMinMap.put(nicId, minReceiveBandwidth);
            }

            // Set unsigned boolean values to true if even one value is reported as true
            if (unsignedSendBandwidth) {
                sendBandwidthUnsignedMap.put(nicId, true);
            }

            if (unsignedReceiveBandwidth) {
                receiveBandwidthUnsignedMap.put(nicId, true);
            }

        }

        List<SystemNicMetric> metricsToRecord = new ArrayList<>();

        for (UUID nicId : recordedDiskIds) {



            // Data containers for metrics
            long averageReadBandwidth = 0;
            long maxReadBandwidth = sendBandwidthMaxMap.get(nicId);
            long minReadBandwidth = sendBandwidthMinMap.get(nicId);

            long averageWriteBandwidth = 0;
            long maxWriteBandwidth = receiveBandwidthMaxMap.get(nicId);
            long minWriteBandwidth = receiveBandwidthMinMap.get(nicId);

            boolean unsignedSendBandwidth = sendBandwidthUnsignedMap.get(nicId);
            boolean unsignedReceiveBandwidth = receiveBandwidthUnsignedMap.get(nicId);

            int countMetrics = numValidNicMetrics.get(nicId);

            for (int secondNum = 0; secondNum < duration; secondNum++) {
                // Sum total usages for all valid processes and applications
                averageReadBandwidth += sendBandwidthTotalMap.get(nicId)[secondNum];
                averageWriteBandwidth += receiveBandwidthTotalMap.get(nicId)[secondNum];
            }

            // Take average of each metric based on the duration
            averageReadBandwidth /= countMetrics;
            averageWriteBandwidth /= countMetrics;

            SystemNicMetric currentMetric = new SystemNicMetric(startTime, duration,
                    granularityRepository.getByLabel(granularityLabel).getId(),
                    averageReadBandwidth,
                    maxReadBandwidth,
                    minReadBandwidth,
                    averageWriteBandwidth,
                    maxWriteBandwidth,
                    minWriteBandwidth,
                    unsignedSendBandwidth,
                    unsignedReceiveBandwidth);

            currentMetric.setNic(nicHardwareInformationRepository.getReferenceById(nicId));

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
     * @param nicMetrics The list of <code>SystemNicMetric</code> objects to record to the
     *                       database
     * @param startTime The starting timestamp of the process metrics collected
     * @param duration The number of seconds which the process metrics were collected for
     * @return The list of <code>SystemNicMetric</code> entities created
     */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public java.util.List<SystemNicMetric> saveNicMetrics(java.util.List<SystemNicMetric> nicMetrics,
                                                            java.sql.Timestamp startTime,
                                                            int duration,
                                                            java.util.List<NicHardwareInformation> currentNics) {

        java.util.List<SystemNicMetric> validMetrics
                = new java.util.ArrayList<>();

        if (duration <= 0) {
            throw new IllegalArgumentException("duration must be greater than 0");
        }

        // Go through all NIC metrics and find which hardware device (of those passed by parameter)
        // they are associated with (if any)
        for (SystemNicMetric metric : nicMetrics) {
            if (metric.getNic() == null) {
                // Skip metrics without a device/friendly name
                if (metric.getDeviceName().trim().isEmpty()) {
                    continue;
                }

                // Map the metric to a NIC based on the friendly name
                for (NicHardwareInformation nic : currentNics) {
                    if (nic.getFriendlyName().trim().equals(metric.getDeviceName().trim())) {
                        metric.setNic(nic);
                    }
                }
            }

            // If there is a matching disk, we're safe to record to the database
            if (metric.getNic() != null) {
                validMetrics.add(metric);
            }
        }

        // We have no metrics to report if validMetrics is null
        if (validMetrics.isEmpty()) {
            return null;
        }

//        // The collection to be sent to the database to complete the rest of the transaction
        validMetrics
                = aggregateNicMetrics(nicMetrics, startTime, duration, "HIGH");

        // Commit Application Metrics and return entities with the committed IDs
        return nicMetricRepository.saveAllAndFlush(validMetrics);

    }




    @Transactional
    public java.util.List<NicHardwareInformation>
            saveNicInformation(java.util.List<NicHardwareInformation> nicInformation) {

        java.util.ArrayList<NicHardwareInformation> nicsToSave = new java.util.ArrayList<>();

        for (NicHardwareInformation nicHardwareInformation : nicInformation) {
            // Query for all disks detected
            NicHardwareInformation matchingConfiguration
                    = nicHardwareInformationRepository.findByUniqueId(nicHardwareInformation.getUniqueId());

            // Update pre-existing disks and add new ones to the database
            nicsToSave.add(Objects.requireNonNullElse(matchingConfiguration, nicHardwareInformation));
        }

        // Save all changes to the database
        return nicHardwareInformationRepository.saveAllAndFlush(nicsToSave);

    }

    public java.util.List<NicHardwareInformation> getStoredDiskInformation() {
        return nicHardwareInformationRepository.findAll(
                org.springframework.data.domain.PageRequest.of(0, 255,
                        org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,
                                "id")))
                .getContent();
    }

    public java.util.List<NicHardwareInformation> getStoredDiskInformation(int pageNumber) {
        return nicHardwareInformationRepository.findAll(
                        org.springframework.data.domain.PageRequest.of(pageNumber, 255,
                                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,
                                        "id")))
                .getContent();
    }

    // Other service methods as needed
}