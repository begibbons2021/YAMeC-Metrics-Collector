//
// Created by Brendan on 4/18/2025.
//

#include "ApplicationInfo.h"

#include <iostream>

ApplicationInfo::ApplicationInfo() : m_pdhManager(nullptr), m_wmiManager(nullptr)
{

}

ApplicationInfo::~ApplicationInfo() = default;

int ApplicationInfo::initialize(PdhQueryManager *pdhManager, WmiQueryManager *wmiManager)
{

    if (!pdhManager)
    {
        return -1;
    }

    if (!wmiManager)
    {
        return -2;
    }

    m_pdhManager = pdhManager;
    m_wmiManager = wmiManager;

    // Add CPU Time Counter Handler
    if (!m_pdhManager->addCounter(TEXT("\\Process V2(*)\\% Processor Time"), &m_processCpuTimeCounter))
    {
        std::cerr << "Failed to add Process % Processor Time counter" << std::endl;
        return -3;
    }

    // Add Committed (Reserved Virtual Memory) Bytes Counter
    if (!m_pdhManager->addCounter(TEXT("\\Process V2(*)\\Page File Bytes"), &m_processPageSizeCounter))
    {
        std::cerr << "Failed to add Process Page File Bytes counter" << std::endl;
        return -4;
    }

    // Add % Committed (virtual) Bytes In Use Counter
    if (!m_pdhManager->addCounter(TEXT("\\Process V2(*)\\Working Set - Private"), &m_processWorkingSetSizeCounter))
    {
        std::cerr << "Failed to add Process Working Set counter" << std::endl;
        return -5;
    }

    return 0;

}

int ApplicationInfo::getProcessCounters(std::vector<std::wstring> *processNames,
                                        std::vector<int> *processIds,
                                        std::vector<double> *cpuUsages,
                                        std::vector<long long> *physicalMemoryUsed,
                                        std::vector<long long> *virtualMemoryUsed) const
{
    if (!m_pdhManager)
    {
        std::cerr << "PDH manager not initialized" << std::endl;
        return -1;
    }

    std::unordered_map<std::wstring, double> processCpuUsageMap;
    try
    {
        if (!m_pdhManager->getCounterValues(m_processCpuTimeCounter, &processCpuUsageMap))
        {
            return -3;
        }
    }
    catch (std::exception &e)
    {
        throw std::runtime_error("Application Info - CPU Usage - " + std::string(e.what()));
    }


    // Collect Physical Memory usage
    std::unordered_map<std::wstring, long long> processPhysicalMemoryMap;
    try
    {
        if (!m_pdhManager->getCounterValues(m_processWorkingSetSizeCounter, &processPhysicalMemoryMap))
        {
            return -4;
        }
    }
    catch (std::exception &e)
    {
        throw std::runtime_error("Application Info - Physical Memory Use -" + std::string(e.what()));
    }



    // Collect Virtual Memory usage
    std::unordered_map<std::wstring, int> processVirtualMemoryMap;
    try
    {
        if (!m_pdhManager->getCounterValues(m_processPageSizeCounter, &processVirtualMemoryMap))
        {
            return -5;
        }
    }
    catch (std::exception &e)
    {
        throw std::runtime_error("Application Info - Virtual Memory Use -" + std::string(e.what()));
    }

    // Clear storage buffers
    if (processNames != nullptr)
    {
        processNames->clear();
    }

    if (processIds != nullptr)
    {
        processIds->clear();
    }

    if (cpuUsages != nullptr)
    {
        cpuUsages->clear();
    }

    if (physicalMemoryUsed != nullptr)
    {
        physicalMemoryUsed->clear();
    }

    if (virtualMemoryUsed != nullptr)
    {
        virtualMemoryUsed->clear();
    }

    // Temporarily store full process names in this vector
    std::vector<std::wstring> processNamesAndIds;


    if (processCpuUsageMap.empty())
    {
        return 0;
    }

    for (const auto&[processNameAndId, usage] : processCpuUsageMap)
    {
        // Don't return the total counters
        if (processNameAndId == L"_Total")
        {
            continue;
        }

        // Get metrics for processes which have all metrics available
        if (processPhysicalMemoryMap.contains(processNameAndId)
            && processVirtualMemoryMap.contains(processNameAndId))
        {
            processNamesAndIds.emplace_back(processNameAndId);
        }
    }

    // Transfer data to the output buffers
    for (const auto &processNameAndId : processNamesAndIds)
    {
        double cpuUsage = processCpuUsageMap.at(processNameAndId);
        long long physicalMemory = processPhysicalMemoryMap.at(processNameAndId);
        long long virtualMemory = processVirtualMemoryMap.at(processNameAndId);

        // Convert the ProcessNameAndIds value to two separate values
        // Find the point where the process name and ID are separated
        const std::wstring::size_type delimiterLoc = processNameAndId.find(L':');
        // This should never, ever fail since we're skipping _Total; but it probably will
        if (delimiterLoc == std::wstring::npos)
        {
            return -7;
        }

        // Separate process name and ID
        std::wstring processName = processNameAndId.substr(0, delimiterLoc);
        std::wstring processIdAsStr = processNameAndId.substr(delimiterLoc + 1);

        // Convert the process ID to an integer
        int processIdAsInt = std::stoi(processIdAsStr);

        if (processNames != nullptr)
        {
            processNames->emplace_back(processName);
        }

        if (processIds != nullptr)
        {
            processIds->emplace_back(processIdAsInt);

        }

        if (cpuUsages != nullptr)
        {
            cpuUsages->emplace_back(cpuUsage);
        }

        if (physicalMemoryUsed != nullptr)
        {
            physicalMemoryUsed->emplace_back(physicalMemory);

        }

        if (virtualMemoryUsed != nullptr)
        {
            virtualMemoryUsed->emplace_back(virtualMemory);
        }

    }

    return 0;

}

