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

    [[nodiscard]] size_t getNumDisks() const;

    [[nodiscard]] size_t getInstanceNames(std::vector<std::wstring> *list) const;

    [[nodiscard]] bool getAllCounters(std::vector<double> *diskUsageValues, std::vector<unsigned long long> *diskReadBandwidthValues, std::vector<unsigned
                        long long> *diskWriteBandwidthValues, std::vector<double> *diskTimeToTransferValues) const;


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