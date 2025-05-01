package com.gibbonsdimarco.yamec.app;

import com.gibbonsdimarco.yamec.app.data.*;
import com.gibbonsdimarco.yamec.app.jni.SystemMonitorManagerJNI;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.*;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class YamecApplicationTests {

    private static SystemMonitorManagerJNI monitor;

    @Test
    void contextLoads() {
    }

    @BeforeAll
    static void systemMonitorManagerCreation() {
        try {
            System.err.println("Starting System Monitor Manager...");
            monitor = new SystemMonitorManagerJNI();
        }
        catch (Exception e) {
            fail("An exception was thrown while creating the SystemMonitorManager.\n\n"
                    + e.getMessage() + "\n"
                    + Arrays.toString(e.getStackTrace()));
        }

        // Manually collect the needed counter data
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {
            fail("The timer to wait to collect data from the SystemMonitorManager was interrupted.");
        }

        try
        {
            int dataCollectionSuccess = monitor.collectCounterData();
            if (dataCollectionSuccess != 0)
            {
                fail("SystemMonitorManager#collectCounterData() returned status code " + dataCollectionSuccess);
            }
        }
        catch (Exception e) {
            fail("An exception was thrown while calling SystemMonitorManager#collectCounterData().\n\n"
                    + e.getMessage() + "\n"
                    + Arrays.toString(e.getStackTrace()));
        }

    }

    @Test
    void systemMonitorDataTests() {
        assert(testSystemMonitorManager());
    }

    @Test
    void testGetCpuMetricsLoads() {
        assertDoesNotThrow(() -> {
            SystemCpuMetric metric = monitor.getCpuMetrics();
        }, "Calling getCpuMetrics() throws an exception.");
    }

    @Test
    void testGetCpuMetricsReturnsValidData() {
        SystemCpuMetric metric = monitor.getCpuMetrics();

        // Returns not null
        assertNotNull(metric,
                "getCpuMetrics() should not return null.");

        // Device Name returns not null
        assertTrue("getCpuMetrics().getDeviceName() should not return null.",
                        metric.getDeviceName() != null);

        // Utilization returns between 0% and 100%
        assertTrue("getCpuMetrics().getUsage() should be returning a value >= 0%"
                        + " but it is " + metric.getAverageUtilization(),
                        metric.getAverageUtilization() >= 0.0);
        assertTrue("getCpuMetrics().getUsage() should be returning a value <= 100%"
                        + " but it is " + metric.getAverageUtilization(),
                        metric.getAverageUtilization() <= 100.0);
    }

    @Test
    void testGetMemoryMetricsReturnsValidData() {
        SystemMemoryMetric metric = monitor.getMemoryMetrics();
        MemoryHardwareInformation memoryHardware = monitor.getMemoryHardwareInformation();

        // Returns not null
        assertNotNull(metric,
                "getMemoryMetrics() should not return null.");

        // Use the unsigned values for physical and virtual memory usage
        boolean physicalMemoryIsUnsigned = metric.isPhysicalMemoryAvailableUnsigned();
        boolean capacityIsUnsigned = memoryHardware.isCapacityUnsigned();

        // Separate tests for returned signed and unsigned values
        if (physicalMemoryIsUnsigned && capacityIsUnsigned) {
            // Physical memory available is less than the total physical memory
            // It also can't be equal total physical memory because otherwise, how is this program running?
            assertTrue("Available Physical Memory must be less than Total Physical Memory, "
                                + "but Available Physical Memory = "
                                + metric.getAveragePhysicalUtilization() + " bytes "
                                + "and Total Physical Memory = "
                                + memoryHardware.getCapacityAsUnsignedString() + " bytes ",
                    Long.compareUnsigned(metric.getAveragePhysicalUtilization(),
                            memoryHardware.getCapacity()) < 0);

        } else if (!physicalMemoryIsUnsigned && !capacityIsUnsigned) {
            // Physical memory used is greater than 0
            // It can't be 0 because otherwise, how is this program running?
            assertTrue("Physical Memory Used must be greater (or equal to) than 0, but it is "
                                + metric.getAveragePhysicalUtilization() + " instead ",
                    metric.getAveragePhysicalUtilization() > 0);

            // Physical memory used must not be more than the total physical memory available
            // It can technically be the capacity, but the OS would likely never allow memory to get that full
            assertTrue("Physical Memory Used must be less than Total Physical Memory, "
                            + "but Physical Memory Used = "
                            + metric.getAveragePhysicalUtilization() + " bytes "
                            + "and Total Physical Memory = "
                            + memoryHardware.getCapacity() + " bytes ",
                    metric.getAveragePhysicalUtilization() <=
                            memoryHardware.getCapacity());
        } else {
            // This is a bizarre condition. Just choose signed or unsigned, for the sake of everyone's sanity
            fail("Signed-unsigned attributes of physical memory and capacity are not the same."
                    + " Keeping them the same ensures they can be properly compared.");
        }

        boolean virtualMemoryIsUnsigned = metric.isVirtualMemoryCommittedUnsigned();

        if (!virtualMemoryIsUnsigned) {
            // Virtual memory must be greater than or equal to 0 bytes if stored as a signed value
            assertTrue("Virtual Memory Utilization must be greater than or equal to 0 bytes, "
                            + "but Virtual Memory Utilization = "
                            + metric.getAverageVirtualUtilization() + " bytes ",
                    metric.getAverageVirtualUtilization() > 0);
        }

        // A percentage of committed memory in use must be between 0% and 100%
        // Likely, if it's close to 100%, the memory committed will be increased to prevent it,
        // but if the commit limit is reached and the page size limit is reached, it may not.
//        assertTrue("getMemoryMetrics().getVirtualMemoryCommittedUsage() should be returning a value >= 0%"
//                        + " but it is " + metric.getMinVirtualUtilization(),
//                metric.getMinVirtualUtilization() >= 0.0);
//        assertTrue("getMemoryMetrics().getVirtualMemoryCommittedUsage() should be returning a value <= 100%"
//                        + " but it is " + metric.getMinVirtualUtilization(),
//                metric.getMinVirtualUtilization() <= 100.0);

    }


    @Test
    void testGetDiskMetricsReturnsValidData() {
        ArrayList<SystemDiskMetric> metrics = monitor.getDiskMetrics();
//        MemoryHardwareInformation memoryHardware = monitor.getMemoryHardwareInformation();

        // Returns not null
        assertNotNull(metrics,
                "getDiskMetrics() should not return null.");

        // Returns at least one metric (_Total on Windows and a single Disk instance)
        assertTrue("getDiskMetrics() should not return no metrics (there should at "
                    + "least be a \"_Total\" member (on Windows) and one other Disk if the query was successful)",
                    !metrics.isEmpty());

        boolean hasOneUniqueDisk = false;

        for (SystemDiskMetric diskMetric : metrics) {
            // Disk Metric should not be null
            assertNotNull(diskMetric, "No SystemDiskMetric in the returned metrics list should be null");

            // The name of the Disk Metric device should not be null, either
            assertNotNull(diskMetric.getDeviceName(), "diskMetric.getDeviceName() should not be null");

            // Check for a uniquely named Disk Instance
            if (!hasOneUniqueDisk) {
                if (!diskMetric.getDeviceName().equals("_Total") || !SystemUtils.IS_OS_WINDOWS) {
                    hasOneUniqueDisk = true;
                }
            }

            if (!diskMetric.isReadBandwidthUnsigned())
            {
                // Read bandwidth must not be a negative number
                assertTrue("Disk Read Bandwidth must be greater than or equal to 0, but it is "
                                + diskMetric.getAvgReadBandwidth() + "B/s for Disk " + diskMetric.getDeviceName(),
                        diskMetric.getAvgReadBandwidth() >= 0);
            }

            if (!diskMetric.isWriteBandwidthUnsigned())
            {
                // Write bandwidth must not be a negative number
                assertTrue("Disk write bandwidth must be greater than or equal to 0, but it is "
                                + diskMetric.getAvgWriteBandwidth() + "B/s for Disk " + diskMetric.getDeviceName(),
                        diskMetric.getAvgWriteBandwidth() >= 0);
            }

            // Average time to transfer must not be a negative number
            assertTrue("Disk write bandwidth must be greater than or equal to 0, but it is "
                            + diskMetric.getAvgTimeToTransfer() + "sec/Transfer for Disk "
                            + diskMetric.getDeviceName(),
                    diskMetric.getAvgTimeToTransfer() >= 0);

            // A percentage of disk utilization must be between 0% and 100%
            assertTrue("diskMetric.getUsage() should be returning a value >= 0%"
                            + " but it is " + diskMetric.getAvgUtilization() + "% for Disk " + diskMetric.getDeviceName(),
                    diskMetric.getAvgUtilization() >= 0.0);
            assertTrue("diskMetric.getUsage() should be returning a value <= 100%"
                            + " but it is " + diskMetric.getAvgUtilization() + "% for Disk " + diskMetric.getDeviceName(),
                    diskMetric.getAvgUtilization() <= 100.0);

        }

    }

    @Test
    void testGetNicMetricsReturnsValidData() {
        ArrayList<SystemNicMetric> metrics = monitor.getNicMetrics();

        // Returns not null
        assertNotNull(metrics,
                "getNicMetrics() should not return null.");

        // Not all collections of NicMetrics have data collected (all NICs could be disabled, for example)

        for (SystemNicMetric nicMetric : metrics) {
            // NIC Metric should not be null
            assertNotNull(nicMetric, "No SystemNicMetric in the returned metrics list should be null");

            // The name of the NIC Metric device should not be null, either
            assertNotNull(nicMetric.getDeviceName(), "nicMetric.getDeviceName() should not be null");


            if (!nicMetric.isBytesSentUnsigned())
            {
                // Read bandwidth must not be a negative number
                assertTrue("NIC Sent Bytes must be greater than or equal to 0, but it is "
                                + nicMetric.getAvgBytesSent() + "B/s for Disk " + nicMetric.getDeviceName(),
                        nicMetric.getAvgBytesSent() >= 0);
            }

            if (!nicMetric.isBytesReceivedUnsigned())
            {
                // Write bandwidth must not be a negative number
                assertTrue("NIC Received Bytes must be greater than or equal to 0, but it is "
                                + nicMetric.getAvgBytesReceived() + "B/s for Disk " + nicMetric.getDeviceName(),
                        nicMetric.getAvgBytesReceived() >= 0);
            }

            if (!nicMetric.isNicBandwidthUnsigned())
            {
                // Write bandwidth must not be a negative number
                assertTrue("NIC Received Bytes must be greater than or equal to 0, but it is "
                                + nicMetric.getNicBandwidth() + "B/s for Disk " + nicMetric.getDeviceName(),
                        nicMetric.getNicBandwidth() >= 0);
            }


        }

    }

    @Test
    void testGetProcessMetricsReturnsValidData() {


        try {
            ArrayList<ProcessMetric> processData = monitor.getProcessMetrics();

            // Returns not null
            assertNotNull(processData,
                    "getProcessMetrics() should not return null.");

            // We should be returning a lot of different processes!
            // Trust me when I say YAMeC is not the only program running on my test machine
            // when I run these tests
            assertTrue("getProcessMetrics() should always return one or more processes.",
                    !processData.isEmpty());

            // Select 5 random processes
            java.util.Random random = new java.util.Random();
            int numProcessesToCheck = Math.min(processData.size(), 5);
            // Store process indices to check in an array
            int[] processIndicesToCheck = new int[numProcessesToCheck];
            for (int i = 0; i < numProcessesToCheck; i++) {
                processIndicesToCheck[i] = -1;

                // Continue until an unused index is found
                while (processIndicesToCheck[i] == -1) {
                    // Pick a random index from the ArrayList
                    int processIndex = random.nextInt(processData.size());

                    // Check the list of selected indices for it
                    boolean foundMatch = false;
                    for (int k : processIndicesToCheck) {
                        if (k == processIndex) {
                            foundMatch = true;
                            break;
                        }
                    }

                    // If it's not in the list, add it
                    if (!foundMatch) {
                        processIndicesToCheck[i] = processIndex;
                    }
                }

            }


            for (int i = 0; i < 5; i++) {
                ProcessMetric processMetric = processData.get(processIndicesToCheck[i]);

                assertNotNull(processMetric,
                        "No ProcessMetric in the returned metrics list should be null, "
                                + "but index " + processIndicesToCheck[i] + "returned null");

                assertNotNull(processMetric.getProcessName(),
                        "processMetric.getProcessName() should not be null, "
                                + "but index " + processIndicesToCheck[i] + "returned null");

                assertTrue("processMetric.getProcessName() should not be empty, "
                            + "but index " + processIndicesToCheck[i] + "returned an empty name",
                            !processMetric.getProcessName().isEmpty());

                assertTrue("processMetric.getProcessId() should be retuning a value > 0, "
                                + "but index " + processIndicesToCheck[i]
                                + " (" + processMetric.getProcessName() + ")" + " has the PID of "
                                + processMetric.getProcessId(),
                            processMetric.getProcessId() >= 0);

                // A percentage of disk utilization must be between 0% and 100%
                assertTrue("processMetric.getCpuUsage() should be returning a value >= 0%"
                                + " but it is " + processMetric.getCpuUsage() + "% for Process "
                                + processMetric.getProcessName() + " ( " + processMetric.getProcessId() + " )",
                        processMetric.getCpuUsage() >= 0.0);
                assertTrue("processMetric.getCpuUsage() should be returning a value <= 100%"
                                + " but it is " + processMetric.getCpuUsage() + "% for Process "
                                + processMetric.getProcessName() + " ( " + processMetric.getProcessId() + " )"
                                + processMetric.getProcessName() + " ( " + processMetric.getProcessId() + " )",
                        processMetric.getCpuUsage() <= 100.0);

                // Physical memory must be greater than or equal to 0 bytes if stored as a signed value
                assertTrue("Physical Memory Bytes Used must be greater than or equal to 0 bytes, "
                                + "but Physical Memory Bytes Used = "
                                + processMetric.getPhysicalMemoryUsage() + " bytes for Process "
                                + processMetric.getProcessName() + " ( " + processMetric.getProcessId() + " )"
                                + processMetric.getProcessName() + " ( " + processMetric.getProcessId() + " )",
                        processMetric.getPhysicalMemoryUsage() >= 0);

                // Virtual memory must be greater than or equal to 0 bytes if stored as a signed value
                assertTrue("Virtual Memory Bytes Used must be greater than or equal to 0 bytes, "
                                + "but Virtual Memory Bytes Used = "
                                + processMetric.getVirtualMemoryUsage() + " bytes for Process "
                                + processMetric.getProcessName() + " ( " + processMetric.getProcessId() + " )"
                                + processMetric.getProcessName() + " ( " + processMetric.getProcessId() + " )",
                        processMetric.getVirtualMemoryUsage() >= 0);


            }

        } catch (Exception e) {
            fail("An exception was thrown while getting the process data.\n\n"
                    + e.getMessage() + "\n"
                    + Arrays.toString(e.getStackTrace()));
        }



    }


    @Test
    void systemMonitorManagerCpuHardwareInfoTests()
    {
        try {
            CpuHardwareInformation cpuInfo = monitor.getCpuHardwareInformation();

            assertTrue("No CPU information was able to be retrieved (returned null).",
                    cpuInfo != null);

            assert(cpuInfo != null);

            System.err.println("CPU Information: ");
            System.err.printf("\tProcessor: %s\n", cpuInfo.getFriendlyName());
            System.err.printf("\t\t%d core(s), %d thread(s)\n",
                    cpuInfo.getCoreCount(), cpuInfo.getLogicalProcessorCount());
            System.err.printf("\t\tArchitecture: %s\n", cpuInfo.getArchitecture());
            System.err.printf("\t\tVirtualization: %s\n", cpuInfo.isVirtualizationEnabled() ? "Enabled" : "Disabled");
            System.err.printf("\t\tNUMA Node Count: %d\n", cpuInfo.getNumaNodeCount());
            System.err.println("\t\tCache Sizes: ");
            System.err.printf("\t\t\tL1: %d bytes\n", cpuInfo.getL1CacheSize());
            System.err.printf("\t\t\tL2: %d bytes\n", cpuInfo.getL2CacheSize());
            System.err.printf("\t\t\tL3: %d bytes\n", cpuInfo.getL3CacheSize());
            System.out.println();

            assertTrue("CPU Friendly Name was a null value, which should not happen",
                        cpuInfo.getFriendlyName() != null);
            assertTrue("CPU Architecture was a null/empty value, which should not happen",
                    cpuInfo.getArchitecture() != null || !cpuInfo.getFriendlyName().isEmpty());

        } catch (Exception e) {
            fail("An exception was thrown while getting the CPU hardware data.\n\n"
                    + e.getMessage() + "\n"
                    + Arrays.toString(e.getStackTrace()));
        }

    }

    @Test
    void systemMonitorGetsProcessData() {


        try {
            System.err.println("Testing Process Data Retrieval");

            ArrayList<ProcessMetric> processData = monitor.getProcessMetrics();

            assert(processData != null);

            assert(!processData.isEmpty());

            System.err.println("Printing 5 random processes...");

            for (int i = 0; i < 5; i++) {
                java.util.Random random = new java.util.Random();
                int selectedProcessIndex = random.nextInt(processData.size());

                System.err.printf("%s (pid: %d)\n", processData.get(selectedProcessIndex).getProcessName(),
                                                        processData.get(selectedProcessIndex).getProcessId());
                System.err.printf("\tCPU Usage: %f\n", processData.get(selectedProcessIndex).getCpuUsage());
                System.err.printf("\tPhysical Memory Usage: %d bytes\n",
                        processData.get(selectedProcessIndex).getPhysicalMemoryUsage());
                System.err.printf("\tVirtual Memory Usage: %d bytes\n",
                        processData.get(selectedProcessIndex).getVirtualMemoryUsage());
                System.err.println();
            }

            for (ProcessMetric processMetric : processData)
            {
                String processName = processMetric.getProcessName();
                if (processName != null && processName.compareTo("Taskmgr") == 0)
                {
                    System.err.printf("%s (pid: %d)\n", processMetric.getProcessName(),
                            processMetric.getProcessId());
                    System.err.printf("\tCPU Usage: %f\n", processMetric.getCpuUsage());
                    System.err.printf("\tPhysical Memory Usage: %d bytes\n",
                            processMetric.getPhysicalMemoryUsage());
                    System.err.printf("\tVirtual Memory Usage: %d bytes\n",
                            processMetric.getVirtualMemoryUsage());
                    System.err.println();
                }
            }

        } catch (Exception e) {
            fail("An exception was thrown while getting the process data.\n\n"
                    + e.getMessage() + "\n"
                    + Arrays.toString(e.getStackTrace()));
        }



    }

    boolean testSystemMonitorManager() {
        System.err.println("Testing System Monitor Manager...");


        try {
            System.err.println("Testing CPU Metrics Retrieval...");
            SystemCpuMetric cpuMetrics = monitor.getCpuMetrics();
            if (cpuMetrics != null) {
                System.err.printf("CPU Information: \n\t%s\n\t\tUsage: %.1f%%\n",
                        cpuMetrics.getDeviceName(), cpuMetrics.getAverageUtilization());
            }
            else {
                System.err.println("CPU Information: \n\tNo CPU Metrics Found");
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to retrieve CPU Metrics.");
            return false;
        }

        // GPU support was cut
        try {
            System.err.println("Testing GPU Metrics Retrieval...");
            SystemGpuMetric gpuMetrics = monitor.getGpuMetrics();
            if (gpuMetrics != null) {
                System.err.printf("GPU Information: \n\t%s\n\t\tUsage: %.1f%%\n",
                        gpuMetrics.getDeviceName(), gpuMetrics.getUsage());
            }
            else {
                System.err.println("GPU Information: \n\tNo GPU Metrics Found");
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to retrieve GPU Metrics.");
            return false;
        }

        try {
            System.err.println("Testing Memory Hardware Data Retrieval...");
            MemoryHardwareInformation memoryHardwareInformation = monitor.getMemoryHardwareInformation();
            if (memoryHardwareInformation != null) {
                // Calculate the actual virtual memory use from the amount of committed memory used.

                System.err.println("Memory Information:");
                System.err.printf("\tTotal Memory (Physical Memory): %d bytes\n",
                        memoryHardwareInformation.getCapacity());
                System.err.printf("\tSpeed: %d MT/s\n",
                        memoryHardwareInformation.getSpeed());
                System.err.printf("\tSlots Used: %d of %d\n",
                        memoryHardwareInformation.getSlotsUsed(),
                        memoryHardwareInformation.getSlotsTotal());
            }
            else {
                System.err.println("Memory Hardware: \n\tNo Memory Hardware Found");
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to retrieve Memory Hardware Information.");
            return false;
        }

        try {
            System.err.println("Testing Memory Metrics Retrieval...");
            SystemMemoryMetric memoryMetrics = monitor.getMemoryMetrics();
            if (memoryMetrics != null) {
                System.err.println("Memory Information:");
                System.err.printf("\tPhysical Memory Used : %s bytes\n",
                        memoryMetrics.getAveragePhysicalUtilization());
                System.err.printf("\tVirtual Memory Used: ~%.0f bytes\n",
                        memoryMetrics.getAverageVirtualUtilization());
            }
            else {
                System.err.println("Memory Information: \n\tNo Memory Metrics Found");
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to retrieve Memory Metrics.");
            return false;
        }

        try {
            System.err.println("Testing Disk Hardware Data Retrieval...");
            ArrayList<DiskHardwareInformation> diskHardwareInformationList = monitor.getDiskHardwareInformation();
            if (diskHardwareInformationList != null) {
                // Calculate the actual virtual memory use from the amount of committed memory used.
                System.err.println("Disk Information:");
                if (diskHardwareInformationList.isEmpty()) {
                    System.err.println("\tNo Disk Hardware Found");
                }
                else {
                    for (DiskHardwareInformation diskHardwareInformation : diskHardwareInformationList) {
                        System.err.printf("\t%s (Drive #: %d)\n", diskHardwareInformation.getFriendlyName(),
                                                                    diskHardwareInformation.getDiskNumber());
                        System.err.printf("\t\tUnique ID: %s\n", diskHardwareInformation.getUniqueId());
                        System.err.printf("\t\tCapacity: %s bytes\n",
                                            diskHardwareInformation.getCapacityAsUnsignedString());
                        System.err.printf("\t\tMedia Type: %s\n",
                                DiskHardwareInformation.getMediaTypeString(diskHardwareInformation.getMediaType()));
                        System.err.print("\t\tPartitions: ");
                        ArrayList<String> partitions = diskHardwareInformation.getPartitions();
                        int numPartitions = partitions.size();
                        if (numPartitions == 0) {
                            System.err.println("No Partitions");
                        }
                        else {
                            for (int partitionNum = 0; partitionNum < numPartitions; partitionNum++) {
                                System.err.printf("%s", partitions.get(partitionNum));
                                if (partitionNum != numPartitions - 1) {
                                    System.err.print("; ");
                                }
                                else {
                                    System.err.println();
                                }
                            }
                        }

                        System.err.println();
                    }
                }
            }
            else {
                System.err.println("Disk Hardware: \n\tDisk Hardware Could Not Be Identified");
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to retrieve Memory Hardware Information.");
            return false;
        }

        try {
            System.err.println("Testing Disk Metrics Retrieval...");
            ArrayList<SystemDiskMetric> diskMetrics = monitor.getDiskMetrics();
            if (diskMetrics != null) {

                System.err.println("Disk Information:");
                System.err.printf("\tDisk Instances Present (including _Total): %d\n", diskMetrics.size());

                for (SystemDiskMetric diskMetric : diskMetrics) {
                    // Skip total system disk use
                    if (diskMetric.getDeviceName().compareTo("_Total") == 0) {
                        continue;
                    }

                    System.err.printf("\t%s\n", diskMetric.getDeviceName());
                    System.err.printf("\t\tUsage: %f%%\n", diskMetric.getAvgUtilization());
                    System.err.printf("\t\tRead Bandwidth: %s bytes/sec\n", diskMetric.getAvgReadBandwidth());
                    System.err.printf("\t\tWrite Bandwidth: %s bytes/sec\n", diskMetric.getAvgWriteBandwidth());
                    System.err.printf("\t\tAverage Transfer Rate: %f sec/transfer\n", diskMetric.getAvgTimeToTransfer());
                }
            }
            else {
                System.err.println("Disk Information: \n\tNo Disk Metrics Found");
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to retrieve Disk Metrics.");
            return false;
        }

        try {
            System.err.println("Testing NIC Hardware Data Retrieval...");
            ArrayList<NicHardwareInformation> nicHardwareInformationList = monitor.getNicHardwareInformation();
            if (nicHardwareInformationList != null) {
                // Calculate the actual virtual memory use from the amount of committed memory used.
                System.err.println("NIC Information:");
                if (nicHardwareInformationList.isEmpty()) {
                    System.err.println("\tNo NIC Hardware Found");
                }
                else {
                    for (NicHardwareInformation nicHardwareInformation : nicHardwareInformationList) {
                        System.err.printf("\t%s (%s)\n", nicHardwareInformation.getFriendlyName(),
                                nicHardwareInformation.getLabel());
                        System.err.printf("\t\tUnique ID: %s\n", nicHardwareInformation.getUniqueId());
                        System.err.printf("\t\tInterface Type: %s\n",
                                NicHardwareInformation.getNicTypeString(nicHardwareInformation.getNicType()));
                        System.err.println();
                    }
                }
            }
            else {
                System.err.println("Disk Hardware: \n\tDisk Hardware Could Not Be Identified");
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to retrieve Memory Hardware Information.");
            return false;
        }

        try {
            System.err.println("Testing NIC Metrics Retrieval...");
            ArrayList<SystemNicMetric> nicMetrics = monitor.getNicMetrics();
            if (nicMetrics != null) {

                System.err.println("NIC Information:");
                System.err.printf("\tNIC Instances Present (including _Total): %d\n", nicMetrics.size());

                for (SystemNicMetric nicMetric : nicMetrics) {
                    // Skip total system NIC use
                    if (nicMetric.getDeviceName().compareTo("_Total") == 0) {
                        continue;
                    }

                    System.err.printf("\t%s\n", nicMetric.getDeviceName());
                    System.err.printf("\t\tCurrent Operation Bandwidth: %s bps\n", nicMetric.getNicBandwidthUnsigned());
                    System.err.printf("\t\tUpload Bandwidth: %s bits/sec\n", nicMetric.getBytesSentUnsigned());
                    System.err.printf("\t\tDownload Bandwidth: %s bits/sec\n", nicMetric.getBytesReceivedUnsigned());
                }
            }
            else {
                System.err.println("NIC Information: \n\tNo NIC Metrics Found");
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to retrieve NIC Metrics.");
            return false;
        }

        System.err.println("Testing complete.");

        return true;
    }

    @AfterAll
    public static void testSystemMonitorManagerClose()
    {
        try {
            System.err.println("Closing System Monitor Manager... ");
            monitor.close();

            if (monitor.isOpen()) {
                fail("The status of the System Monitor Manager did not change to closed when it was closed.");
            }
        }
        catch (Exception e) {
            fail("An exception was thrown while closing the System Monitor Manager.\n\n"
                    + e.getMessage() + "\n"
                    + Arrays.toString(e.getStackTrace()));
        }
    }

}
