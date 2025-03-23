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
    if (MemoryInfo::getMemoryStatus(&memStatus))
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

    std::cout << "\nAll tests completed successfully!" << std::endl;
    return 0;
}
