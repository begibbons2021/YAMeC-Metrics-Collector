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
// TODO: Update to handle these as unsigned values
    if (!m_pdhManager)
    {
        std::cerr << "PDH manager not initialized" << std::endl;
        return -1;
    }

    // Collect CPU usage data
    std::vector<std::wstring> processNamesAndIds;
    std::vector<double> cpuUsagesTemp;
    if (!m_pdhManager->getCounterValues(m_processCpuTimeCounter, &processNamesAndIds, &cpuUsagesTemp))
    {
        return -3;
    }

    // Collect Physical Memory usage
    std::vector<long long> physicalMemoryUsageTemp;
    if (!m_pdhManager->getCounterValues(m_processWorkingSetSizeCounter, &processNamesAndIds, &physicalMemoryUsageTemp))
    {
        return -4;
    }

    // Collect Virtual Memory usage
    std::vector<long long> virtualMemoryUsageTemp;
    if (!m_pdhManager->getCounterValues(m_processPageSizeCounter, &processNamesAndIds, &virtualMemoryUsageTemp))
    {
        return -5;
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

    // Transfer data to the output buffers
    for (size_t i = 0; i < processNamesAndIds.size(); ++i)
    {
        // Don't return the total counters
        if (processNamesAndIds.at(i) == L"_Total")
        {
            continue;
        }

//        std::cout << "CPU usage at " << i << ": " << cpuUsagesTemp.at(i) << std::endl;

        double cpuUsage = cpuUsagesTemp.at(i);
        long long physicalMemory = physicalMemoryUsageTemp.at(i);
        long long virtualMemory = virtualMemoryUsageTemp.at(i);

        // Convert the ProcessNameAndIds value to two separate values
        // Find the point where the process name and ID are separated
        const std::wstring::size_type delimiterLoc = processNamesAndIds.at(i).find(L':');
        // This should never, ever fail since we're skipping _Total; but it probably will
        if (delimiterLoc == std::wstring::npos)
        {
            return -7;
        }

        // Separate process name and ID
        std::wstring processName = processNamesAndIds.at(i).substr(0, delimiterLoc);
        std::wstring processIdAsStr = processNamesAndIds.at(i).substr(delimiterLoc + 1);

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

