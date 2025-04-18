//
// Created by Brendan on 3/27/2025.
//

#ifndef NicInfo_H
#define NicInfo_H

#include <vector>

#include "PdhQueryManager.h"
#include "WmiQueryManager.h"
#include <windows.h>


class NicInfo
{
    public:
    NicInfo();

    ~NicInfo();

    [[nodiscard]] bool initialize(PdhQueryManager *pdhManager, WmiQueryManager *wmiManager);

    [[nodiscard]] size_t getInstanceNames(std::vector<std::wstring> *list) const;

    [[nodiscard]] size_t getNumNics() const;

    [[nodiscard]] int getAllCounters(std::vector<unsigned long long> *nicBandwidthBpsValues,
                                     std::vector<unsigned long long> *nicSendBytesValues,
                                     std::vector<unsigned long long> *nicRecvBytesValues) const;

    /**
     * Retrieves information regarding the NIC hardware devices available on the system via a WMI query
     *
     * @param hardwareNames A pointer to a std::vector of wide strings to fill with the friendly name, or NIC description,
     * of all NIC devices
     * @param labels A pointer to a std::vector of wide strings to fill with the interface names, or effectively labels,
     * of all NIC devices.
     * Labels returned from WMI usually identify the type of wireless interface (and a number, if there are
     * multiple), such as "Ethernet 2"
     * @param uniqueIds A pointer to a std::vector of wide strings to fill with the locally unique identifiers
     * of all NIC devices
     * @param nicTypes A pointer to a std::vector containing unsigned integers representing the type of disk device
     *                      (3 for HDD, 4 for SSD, etc.)
     * @return A status code either as part of the WMI standard or 0 for success or a negative number
     *          for a failure of some kind (as an int)
     * @link For information on the mappings between the NIC interface type and the kind of disk (hard drive) in use, visit:
     *  https://learn.microsoft.com/en-us/windows/win32/fwp/wmi/netadaptercimprov/msft-netadapter
     */
    [[nodiscard]] int getNicInformation(std::vector<std::wstring> *hardwareNames,
                                                 std::vector<std::wstring> *labels,
                                                 std::vector<std::wstring> *uniqueIds,
                                                 std::vector<unsigned int> *nicTypes) const;
    
    private:
    size_t num_nics = 0;
    std::vector<std::wstring> nic_instance_names;

    std::vector<PDH_HCOUNTER> m_nicRecvBytesCounters;
    std::vector<PDH_HCOUNTER> m_nicSendBytesCounters;
    std::vector<PDH_HCOUNTER> m_nicBandwidthBpsCounters;

    PdhQueryManager *m_pdhManager;
    WmiQueryManager *m_wmiManager;

    [[nodiscard]] size_t initInstances();

};

#endif // NicInfo_H