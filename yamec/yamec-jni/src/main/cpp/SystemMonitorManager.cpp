// SystemMonitorManager.cpp
#include "SystemMonitorManager.h"

SystemMonitorManager::SystemMonitorManager() : m_initialized(false) {}

SystemMonitorManager::~SystemMonitorManager() = default;

bool SystemMonitorManager::initialize()
{
    if (m_initialized)
    {
        return true;
    }

    // Initialize the PDH query manager
    if (!m_pdhManager.initialize())
    {
        return false;
    }

    // Initialize the CPU info
    if (!m_cpuInfo.initialize(&m_pdhManager))
    {
        return false;
    }

    // Initialize the GPU info (this may fail if no compatible GPU is present)
    if (!m_gpuInfo.initialize(&m_pdhManager))
    {
        return false;
    }

    // Initialize the memory info
    if (!m_memoryInfo.initialize(&m_pdhManager))
    {
        return false;
    }

    // Initialize the disk info
    if (!m_diskInfo.initialize(&m_pdhManager))
    {
        return false;
    }

    // Initialize the NIC info
    if (!m_nicInfo.initialize(&m_pdhManager))
    {
        return false;
    }

    m_initialized = true;
    return true;
}

bool SystemMonitorManager::getCpuUsage(double *usage) const
{
    if (!m_initialized || !usage)
    {
        return false;
    }

    return m_cpuInfo.getUsage(usage);
}

bool SystemMonitorManager::getGpuUsage(double *usage) const
{
    if (!m_initialized || !usage)
    {
        return false;
    }

    return m_gpuInfo.getUsage(usage);
}

/**
* Retrieves all memory-related performance counters.
*/
bool SystemMonitorManager::getMemoryCounters(unsigned long long *physicalBytesAvailable,
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

bool SystemMonitorManager::getDiskCounters(std::vector <double> *diskInstancesUsage,
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
        return false;
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


bool SystemMonitorManager::getNicCounters(std::vector<unsigned long long> *nicInstancesBandwidth,
                                            std::vector<unsigned long long> *nicInstancesSendBytes,
                                            std::vector<unsigned long long> *nicInstancesRecvBytes) const
{
    if (!m_initialized
        || !nicInstancesBandwidth
        || !nicInstancesSendBytes
        || !nicInstancesRecvBytes)
    {
        return false;
    }

    return m_nicInfo.getAllCounters(nicInstancesBandwidth, nicInstancesSendBytes, nicInstancesRecvBytes);


}

bool SystemMonitorManager::getPhysicalMemoryAvailable(unsigned long long *bytesAvailable) const
{
    if (!m_initialized || !bytesAvailable)
    {
        return false;
    }

    return m_memoryInfo.getPhysicalMemoryAvailable(bytesAvailable);
}

bool SystemMonitorManager::getVirtualMemoryCommitted(unsigned long long *bytesCommitted) const
{
    if (!m_initialized || !bytesCommitted)
    {
        return false;
    }

    return m_memoryInfo.getVirtualMemoryCommitted(bytesCommitted);
}

bool SystemMonitorManager::getVirtualMemoryCommittedPercentUsed(double *committedPercentUsed) const
{
    if (!m_initialized || !committedPercentUsed)
    {
        return false;
    }

    return m_memoryInfo.getVirtualMemoryCommittedPercentUsed(committedPercentUsed);
}

unsigned long long SystemMonitorManager::getPhysicalMemory()
{
    return m_memoryInfo.getPhysicalMemory();
}
