//
// Created by cmarc on 3/23/2025.
//

#ifndef MEMORYINFO_H
#define MEMORYINFO_H

#ifdef _WIN32
    #ifdef BUILDING_DLL
        #define YAMEC_API __declspec(dllexport)
    #else
        #define YAMEC_API __declspec(dllimport)
    #endif
#else
    #define YAMEC_API
#endif

// MemoryInfo.h
#include "PdhQueryManager.h"
#include <windows.h>

#include "WmiQueryManager.h"

class YAMEC_API MemoryInfo
{
public:
    MemoryInfo();

    ~MemoryInfo();

    [[nodiscard]] bool initialize(PdhQueryManager *pdhManager,
                                    WmiQueryManager *wmiManager);

    [[nodiscard]] static unsigned long long getPhysicalMemory();

    [[nodiscard]] bool getMemoryStatus(MEMORYSTATUSEX *memStatus);

    /**
     * Retrieves system information pertaining to the physical memory setup of
     * the system
     *
     * @param speed A buffer variable for the operating speed of the memory in
     * bits per second
     * @param formfactor A buffer variable for the formfactor of memory used
     * @param capacity A buffer variable for the total amount of physical memory in bytes
     * @param slotsUsed A buffer variable for the number of physical memory slots used
     * @param slotsTotal A buffer variable for the number of physical memory slots present, used or unused
     * @return A WMI status code indicating whether the query was successful or not
     */
    [[nodiscard]] int getMemoryInformation(unsigned long long *speed,
                                           char *formfactor,
                                           unsigned long long *capacity,
                                           unsigned int *slotsUsed,
                                           unsigned int *slotsTotal) const;

    [[nodiscard]] int getAllCounters(unsigned long long *physicalMemoryAvailable,
                                     unsigned long long *virtualMemoryCommitted,
                                     double *virtualMemoryCommittedPercentUsed) const;

    [[nodiscard]] int getPhysicalMemoryAvailable(unsigned long long *physicalMemoryAvailable) const;

    [[nodiscard]] int getVirtualMemoryCommitted(unsigned long long *virtualMemoryCommitted) const;

    [[nodiscard]] int getVirtualMemoryCommittedPercentUsed(double *virtualMemoryCommittedPercentUsed) const;

    private:
    PDH_HCOUNTER m_physicalMemoryAvailableCounter{};
    PDH_HCOUNTER m_virtualMemoryCommittedCounter{};
    PDH_HCOUNTER m_virtualMemoryCommittedPercentUsedCounter{};
    PdhQueryManager *m_pdhManager;
    WmiQueryManager *m_wmiManager;

};

#endif //MEMORYINFO_H
