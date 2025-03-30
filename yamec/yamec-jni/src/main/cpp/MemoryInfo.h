//
// Created by cmarc on 3/23/2025.
//

#ifndef MEMORYINFO_H
#define MEMORYINFO_H

// MemoryInfo.h
#include "PdhQueryManager.h"
#include <windows.h>

class MemoryInfo
{
public:
    MemoryInfo();

    ~MemoryInfo();

    [[nodiscard]] bool initialize(PdhQueryManager *pdhManager);

    [[nodiscard]] static unsigned long long getPhysicalMemory();

    [[nodiscard]] bool getMemoryStatus(MEMORYSTATUSEX *memStatus);

    [[nodiscard]] bool getAllCounters(unsigned long long *physicalMemoryAvailable,
                                        unsigned long long *virtualMemoryCommitted,
                                        double *virtualMemoryCommittedPercentUsed) const;

    [[nodiscard]] bool getPhysicalMemoryAvailable(unsigned long long *physicalMemoryAvailable) const;

    [[nodiscard]] bool getVirtualMemoryCommitted(unsigned long long *virtualMemoryCommitted) const;

    [[nodiscard]] bool getVirtualMemoryCommittedPercentUsed(double *virtualMemoryCommittedPercentUsed) const;

    private:
    PDH_HCOUNTER m_physicalMemoryAvailableCounter{};
    PDH_HCOUNTER m_virtualMemoryCommittedCounter{};
    PDH_HCOUNTER m_virtualMemoryCommittedPercentUsedCounter{};
    PdhQueryManager *m_pdhManager;

};

#endif //MEMORYINFO_H
