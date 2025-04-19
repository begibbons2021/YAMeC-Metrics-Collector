// SystemMonitorManager.cpp
#include "SystemMonitorManager.h"

#include <sstream>

SystemMonitorManager::SystemMonitorManager() : m_initialized(false) {}

SystemMonitorManager::~SystemMonitorManager() = default;

int SystemMonitorManager::initialize()
{
    if (m_initialized)
    {
        return 0;
    }

    // Initialize the PDH query manager
    if (!m_pdhManager.initialize())
    {
        return -1;
    }

    if (!m_wmiManager.initialize())
    {
        return -2;
    }

    // Initialize the CPU info
    if (!m_cpuInfo.initialize(&m_pdhManager))
    {
        return -3;
    }

    // Initialize the GPU info (this may fail if no compatible GPU is present)
    if (!m_gpuInfo.initialize(&m_pdhManager))
    {
        return -4;
    }

    // Initialize the memory info
    if (!m_memoryInfo.initialize(&m_pdhManager, &m_wmiManager))
    {
        return -5;
    }

    // Initialize the disk info
    if (!m_diskInfo.initialize(&m_pdhManager, &m_wmiManager))
    {
        return -6;
    }

    // Initialize the NIC info
    if (!m_nicInfo.initialize(&m_pdhManager, &m_wmiManager))
    {
        return -7;
    }

    // Initialize the Application info module
    if (const int applicationInfoInitialized = m_applicationInfo.initialize(&m_pdhManager, &m_wmiManager);
        applicationInfoInitialized != 0)
    {
        return -8*10 + applicationInfoInitialized;
    }

    m_initialized = true;
    return 0;
}

int SystemMonitorManager::getCpuUsage(double *usage) const
{
    if (!m_initialized || !usage)
    {
        return -1;
    }

    return m_cpuInfo.getUsage(usage);
}

int SystemMonitorManager::getGpuUsage(double *usage) const
{
    if (!m_initialized || !usage)
    {
        return -1;
    }

    return m_gpuInfo.getUsage(usage);
}

/**
* Retrieves all memory-related performance counters.
*/
int SystemMonitorManager::getMemoryCounters(unsigned long long *physicalBytesAvailable,
                                            unsigned long long *virtualBytesCommitted,
                                            double *committedPercentUsed) const
{
    if (!m_initialized
      || !physicalBytesAvailable
      || !virtualBytesCommitted
      || !committedPercentUsed)
    {
        return false;
    }

    return m_memoryInfo.getAllCounters(physicalBytesAvailable, virtualBytesCommitted, committedPercentUsed);
}

size_t SystemMonitorManager::getDiskInstances(std::vector<std::wstring> *instanceNames) const
{
    if (!m_initialized)
    {
        return 0;
    }

    return m_diskInfo.getInstanceNames(instanceNames);
}

int SystemMonitorManager::getDiskCounters(std::vector<double> *diskInstancesUsage,
                                          std::vector<unsigned long long> *diskInstancesReadBandwidth,
                                          std::vector<unsigned long long> *diskInstancesWriteBandwidth,
                                          std::vector<double> *diskInstancesAvgTimeToTransfer) const
{
    if (!m_initialized
        || !diskInstancesUsage
        || !diskInstancesReadBandwidth
        || !diskInstancesWriteBandwidth
        || !diskInstancesAvgTimeToTransfer)
    {
        return -1;
    }

    return m_diskInfo.getAllCounters(diskInstancesUsage,
                                        diskInstancesReadBandwidth,
                                        diskInstancesWriteBandwidth,
                                        diskInstancesAvgTimeToTransfer);
}

size_t SystemMonitorManager::getNicInstances(std::vector<std::wstring> *instanceNames) const
{
    if (!m_initialized)
    {
        return 0;
    }

    return m_nicInfo.getInstanceNames(instanceNames);
}


int SystemMonitorManager::getNicCounters(std::vector<unsigned long long> *nicInstancesBandwidth,
                                         std::vector<unsigned long long> *nicInstancesSendBytes,
                                         std::vector<unsigned long long> *nicInstancesRecvBytes) const
{
    if (!m_initialized
        || !nicInstancesBandwidth
        || !nicInstancesSendBytes
        || !nicInstancesRecvBytes)
    {
        return -1;
    }

    return m_nicInfo.getAllCounters(nicInstancesBandwidth, nicInstancesSendBytes, nicInstancesRecvBytes);


}

int SystemMonitorManager::getApplicationCounters(std::vector<std::wstring> *processNames,
                                         std::vector<int> *processIds,
                                         std::vector<double> *cpuUsages,
                                         std::vector<long long> *physicalMemoryUsed,
                                         std::vector<long long> *virtualMemoryUsed) const
{
    if (!m_initialized)
    {
        return -1;
    }

    const int status = m_applicationInfo.getProcessCounters(processNames,
                                                            processIds,
                                                            cpuUsages,
                                                            physicalMemoryUsed,
                                                            virtualMemoryUsed);

    // System Monitor Manager will scale the CPU usages down to match the number of
    // processors on the system
    if (status == 0)
    {
        if (const unsigned int numLogicalProcessors = m_cpuInfo.getSystemInfo().numberOfProcessors;
            numLogicalProcessors != 0)
        {
            // Oh right, we can address these as references!
            for (double & cpuUsage : *cpuUsages)
            {
                // Divide storage
                cpuUsage = cpuUsage / static_cast<double>(numLogicalProcessors);
            }
        }
    }

    return status;

}

int SystemMonitorManager::getPhysicalMemoryAvailable(unsigned long long *bytesAvailable) const
{
    if (!m_initialized || !bytesAvailable)
    {
        return -1;
    }

    return m_memoryInfo.getPhysicalMemoryAvailable(bytesAvailable);
}

int SystemMonitorManager::getVirtualMemoryCommitted(unsigned long long *bytesCommitted) const
{
    if (!m_initialized || !bytesCommitted)
    {
        return -1;
    }

    return m_memoryInfo.getVirtualMemoryCommitted(bytesCommitted);
}

int SystemMonitorManager::getVirtualMemoryCommittedPercentUsed(double *committedPercentUsed) const
{
    if (!m_initialized || !committedPercentUsed)
    {
        return -1;
    }

    return m_memoryInfo.getVirtualMemoryCommittedPercentUsed(committedPercentUsed);
}

int SystemMonitorManager::getHardwareCpuInformation(std::wstring *brandString,
                                                    unsigned int *numCores,
                                                    unsigned int *numLogicalProcessors,
                                                    std::wstring *architecture,
                                                    unsigned int *numNumaNodes,
                                                    unsigned int *l1CacheSize,
                                                    unsigned int *l2CacheSize,
                                                    unsigned int *l3CacheSize,
                                                    bool *supportsVirtualization) const
{
    // Check if the Monitor is initialized
    if (!m_initialized)
    {
        return -1;
    }

    const std::string brandStringAsBSTR = m_cpuInfo.getBrandString();
    std::wostringstream converter;

    converter << brandStringAsBSTR.c_str();

    if (brandString != nullptr)
    {
        brandString->clear();
        brandString->append(converter.str());
    }

    converter.clear();

    const auto [numberOfProcessorsTemp, architectureTemp] = m_cpuInfo.getSystemInfo();

    const CacheInfo cacheInfo = m_cpuInfo.getCacheInfo();

    const int architectureAsInt = architectureTemp;
    if (architecture != nullptr)
    {
        architecture->clear();
        switch (architectureAsInt)
        {
            case 0:
                architecture->append(L"x86");
                break;
            case 5:
                architecture->append(L"ARM");
                break;
            case 6:
                architecture->append(L"Intel Itanium-based");
                break;
            case 9:
                architecture->append(L"x64");
                break;
            case 12:
                architecture->append(L"ARM64");
                break;
            default:
                architecture->append(L"Unknown");
                break;
        }
    }

    if (numCores != nullptr)
    {
        // Set dereferenced core count
        *numCores = cacheInfo.processorCoreCount;
    }

    if (numLogicalProcessors != nullptr)
    {
        // Set dereferenced core count
        *numCores = cacheInfo.logicalProcessorCount;
    }

    if (numNumaNodes != nullptr)
    {
        // Set dereferenced core count
        *numNumaNodes = cacheInfo.numaNodeCount;
    }

    if (l1CacheSize != nullptr)
    {
        // Set dereferenced core count
        *l1CacheSize = cacheInfo.processorL1CacheSize;
    }

    if (l2CacheSize != nullptr)
    {
        // Set dereferenced core count
        *l2CacheSize = cacheInfo.processorL2CacheSize;
    }

    if (l3CacheSize != nullptr)
    {
        // Set dereferenced core count
        *l3CacheSize = cacheInfo.processorL3CacheSize;
    }

    if (supportsVirtualization != nullptr)
    {
        // Set dereferenced core count
        *supportsVirtualization = m_cpuInfo.isVirtualizationAvailable();
    }

    return 0;


}

int SystemMonitorManager::getHardwareMemoryInformation(unsigned long long *speed,
                                                        unsigned long long *capacity,
                                                        unsigned int *slotsUsed,
                                                        unsigned int *slotsTotal) const
{
    if (!m_initialized
        || !speed
        || !capacity
        || !slotsUsed
        || !slotsTotal)
    {
        // Passed in pointers which aren't valid or Monitor isn't initialized
        return -1;
    }

    return m_memoryInfo.getMemoryInformation(speed,
                                                nullptr,
                                                capacity,
                                                slotsUsed,
                                                slotsTotal);
}

int SystemMonitorManager::getHardwareDiskInformation(std::vector<std::wstring> *hardwareNames,
                                                     std::vector<std::wstring> *uniqueIds,
                                                     std::vector<unsigned int> *mediaTypes,
                                                     std::vector<unsigned long long> *capacities,
                                                     std::vector<unsigned int> *diskNumbers,
                                                     std::map<std::wstring, unsigned int> *partitionMappings) const
{
    if (!m_initialized)
    {
        return -1;
    }

    return m_diskInfo.getDiskInformation(hardwareNames, uniqueIds,
                                         mediaTypes, capacities,
                                         diskNumbers,
                                         partitionMappings);
}

int SystemMonitorManager::getHardwareNicInformation(std::vector<std::wstring> *hardwareNames,
                                            std::vector<std::wstring> *labels,
                                            std::vector<std::wstring> *uniqueIds,
                                            std::vector<unsigned int> *nicTypes) const
{
    if (!m_initialized)
    {
        return -1;
    }

    return m_nicInfo.getNicInformation(hardwareNames, labels,
                                            uniqueIds, nicTypes);
}

unsigned long long SystemMonitorManager::getPhysicalMemory()
{
    return m_memoryInfo.getPhysicalMemory();
}
