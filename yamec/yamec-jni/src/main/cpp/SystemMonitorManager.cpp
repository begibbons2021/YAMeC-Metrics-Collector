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
