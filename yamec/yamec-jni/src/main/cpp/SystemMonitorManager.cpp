// SystemMonitorManager.cpp
#include "SystemMonitorManager.h"

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
    if (!m_diskInfo.initialize(&m_pdhManager))
    {
        return -6;
    }

    // Initialize the NIC info
    if (!m_nicInfo.initialize(&m_pdhManager))
    {
        return -7;
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

unsigned long long SystemMonitorManager::getPhysicalMemory()
{
    return m_memoryInfo.getPhysicalMemory();
}
