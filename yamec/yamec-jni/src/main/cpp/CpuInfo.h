//
// Created by cmarc on 3/23/2025.
//

#ifndef CPUINFO_H
#define CPUINFO_H

#ifdef _WIN32
    #ifdef BUILDING_DLL
        #define YAMEC_API __declspec(dllexport)
    #else
        #define YAMEC_API __declspec(dllimport)
    #endif
#else
    #define YAMEC_API
#endif


// CpuInfo.h
#include "PdhQueryManager.h"
#include <windows.h>
#include <intrin.h>
#include <string>

struct SystemInfo
{
    DWORD numberOfProcessors;
    WORD architecture;
};

struct CacheInfo
{
    DWORD logicalProcessorCount = 0;
    DWORD numaNodeCount = 0;
    DWORD processorCoreCount = 0;
    DWORD processorL1CacheSize = 0;
    DWORD processorL2CacheSize = 0;
    DWORD processorL3CacheSize = 0;
    DWORD processorPackageCount = 0;
};

class YAMEC_API CpuInfo
{
public:
    CpuInfo();

    ~CpuInfo();

    [[nodiscard]] bool initialize(PdhQueryManager *pdhManager);

    [[nodiscard]] int getUsage(double *usage) const;

    [[nodiscard]] static std::string getBrandString();

    [[nodiscard]] static SystemInfo getSystemInfo();

    [[nodiscard]] static CacheInfo getCacheInfo();

    [[nodiscard]] static unsigned long long getSystemUptime();

    [[nodiscard]] static bool isVirtualizationAvailable();

private:
    PDH_HCOUNTER m_usageCounter{};
    PdhQueryManager *m_pdhManager;

    static DWORD countSetBits(ULONG_PTR bitMask);
};


#endif //CPUINFO_H
