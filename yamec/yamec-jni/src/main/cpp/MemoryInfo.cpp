//
// Created by cmarc on 3/23/2025.
//

// MemoryInfo.cpp
#include "MemoryInfo.h"
#include <stdexcept>
#include <iostream>

MemoryInfo::MemoryInfo() : m_pdhManager(nullptr) {}
MemoryInfo::~MemoryInfo() = default;

bool MemoryInfo::initialize(PdhQueryManager *pdhManager)
{
    if (!pdhManager)
    {
        std::cerr << "Invalid PDH manager" << std::endl;
        return false;
    }

    m_pdhManager = pdhManager;

    // Add Available (physical) Bytes Counter
    if (!m_pdhManager->addCounter(TEXT("\\Memory\\Available Bytes"), &m_physicalMemoryAvailableCounter))
    {
        std::cerr << "Failed to add Memory Available Bytes counter" << std::endl;
        return false;
    }

    // Add Committed (Reserved Virtual Memory) Bytes Counter
    if (!m_pdhManager->addCounter(TEXT("\\Memory\\Committed Bytes"), &m_virtualMemoryCommittedCounter))
    {
        std::cerr << "Failed to add Memory Committed Bytes counter" << std::endl;
        return false;
    }

    // Add % Committed (virtual) Bytes In Use Counter
    if (!m_pdhManager->addCounter(TEXT("\\Memory\\% Committed Bytes In Use"), &m_virtualMemoryCommittedPercentUsedCounter))
    {
        std::cerr << "Failed to add Memory % Committed Bytes In Use counter" << std::endl;
        return false;
    }

    return true;
}

bool MemoryInfo::getAllCounters(unsigned long long *physicalMemoryAvailable,
                                        unsigned long long *virtualMemoryCommitted,
                                        double *virtualMemoryCommittedPercentUsed) const
{
    if (!m_pdhManager)
    {
        std::cerr << "PDH manager not initialized" << std::endl;
        return false;
    }

    // Collect data twice for accurate readings
    if (!m_pdhManager->collectData())
    {
        return false;
    }

    Sleep(500); // Wait for 500ms

    if (!m_pdhManager->collectData())
    {
        return false;
    }

    if (!m_pdhManager->getCounterValue(m_physicalMemoryAvailableCounter, physicalMemoryAvailable))
    {
        return false;
    }

    if (!m_pdhManager->getCounterValue(m_virtualMemoryCommittedCounter, virtualMemoryCommitted))
    {
        return false;
    }

    if (!m_pdhManager->getCounterValue(m_virtualMemoryCommittedPercentUsedCounter, virtualMemoryCommittedPercentUsed))
    {
        return false;
    }

    return true;

}

bool MemoryInfo::getPhysicalMemoryAvailable(unsigned long long *physicalMemoryAvailable) const
{
    if (!m_pdhManager)
    {
        std::cerr << "PDH manager not initialized" << std::endl;
        return false;
    }

    // Collect data twice for accurate readings
    if (!m_pdhManager->collectData())
    {
        return false;
    }

    Sleep(500); // Wait for 500ms

    if (!m_pdhManager->collectData())
    {
        return false;
    }

    if (!m_pdhManager->getCounterValue(m_physicalMemoryAvailableCounter, physicalMemoryAvailable))
    {
        return false;
    }

    return true;
}

bool MemoryInfo::getVirtualMemoryCommitted(unsigned long long *virtualMemoryCommitted) const
{
    if (!m_pdhManager)
    {
        std::cerr << "PDH manager not initialized" << std::endl;
        return false;
    }

    // Collect data twice for accurate readings
    if (!m_pdhManager->collectData())
    {
        return false;
    }

    Sleep(500); // Wait for 500ms

    if (!m_pdhManager->collectData())
    {
        return false;
    }

    if (!m_pdhManager->getCounterValue(m_virtualMemoryCommittedCounter, virtualMemoryCommitted))
    {
        return false;
    }

    return true;
}

bool MemoryInfo::getVirtualMemoryCommittedPercentUsed(double *virtualMemoryCommittedPercentUsed) const
{
    if (!m_pdhManager)
    {
        std::cerr << "PDH manager not initialized" << std::endl;
        return false;
    }

    // Collect data twice for accurate readings
    if (!m_pdhManager->collectData())
    {
        return false;
    }

    Sleep(500); // Wait for 500ms

    if (!m_pdhManager->collectData())
    {
        return false;
    }

    if (!m_pdhManager->getCounterValue(m_virtualMemoryCommittedPercentUsedCounter, virtualMemoryCommittedPercentUsed))
    {
        return false;
    }

    return true;
}

unsigned long long MemoryInfo::getPhysicalMemory()
{
    ULONGLONG memory;

    if (!GetPhysicallyInstalledSystemMemory(&memory))
    {
        throw std::runtime_error("Unable to get RAM information.");
    }

    return memory;
}

bool MemoryInfo::getMemoryStatus(MEMORYSTATUSEX *memStatus)
{
    memStatus->dwLength = sizeof(MEMORYSTATUSEX);
    return GlobalMemoryStatusEx(memStatus);
}


