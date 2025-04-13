// yamec/yamec-jni/src/main/cpp/main.cpp
#include "SystemMonitorManager.h"
#include <iostream>
#include <iomanip>

int main()
{
    std::cout << "==== System Metrics Test ====" << std::endl;

    // Initialize the system monitor
    SystemMonitorManager monitor;
    if (!monitor.initialize())
    {
        std::cerr << "Failed to initialize system monitor" << std::endl;
        return 1;
    }

    std::cout << "\n--- CPU Information ---" << std::endl;
    std::cout << "CPU Brand: " << CpuInfo::getBrandString() << std::endl;

    auto [numberOfProcessors, architecture] = CpuInfo::getSystemInfo();
    std::cout << "Number of processors: " << numberOfProcessors << std::endl;
    std::cout << "Architecture: " << architecture << std::endl;

    std::cout << "System uptime: " << CpuInfo::getSystemUptime() << " ms" << std::endl;
    std::cout << "Virtualization: " << (CpuInfo::isVirtualizationAvailable()
                                            ? "Available"
                                            : "Not available") << std::endl;

    std::cout << "\n--- Memory Information ---" << std::endl;
    std::cout << "Physical memory: " << monitor.getPhysicalMemory() / (1024.0 * 1024) << " GB" << std::endl;

    MEMORYSTATUSEX memStatus = {0};
    memStatus.dwLength = sizeof(MEMORYSTATUSEX);
    if (monitor.getMemoryInfo()->getMemoryStatus(&memStatus))
    {
        std::cout << "Memory load: " << memStatus.dwMemoryLoad << "%" << std::endl;
        std::cout << "Available physical memory: " << memStatus.ullAvailPhys / (1024.0 * 1024 * 1024) << " GB" <<
                std::endl;
    }

    std::cout << "\n--- Cache Information ---" << std::endl;
    CacheInfo cacheInfo = CpuInfo::getCacheInfo();
    std::cout << "Logical processors: " << cacheInfo.logicalProcessorCount << std::endl;
    std::cout << "Physical cores: " << cacheInfo.processorCoreCount << std::endl;
    std::cout << "L1 cache size: " << cacheInfo.processorL1CacheSize / 1024.0 << " KB" << std::endl;
    std::cout << "L2 cache size: " << cacheInfo.processorL2CacheSize / 1024.0 << " KB" << std::endl;
    std::cout << "L3 cache size: " << cacheInfo.processorL3CacheSize / 1024.0 << " KB" << std::endl;

    std::cout << "\n--- Performance Monitoring ---" << std::endl;

    // CPU usage (current)
    double cpuUsage = 0.0;
    if (monitor.getCpuUsage(&cpuUsage))
    {
        std::cout << "Current CPU usage: " << std::fixed << std::setprecision(2) << cpuUsage << "%" << std::endl;
    } else
    {
        std::cerr << "Failed to get CPU usage" << std::endl;
    }

    // GPU usage (if available)
    double gpuUsage = 0.0;
    if (monitor.getGpuUsage(&gpuUsage))
    {
        std::cout << "Current GPU usage: " << std::fixed << std::setprecision(2) << gpuUsage << "%" << std::endl;
    } else
    {
        std::cout << "GPU monitoring not available" << std::endl;
    }


    unsigned long long memoryPhysicalAvailable;
    unsigned long long memoryBytesCommitted;
    double memoryCommittedPercentUsed;
    if (monitor.getMemoryCounters(&memoryPhysicalAvailable, &memoryBytesCommitted, &memoryCommittedPercentUsed))
    {
        std::cout << "Physical memory available: " << memoryPhysicalAvailable << " bytes" << std::endl;
        std::cout << "Bytes committed: " << memoryBytesCommitted << " bytes" << std::endl;

        // Calculate virtual memory in use my multiplying the committed bytes by the percent actually in use
        const long long int virtualBytesUsed = ceil(memoryBytesCommitted * (memoryCommittedPercentUsed/100));

        std::cout << "Percent committed in use: " << memoryCommittedPercentUsed
                    << "% (" << std::fixed << virtualBytesUsed  << std::defaultfloat << " bytes)" << std::endl;
        std::cout << std::endl;
    }
    else
    {
        std::cerr << "Memory monitoring not available" << std::endl;
    }

    unsigned long long speed;
    unsigned long long capacity;
    unsigned int slotsUsed;
    unsigned int slotsTotal;

    if (const int hr = monitor.getHardwareMemoryInformation(&speed,
    nullptr,
    &capacity,
    &slotsUsed,
    &slotsTotal); !FAILED(hr))
    {
        std::cout << "Physical memory Speed: " << speed << " MT/s" << std::endl;
        std::cout << "Capacity: " << capacity << " bytes" <<  std::endl;
        std::cout << "Slots Used: " << slotsUsed << std::endl;
        std::cout << "Slots Total: " << slotsTotal << std::endl;
    }
    else
    {
        std::cerr << "Failed to get Hardware Memory Information." << std::endl;
        std::cerr << "Error code: " << std::hex << hr << std::endl;
    }

    // Disk usage (if available)
    std::vector<std::wstring> diskInstanceNames;

    if (const size_t diskInstances = monitor.getDiskInstances(&diskInstanceNames); diskInstances > 0)
    {
        std::cout << "Disk Instances: ";
        for (int i = 0; i < diskInstances; i++)
        {
            std::wcout << diskInstanceNames.at(i);
            if (i < diskInstances - 1)
            {
                std::cout << "; ";
            }
        }
        std::cout << std::endl;

        std::vector<double> diskInstancesUsage;
        std::vector<unsigned long long> diskInstancesReadBandwidth;
        std::vector<unsigned long long> diskInstancesWriteBandwidth;
        std::vector<double> diskInstancesAvgTimeToTransfer;
        if (monitor.getDiskCounters(&diskInstancesUsage,
                                        &diskInstancesReadBandwidth,
                                        &diskInstancesWriteBandwidth,
                                        &diskInstancesAvgTimeToTransfer))
        {
            std::cout << "Disk Usage: " << std::endl;
            for (int i = 0; i < diskInstances; i++)
            {
                std::wcout << diskInstanceNames.at(i) << L": "
                            << diskInstancesUsage.at(i) << std::endl;
            }

            std::cout << "Disk Bytes Read/sec: " << std::endl;
            for (int i = 0; i < diskInstances; i++)
            {
                std::wcout << diskInstanceNames.at(i) << L": "
                                << diskInstancesReadBandwidth.at(i) << std::endl;
            }

            std::cout << "Disk Bytes Written/sec: " << std::endl;
            for (int i = 0; i < diskInstances; i++)
            {
                std::wcout << diskInstanceNames.at(i) << L": "
                                << diskInstancesWriteBandwidth.at(i) << std::endl;
            }

            std::cout << "Disk Average Time (sec) to Transfer: " << std::endl;
            for (int i = 0; i < diskInstances; i++)
            {
                std::wcout << diskInstanceNames.at(i) << L": "
                                << diskInstancesAvgTimeToTransfer.at(i) << std::endl;
            }
        }
    }
    else
    {
        std::cout << "Disk monitoring not available" << std::endl;
    }

    // NIC usage (if available)
    std::vector<std::wstring> nicInstanceNames;
    if (const size_t nicInstances = monitor.getNicInstances(&nicInstanceNames); nicInstances > 0)
    {
        std::cout << "NIC Instances: ";
        for (int i = 0; i < nicInstances; i++)
        {
            std::wcout << nicInstanceNames.at(i);
            if (i < nicInstances - 1)
            {
                std::cout << "; ";
            }
        }
        std::cout << std::endl;
        std::vector<unsigned long long> nicInstancesBandwidth;
        std::vector<unsigned long long> nicInstancesRecvBytes;
        std::vector<unsigned long long> nicInstancesSendBytes;
        if (monitor.getNicCounters(&nicInstancesBandwidth,
                                        &nicInstancesRecvBytes,
                                        &nicInstancesSendBytes))
        {
            std::cout << "NIC Bandwidth (bps): " << std::endl;
            for (int i = 0; i < nicInstances; i++)
            {
                std::wcout << nicInstanceNames.at(i) << L": "
                                << nicInstancesBandwidth.at(i) << std::endl;
            }

            std::cout << "NIC Bytes Received/sec: " << std::endl;
            for (int i = 0; i < nicInstances; i++)
            {
                std::wcout << nicInstanceNames.at(i) << L": "
                                << nicInstancesRecvBytes.at(i) << std::endl;
            }

            std::cout << "NIC Bytes Sent/sec: " << std::endl;
            for (int i = 0; i < nicInstances; i++)
            {
                std::wcout << nicInstanceNames.at(i) << L": "
                                << nicInstancesSendBytes.at(i) << std::endl;
            }
        }
    }
    else
    {
        std::cout << "NIC monitoring not available" << std::endl;
    }


    std::cout << "\nAll tests completed successfully!" << std::endl;
    return 0;
}
