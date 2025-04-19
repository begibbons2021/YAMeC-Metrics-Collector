//
// Created by Brendan on 4/18/2025.
//

#ifndef APPLICATIONINFO_H
#define APPLICATIONINFO_H
#include "PdhQueryManager.h"
#include "WmiQueryManager.h"

/**
 * Manages information queries for applications and processes running on the system.
 */
class ApplicationInfo {


    public:

    ApplicationInfo();
    ~ApplicationInfo();

    [[nodiscard]] int initialize(PdhQueryManager *pdhManager,
                                    WmiQueryManager *wmiManager);

    /**
     * Retrieves information pertaining to all currently executing processes/applications on
     * the system
     * @param processNames A pointer to a std::vector of wide strings to hold process names
     * @param processIds A pointer to a std::vector of integers to hold process IDs
     * @param cpuUsages A pointer to a std::vector of double precision decimals to hold
     * processes' CPU usages. Note that this returns the CPU usages over 100%, as it sums the
     * usage level on all cores together. (If you need a CPU usage relative to all cores, divide by
     * the number of logical processors)
     * @param physicalMemoryUsed A pointer to a std::vector of 64-bit long integers containing the
     * amount of physical memory in use for a process, derived from its working set size
     * @param virtualMemoryUsed A pointer to a std::vector of 64-bit long integers containing the
     * amount of virtual memory in use for a process, derived from its page file size
     * @return A status code either as part of the PDH standard or 0 for success or a negative number
     *          for a failure of some kind (as an int)
     */
    [[nodiscard]] int getProcessCounters(std::vector<std::wstring> *processNames,
                                         std::vector<int> *processIds,
                                         std::vector<double> *cpuUsages,
                                         std::vector<long long> *physicalMemoryUsed,
                                         std::vector<long long> *virtualMemoryUsed) const;

    private:
        PdhQueryManager *m_pdhManager;
        WmiQueryManager *m_wmiManager;

        PDH_HCOUNTER m_processCpuTimeCounter;
        PDH_HCOUNTER m_processPageSizeCounter;
        PDH_HCOUNTER m_processWorkingSetSizeCounter;

};



#endif //APPLICATIONINFO_H
