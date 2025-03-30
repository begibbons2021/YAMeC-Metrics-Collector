//
// Created by Brendan on 3/27/2025.
//

#ifndef DISKINFO_H
#define DISKINFO_H

#include <vector>

#include "PdhQueryManager.h"
#include <windows.h>

class DiskInfo
{
    public:
    DiskInfo();

    ~DiskInfo();

    [[nodiscard]] bool initialize(PdhQueryManager *pdhManager);

    [[nodiscard]] size_t getInstanceNames(std::vector<std::wstring> *list) const;

    // [[nodiscard]] static unsigned long long getPhysicalMemory();
    //
    // [[nodiscard]] bool getMemoryStatus(MEMORYSTATUSEX *memStatus);
    //
    // [[nodiscard]] bool getAllCounters(unsigned long long *physicalMemoryAvailable,
    //                                     unsigned long long *virtualMemoryCommitted,
    //                                     double *virtualMemoryCommittedPercentUsed) const;
    //
    // [[nodiscard]] bool getPhysicalMemoryAvailable(unsigned long long *physicalMemoryAvailable) const;
    //
    // [[nodiscard]] bool getVirtualMemoryCommitted(unsigned long long *virtualMemoryCommitted) const;
    //
    // [[nodiscard]] bool getVirtualMemoryCommittedPercentUsed(double *virtualMemoryCommittedPercentUsed) const;

    private:
    size_t num_disks = 0;
    std::vector<std::wstring> disk_instance_names;

    std::vector<PDH_HCOUNTER> m_diskReadBandwidthCounters;
    std::vector<PDH_HCOUNTER> m_diskWriteBandwidthCounters;
    std::vector<PDH_HCOUNTER> m_diskUsagePercentCounters;
    std::vector<PDH_HCOUNTER> m_diskTimeToTransferCounters;

    PdhQueryManager *m_pdhManager;

    [[nodiscard]] size_t initInstances();

};

#endif // DISKINFO_H