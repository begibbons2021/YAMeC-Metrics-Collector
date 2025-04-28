package com.gibbonsdimarco.yamec.app.service;

import com.gibbonsdimarco.yamec.app.data.Application;
import com.gibbonsdimarco.yamec.app.data.ApplicationMetric;
import com.gibbonsdimarco.yamec.app.data.ProcessMetric;
import com.gibbonsdimarco.yamec.app.repository.ApplicationMetricRepository;
import com.gibbonsdimarco.yamec.app.repository.ApplicationRepository;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Manages repositories for Application and ApplicationMetric data in the database
 *
 */
@Service
public class ApplicationDataService {

    ApplicationRepository applicationRepository;
    ApplicationMetricRepository applicationMetricRepository;

    @Autowired
    public ApplicationDataService(ApplicationRepository applicationRepository,
                                  ApplicationMetricRepository applicationMetricRepository) {
        this.applicationRepository = applicationRepository;
        this.applicationMetricRepository = applicationMetricRepository;
    }

    /**
     * <p>Records information about applications on the system to the database from the
     * <code>ArrayList<ProcessMetric></code> objects passed by parameter, including their
     * process names, CPU usage time, and memory usage for a given timespan expressed by
     * the <code>startTime</code> and <code>duration</code> parameters.</p>
     *
     * <p>Both <code>Application</code> records and <code>ApplicationMetric</code> records are
     * committed to the database upon a successful transaction. Only process information within
     * the specified timespan is considered in the creation of <code>ApplicationMetric</code>
     * entities (from <code>startTime</code> to <code>duration</code>
     * seconds after)</p>
     *
     * @param processMetrics The list of <code>ProcessMetric</code> objects to record to the
     *                       database as <code>ApplicationMetric</code> entities
     * @param startTime The starting timestamp of the process metrics collected
     * @param duration The number of seconds which the process metrics were collected for
     * @return The list of <code>ApplicationMetric</code> entities created
     */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public java.util.List<ApplicationMetric> saveApplicationMetrics(java.util.List<ProcessMetric> processMetrics,
                                                                    java.sql.Timestamp startTime,
                                                                    int duration) {

        if (duration <= 0) {
            throw new IllegalArgumentException("duration must be greater than 0");
        }


        java.util.List<Application> newApplications = new java.util.ArrayList<>();

        // The collection to be sent to the database to complete the rest of the transaction
        java.util.ArrayList<ApplicationMetric> applicationMetrics = new java.util.ArrayList<>();

        // Associates Application IDs with process metrics
        java.util.HashMap<UUID, java.util.ArrayList<ProcessMetric>> applicationNameProcessMetrics =
                new java.util.HashMap<>();

        long startTimeAsLong = startTime.getTime();

        for (ProcessMetric activeProcess : processMetrics) {
            // Check if the application exists in the DB
            Application application = applicationRepository.findByApplicationName(activeProcess.getProcessName());

            // Add process names (Application executables) to Application repository
            // while returning the respective application for grouping if it does not
            if (application == null) {
                application = new Application(activeProcess.getProcessName());
//                newApplications.add(application);
                application = applicationRepository.saveAndFlush(application);
            }

            // Add processes which have an application associated with them to the appropriate
            // Application map
            if (!applicationNameProcessMetrics.containsKey(application.getId())) {
                    applicationNameProcessMetrics.put(application.getId(), new java.util.ArrayList<>());
            }

            applicationNameProcessMetrics.get(application.getId()).add(activeProcess);

        }

//        newApplications = applicationRepository.saveAllAndFlush(newApplications);

        // Create ApplicationMetrics based on information from all processes for each application
        for (UUID applicationName : applicationNameProcessMetrics.keySet()) {

            // Sum the number of processes and metrics per second
//            long[] numProcessesPerSecond = new long[duration];
            double[] totalCpuUsagePerSecond = new double[duration];
            long[] totalPhysicalMemoryUsedPerSecond = new long[duration];
            long[] totalVirtualMemoryUsedPerSecond = new long[duration];


            // Go through all processes related to this application and get the total resource use
            // by the application per second
            for (ProcessMetric activeProcess : applicationNameProcessMetrics.get(applicationName)) {
                // Get all metrics for just this process
                double processCpuUsage = activeProcess.getCpuUsage();
                long processPhysicalMemoryUsed = activeProcess.getPhysicalMemoryUsage();
                long processVirtualMemoryUsed = activeProcess.getVirtualMemoryUsage();

                // Skip processes which are reporting 0 resource use (no data to report)
                if (processCpuUsage == 0
                        && processPhysicalMemoryUsed == 0
                        && processVirtualMemoryUsed == 0) {
                    continue;
                }

                // Fail-safe in case a process is not given a timestamp (Can't accurately get metrics from it)
                if (activeProcess.getTimestamp() == null) {
                    continue;
                }

                long timestampAsLong = activeProcess.getTimestamp().getTime();

                // Used to map process data to the second number of the data
                int secondsSinceStartTime = (int)((timestampAsLong - startTimeAsLong)/1000);

                // TODO: Remove debug messages for release
                LoggerFactory.getLogger(ApplicationDataService.class)
                            .debug("Start Time: {} - Duration: {} vs Timestamp: {}",
                            startTimeAsLong/1000, duration, secondsSinceStartTime);

                // Fail-safe if a record outside the duration is included
                if (secondsSinceStartTime > duration - 1) {
                    continue;
                }

                // Add to running totals per second
                totalCpuUsagePerSecond[secondsSinceStartTime] += processCpuUsage;
                totalPhysicalMemoryUsedPerSecond[secondsSinceStartTime] += processPhysicalMemoryUsed;
                totalVirtualMemoryUsedPerSecond[secondsSinceStartTime] += processVirtualMemoryUsed;
//                numProcessesPerSecond[secondsSinceStartTime]++;
            }

            // Data containers for metrics
            double averageCpuUsage = 0;
            long averagePhysicalMemoryUsed = 0;
            long averageVirtualMemoryUsed = 0;

            double maxCpuUsage = Double.MIN_VALUE;
            long maxPhysicalMemoryUsed = Long.MIN_VALUE;
            long maxVirtualMemoryUsed = Long.MIN_VALUE;

            double minCpuUsage = Double.MAX_VALUE;
            long minPhysicalMemoryUsed = Long.MAX_VALUE;
            long minVirtualMemoryUsed = Long.MAX_VALUE;

            for (int secondNum = 0; secondNum < duration; secondNum++) {

                // Sum total usages for all valid processes and applications
                averageCpuUsage += totalCpuUsagePerSecond[secondNum];
                averagePhysicalMemoryUsed += totalPhysicalMemoryUsedPerSecond[secondNum];
                averageVirtualMemoryUsed += totalVirtualMemoryUsedPerSecond[secondNum];


                if (secondNum == 0) {
                    // Set initial values for all the maximums and minimums at second 0
                    maxCpuUsage = minCpuUsage = totalCpuUsagePerSecond[secondNum];
                    maxPhysicalMemoryUsed = minPhysicalMemoryUsed = totalPhysicalMemoryUsedPerSecond[secondNum];
                    maxVirtualMemoryUsed = minVirtualMemoryUsed = totalVirtualMemoryUsedPerSecond[secondNum];
                } else {
                    // Set the maximums and minimums of each metric as they are identified
                    if (totalCpuUsagePerSecond[secondNum] > maxCpuUsage) {
                        maxCpuUsage = totalCpuUsagePerSecond[secondNum];
                    } else if (totalCpuUsagePerSecond[secondNum] < minCpuUsage) {
                        minCpuUsage = totalCpuUsagePerSecond[secondNum];
                    }

                    if (totalPhysicalMemoryUsedPerSecond[secondNum] > maxPhysicalMemoryUsed) {
                        maxPhysicalMemoryUsed = totalPhysicalMemoryUsedPerSecond[secondNum];
                    } else if (totalPhysicalMemoryUsedPerSecond[secondNum] < minPhysicalMemoryUsed) {
                        minPhysicalMemoryUsed = totalPhysicalMemoryUsedPerSecond[secondNum];
                    }

                    if (totalVirtualMemoryUsedPerSecond[secondNum] > maxVirtualMemoryUsed) {
                        maxVirtualMemoryUsed = totalVirtualMemoryUsedPerSecond[secondNum];
                    } else if (totalVirtualMemoryUsedPerSecond[secondNum] < minVirtualMemoryUsed) {
                        minVirtualMemoryUsed = totalVirtualMemoryUsedPerSecond[secondNum];
                    }

                }

            }

            // Take average of each metric based on the duration
            averageCpuUsage /= duration;
            averagePhysicalMemoryUsed /= duration;
            averageVirtualMemoryUsed /= duration;

            if (LoggerFactory.getLogger(ApplicationDataService.class).isDebugEnabled()) {
                LoggerFactory.getLogger(ApplicationDataService.class).debug("Process {} - Average CPU Usage: {}",
                        applicationRepository.getById(applicationName).getApplicationName(), averageCpuUsage);
            }

            // Create Metric object
            applicationMetrics.add(new ApplicationMetric(applicationRepository.getById(applicationName),
                                                            startTime, duration,
                                                            averageCpuUsage,
                                                            averagePhysicalMemoryUsed,
                                                            averageVirtualMemoryUsed));


        }

        // Commit Application Metrics and return entities with the committed IDs
        return applicationMetricRepository.saveAllAndFlush(applicationMetrics);

    }

    public java.util.List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    public java.util.List<ApplicationMetric> getAllApplicationMetrics() {
        return applicationMetricRepository.findAll();
    }

    public java.util.List<ApplicationMetric> getApplicationMetricsByApplicationId(UUID applicationId) {
        return applicationMetricRepository.findByApplicationId(applicationId);
    }

}
