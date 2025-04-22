#include <gtest/gtest.h>
#include <yamecjni/SystemMonitorManager.h>
#include <chrono>
#include <vector>
#include <thread>

class SystemMonitorManagerTest : public ::testing::Test {
protected:
    SystemMonitorManager manager;

    void SetUp() override {
        // Initialize the manager once for all tests
        int result = manager.initialize();
        ASSERT_LE(result, 0) << "SystemMonitorManager initialization failed with error: " << result;

        // Wait 1 second and then collect new counter data
        Sleep(1000);

        const int collectCounterDataSuccess = manager.collectMetricsData();
        ASSERT_EQ(collectCounterDataSuccess, 0) << "SystemMonitorManager.collectMetricsData() should return a "
                                                << "success code of 0 (got " << collectCounterDataSuccess << ")" ;

    }
};

// Test CPU usage method
TEST_F(SystemMonitorManagerTest, GetCpuUsageTest) {
    double usage = -1.0;
    int result = manager.getCpuUsage(&usage);

    EXPECT_EQ(result, 0) << "Failed to get CPU usage";
    EXPECT_GE(usage, 0.0) << "CPU usage cannot be negative";
    EXPECT_LE(usage, 100.0) << "CPU usage cannot exceed 100%";
}

// Test GPU usage method (might not always be available)
TEST_F(SystemMonitorManagerTest, GetGpuUsageTest) {
    double usage = -1.0;
    int result = manager.getGpuUsage(&usage);

    if (result == 0) {
        EXPECT_GE(usage, 0.0) << "GPU usage cannot be negative";
        EXPECT_LE(usage, 100.0) << "GPU usage cannot exceed 100%";
    } else {
        // GPU might not be available, which is acceptable
        EXPECT_EQ(usage, -1.0) << "Usage should remain untouched on failure";
    }
}

// Test memory counters
TEST_F(SystemMonitorManagerTest, GetMemoryCountersTest) {
    unsigned long long physicalBytesAvailable = 0;
    unsigned long long virtualBytesCommitted = 0;
    double committedPercentUsed = 0.0;

    int result = manager.getMemoryCounters(&physicalBytesAvailable,
                                           &virtualBytesCommitted,
                                           &committedPercentUsed);

    EXPECT_EQ(result, 0) << "Failed to get memory counters";
    EXPECT_GT(physicalBytesAvailable, 0ULL) << "Physical bytes available should be positive";
    EXPECT_GT(virtualBytesCommitted, 0ULL) << "Virtual bytes committed should be positive";
    EXPECT_GE(committedPercentUsed, 0.0) << "Committed percent used cannot be negative";
    EXPECT_LE(committedPercentUsed, 100.0) << "Committed percent used cannot exceed 100%";
}

// Test disk instances and counters
TEST_F(SystemMonitorManagerTest, GetDiskDataTest) {
    std::vector<std::wstring> instanceNames;
    size_t numInstances = manager.getDiskInstances(&instanceNames);

    EXPECT_GT(numInstances, 0U) << "System should have at least one disk";
    EXPECT_EQ(instanceNames.size(), numInstances) << "Instance names vector size should match count";

    // Test disk counters
    std::vector<double> diskUsage;
    std::vector<unsigned long long> diskReadBandwidth;
    std::vector<unsigned long long> diskWriteBandwidth;
    std::vector<double> diskAvgTimeToTransfer;

    int result = manager.getDiskCounters(&diskUsage,
                                         &diskReadBandwidth,
                                         &diskWriteBandwidth,
                                         &diskAvgTimeToTransfer);

    EXPECT_EQ(result, 0) << "Failed to get disk counters";
    EXPECT_EQ(diskUsage.size(), numInstances) << "Disk usage vector size doesn't match instance count";

    // Validate each disk's data
    for (size_t i = 0; i < numInstances; ++i) {
        EXPECT_GE(diskUsage[i], 0.0) << "Disk usage cannot be negative";
        EXPECT_LE(diskUsage[i], 100.0) << "Disk usage cannot exceed 100%";
        EXPECT_GE(diskReadBandwidth[i], 0ULL) << "Read bandwidth cannot be negative";
        EXPECT_GE(diskWriteBandwidth[i], 0ULL) << "Write bandwidth cannot be negative";
        EXPECT_GE(diskAvgTimeToTransfer[i], 0.0) << "Average transfer time cannot be negative";
    }
}

// Test NIC instances and counters
TEST_F(SystemMonitorManagerTest, GetNicDataTest) {
    std::vector<std::wstring> instanceNames;
    size_t numInstances = manager.getNicInstances(&instanceNames);

    EXPECT_GT(numInstances, 0U) << "System should have at least one network interface";
    EXPECT_EQ(instanceNames.size(), numInstances) << "Instance names vector size should match count";

    // Test NIC counters
    std::vector<unsigned long long> nicBandwidth;
    std::vector<unsigned long long> nicSendBytes;
    std::vector<unsigned long long> nicRecvBytes;

    int result = manager.getNicCounters(&nicBandwidth, &nicSendBytes, &nicRecvBytes);

    EXPECT_EQ(result, 0) << "Failed to get NIC counters";
    EXPECT_EQ(nicBandwidth.size(), numInstances) << "NIC bandwidth vector size doesn't match instance count";

    // Validate each NIC's data
    for (size_t i = 0; i < numInstances; ++i) {
        EXPECT_GE(nicBandwidth[i], 0ULL) << "Bandwidth cannot be negative";
        EXPECT_GE(nicSendBytes[i], 0ULL) << "Send bytes cannot be negative";
        EXPECT_GE(nicRecvBytes[i], 0ULL) << "Receive bytes cannot be negative";
    }
}

// Test application counters
TEST_F(SystemMonitorManagerTest, GetApplicationCountersTest) {
    std::vector<std::wstring> processNames;
    std::vector<int> processIds;
    std::vector<double> cpuUsages;
    std::vector<long long> physicalMemoryUsed;
    std::vector<long long> virtualMemoryUsed;

    int result = manager.getApplicationCounters(&processNames,
                                                &processIds,
                                                &cpuUsages,
                                                &physicalMemoryUsed,
                                                &virtualMemoryUsed);

    EXPECT_EQ(result, 0) << "Failed to get application counters";

    // All vectors should have the same size
    size_t numProcesses = processNames.size();
    EXPECT_GT(numProcesses, 0U) << "System should have at least one process";
    EXPECT_EQ(processIds.size(), numProcesses) << "Process IDs vector size mismatch";
    EXPECT_EQ(cpuUsages.size(), numProcesses) << "CPU usages vector size mismatch";
    EXPECT_EQ(physicalMemoryUsed.size(), numProcesses) << "Physical memory vector size mismatch";
    EXPECT_EQ(virtualMemoryUsed.size(), numProcesses) << "Virtual memory vector size mismatch";

    // Validate each process's data
    for (size_t i = 0; i < numProcesses; ++i) {
        EXPECT_GE(processIds[i], 0) << "Process ID cannot be negative";
        EXPECT_GE(cpuUsages[i], 0.0) << "CPU usage cannot be negative";
        EXPECT_GE(physicalMemoryUsed[i], 0LL) << "Physical memory used cannot be negative";
        EXPECT_GE(virtualMemoryUsed[i], 0LL) << "Virtual memory used cannot be negative";
    }
}

// Test hardware information methods
TEST_F(SystemMonitorManagerTest, GetHardwareCpuInformationTest) {
    std::wstring brandString;
    unsigned int numCores = 0;
    unsigned int numLogicalProcessors = 0;
    std::wstring architecture;
    unsigned int numNumaNodes = 0;
    unsigned int l1CacheSize = 0;
    unsigned int l2CacheSize = 0;
    unsigned int l3CacheSize = 0;
    bool supportsVirtualization = false;

    int result = manager.getHardwareCpuInformation(&brandString,
                                                   &numCores,
                                                   &numLogicalProcessors,
                                                   &architecture,
                                                   &numNumaNodes,
                                                   &l1CacheSize,
                                                   &l2CacheSize,
                                                   &l3CacheSize,
                                                   &supportsVirtualization);

    EXPECT_EQ(result, 0) << "Failed to get CPU hardware information";
    EXPECT_FALSE(brandString.empty()) << "CPU brand string should not be empty";
    EXPECT_GT(numCores, 0U) << "Number of cores should be positive";
    EXPECT_GE(numLogicalProcessors, numCores) << "Logical processors should be at least as many as cores";
    EXPECT_FALSE(architecture.empty()) << "Architecture string should not be empty";
    EXPECT_GT(numNumaNodes, 0U) << "Number of NUMA nodes should be positive";
    EXPECT_GT(l1CacheSize, 0U) << "L1 cache size should be positive";
    // L2 and L3 might be 0 on some systems
}

// Test memory hardware information
TEST_F(SystemMonitorManagerTest, GetHardwareMemoryInformationTest) {
    unsigned long long speed = 0;
    unsigned long long capacity = 0;
    unsigned int slotsUsed = 0;
    unsigned int slotsTotal = 0;

    int result = manager.getHardwareMemoryInformation(&speed,
                                                      &capacity,
                                                      &slotsUsed,
                                                      &slotsTotal);

    EXPECT_EQ(result, 0) << "Failed to get memory hardware information";
    EXPECT_GT(speed, 0ULL) << "Memory speed should be positive";
    EXPECT_GT(capacity, 0ULL) << "Memory capacity should be positive";
    EXPECT_GT(slotsUsed, 0U) << "Used slots should be positive";
    EXPECT_GE(slotsTotal, slotsUsed) << "Total slots should be at least as many as used slots";
}

// Test error handling with null pointers
TEST_F(SystemMonitorManagerTest, NullPointerHandlingTest) {
    EXPECT_EQ(manager.getCpuUsage(nullptr), -1) << "Should handle null pointer for CPU usage";
    EXPECT_EQ(manager.getGpuUsage(nullptr), -1) << "Should handle null pointer for GPU usage";
    EXPECT_EQ(manager.getPhysicalMemoryAvailable(nullptr), -1) << "Should handle null pointer for physical memory";

    // Test getMemoryCounters with various null combinations
    unsigned long long dummyLong = 0;
    double dummyDouble = 0.0;
    EXPECT_EQ(manager.getMemoryCounters(nullptr, &dummyLong, &dummyDouble), false);
    EXPECT_EQ(manager.getMemoryCounters(&dummyLong, nullptr, &dummyDouble), false);
    EXPECT_EQ(manager.getMemoryCounters(&dummyLong, &dummyLong, nullptr), false);
}

// Performance test - ensure methods don't take too long
TEST_F(SystemMonitorManagerTest, PerformanceTest) {
    auto start = std::chrono::high_resolution_clock::now();

    double cpuUsage;
    manager.getCpuUsage(&cpuUsage);

    auto end = std::chrono::high_resolution_clock::now();
    auto duration = std::chrono::duration_cast<std::chrono::milliseconds>(end - start);

    EXPECT_LT(duration.count(), 1000) << "Getting CPU usage should take less than 1 second";
}