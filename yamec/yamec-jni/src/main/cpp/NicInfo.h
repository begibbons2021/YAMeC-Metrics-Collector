//
// Created by Brendan on 3/27/2025.
//

#ifndef NicInfo_H
#define NicInfo_H

#include <vector>

#include "PdhQueryManager.h"
#include <windows.h>

class NicInfo
{
    public:
    NicInfo();

    ~NicInfo();

    [[nodiscard]] bool initialize(PdhQueryManager *pdhManager);

    [[nodiscard]] size_t getInstanceNames(std::vector<std::wstring> *list) const;

    [[nodiscard]] size_t getNumNics() const;

    [[nodiscard]] int getAllCounters(std::vector<unsigned long long> *nicBandwidthBpsValues,
                                     std::vector<unsigned long long> *nicSendBytesValues,
                                     std::vector<unsigned long long> *nicRecvBytesValues) const;

    
    private:
    size_t num_nics = 0;
    std::vector<std::wstring> nic_instance_names;

    std::vector<PDH_HCOUNTER> m_nicRecvBytesCounters;
    std::vector<PDH_HCOUNTER> m_nicSendBytesCounters;
    std::vector<PDH_HCOUNTER> m_nicBandwidthBpsCounters;

    PdhQueryManager *m_pdhManager;

    [[nodiscard]] size_t initInstances();

};

#endif // NicInfo_H