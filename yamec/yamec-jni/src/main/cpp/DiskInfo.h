//
// Created by Brendan on 3/27/2025.
//

#ifndef DISKINFO_H
#define DISKINFO_H

#ifdef _WIN32
    #ifdef BUILDING_DLL
        #define YAMEC_API __declspec(dllexport)
    #else
        #define YAMEC_API __declspec(dllimport)
    #endif
#else
    #define YAMEC_API
#endif

#include <vector>

#include "PdhQueryManager.h"
#include <windows.h>

#include "WmiQueryManager.h"

class YAMEC_API DiskInfo
{
    public:
    DiskInfo();

    ~DiskInfo();

    [[nodiscard]] bool initialize(PdhQueryManager *pdhManager, WmiQueryManager *wmiManager);

    [[nodiscard]] size_t getNumDisks() const;

    size_t getInstanceNames(std::vector<std::wstring> *list) const;

    [[nodiscard]] int getAllCounters(std::vector<double> *diskUsageValues,
                                     std::vector<unsigned long long> *diskReadBandwidthValues,
                                     std::vector<unsigned long long> *diskWriteBandwidthValues,
                                     std::vector<double> *diskTimeToTransferValues) const;

    /**
     * Retrieves all information regarding connected hard disk drives on the system, including:
     * <br>
     *
     * - Their friendly hardware names (hardwareNames)
     * - Their locally unique IDs (uniqueIds)
     * - Their media types as integer representation according to the WMI standard (mediaTypes)
     * - Their storage capacities in bytes (capacities)
     * - The mappings from locally unique IDs to disk numbers/disk IDs (uniqueIdsToDiskIdMappings)
     * - The mappings from disk IDs to drive letters/partitions (partitionMappings)
     *
     * A status code is returned upon method completion.
     *
     * @param hardwareNames A std::vector containing friendly hardware names for all disks as wide strings
     * @param uniqueIds A std::vector containing wide string unique IDs for each disk
     * @param mediaTypes A std::vector containing unsigned integers representing the type of disk device
     *                      (3 for HDD, 4 for SSD, etc.)
     * @param capacities A std::vector containing 64-bit unsigned long integers representing the capacity
     *                      of each of the disks
     * @param diskNumbers A std::vector containing 32-bit unsigned integers representing the drive number
     *                      used to differentiate the drives from others
     * @param partitionMappings A std::map containing partition letter mapping to unsigned integer drive
     *                          numbers assigned by the operating system
     * @return A status code either as part of the WMI standard or 0 for success or a negative number
     *          for a failure of some kind (as an int)
     *
     * @link For information on the mappings between the MediaType and the kind of disk (hard drive) in use, visit:
     * https://learn.microsoft.com/en-us/windows-hardware/drivers/storage/msft-physicaldisk
     */
    [[nodiscard]] int getDiskInformation(std::vector<std::wstring> *hardwareNames,
                                         std::vector<std::wstring> *uniqueIds,
                                         std::vector<unsigned int> *mediaTypes,
                                         std::vector<unsigned long long> *capacities,
                                         std::vector<unsigned int> *diskNumbers,
                                         std::map<std::wstring, unsigned int> *partitionMappings) const;

    private:
    size_t num_disks = 0;
    std::vector<std::wstring> disk_instance_names;

    std::vector<PDH_HCOUNTER> m_diskReadBandwidthCounters;
    std::vector<PDH_HCOUNTER> m_diskWriteBandwidthCounters;
    std::vector<PDH_HCOUNTER> m_diskUsagePercentCounters;
    std::vector<PDH_HCOUNTER> m_diskTimeToTransferCounters;

    PdhQueryManager *m_pdhManager;
    WmiQueryManager *m_wmiManager;

    [[nodiscard]] size_t initInstances();

};

#endif // DISKINFO_H