// CpuInfo.cpp
#include "CpuInfo.h"
#include <iostream>
#include <chrono>
#include <stdexcept>
#include <format>

CpuInfo::CpuInfo() : m_pdhManager(nullptr) {}

CpuInfo::~CpuInfo() = default;

bool CpuInfo::initialize(PdhQueryManager *pdhManager)
{
    if (!pdhManager)
    {
        std::cerr << "Invalid PDH manager" << std::endl;
        return false;
    }

    m_pdhManager = pdhManager;

    // Add CPU usage counter
    if (!m_pdhManager->addCounter(TEXT("\\Processor(_Total)\\% Processor Time"), &m_usageCounter))
    {
        std::cerr << "Failed to add CPU usage counter" << std::endl;
        return false;
    }

    return true;
}

int CpuInfo::getUsage(double *usage) const
{
    if (!m_pdhManager)
    {
        std::cerr << "PDH manager not initialized" << std::endl;
        return -1;
    }

    if (!m_pdhManager->getCounterValue(m_usageCounter, usage))
    {
        return -4;
    }

    return 0;
}

std::string CpuInfo::getBrandString()
{
    char brand[0x40] = {};
    int cpuInfo[4] = {-1};

    __cpuid(cpuInfo, 0x80000000);
    const unsigned int nExIds = cpuInfo[0];

    for (unsigned int i = 0x80000000; i <= nExIds; ++i)
    {
        __cpuid(cpuInfo, i);
        if (i == 0x80000002)
            memcpy(brand, cpuInfo, sizeof(cpuInfo));
        else if (i == 0x80000003)
            memcpy(brand + 16, cpuInfo, sizeof(cpuInfo));
        else if (i == 0x80000004)
            memcpy(brand + 32, cpuInfo, sizeof(cpuInfo));
    }

    return {brand};
}

SystemInfo CpuInfo::getSystemInfo()
{
    SYSTEM_INFO sysInfo;
    GetSystemInfo(&sysInfo);

    SystemInfo info{};
    info.numberOfProcessors = sysInfo.dwNumberOfProcessors;
    info.architecture = sysInfo.wProcessorArchitecture;

    return info;
}

DWORD CpuInfo::countSetBits(const ULONG_PTR bitMask)
{
    constexpr DWORD LSHIFT = sizeof(ULONG_PTR) * 8 - 1;
    DWORD bitSetCount = 0;
    ULONG_PTR bitTest = static_cast<ULONG_PTR>(1) << LSHIFT;

    for (DWORD i = 0; i <= LSHIFT; ++i)
    {
        bitSetCount += ((bitMask & bitTest) ? 1 : 0);
        bitTest /= 2;
    }

    return bitSetCount;
}

CacheInfo CpuInfo::getCacheInfo()
{
    BOOL done = FALSE;
    PSYSTEM_LOGICAL_PROCESSOR_INFORMATION buffer = nullptr;
    PSYSTEM_LOGICAL_PROCESSOR_INFORMATION ptr = nullptr;
    DWORD returnLength = 0;
    DWORD byteOffset = 0;
    PCACHE_DESCRIPTOR Cache;
    CacheInfo cache{};

    while (!done)
    {
        if (const DWORD rc = GetLogicalProcessorInformation(buffer, &returnLength); FALSE == rc)
        {
            if (GetLastError() == ERROR_INSUFFICIENT_BUFFER)
            {
                if (buffer)
                    free(buffer);

                buffer = static_cast<PSYSTEM_LOGICAL_PROCESSOR_INFORMATION>(malloc(
                    returnLength));

                if (buffer == nullptr)
                {
                    throw std::runtime_error("Error: Allocation failure");
                }
            } else
            {
                throw std::runtime_error(std::format("Error: %d", GetLastError()));
            }
        } else
        {
            done = TRUE;
        }
    }

    ptr = buffer;

    while (byteOffset + sizeof(SYSTEM_LOGICAL_PROCESSOR_INFORMATION) <= returnLength)
    {
        switch (ptr->Relationship)
        {
            case RelationNumaNode:
                // Non-NUMA systems report a single record of this type.
                cache.numaNodeCount++;
                break;

            case RelationProcessorCore:
                cache.processorCoreCount++;
            // A hyperthreaded core supplies more than one logical processor.
                cache.logicalProcessorCount += countSetBits(ptr->ProcessorMask);
                break;

            case RelationCache:
                // Cache data is in ptr->Cache, one CACHE_DESCRIPTOR structure for each cache.
                Cache = &ptr->Cache;
                if (Cache->Level == 1)
                {
                    cache.processorL1CacheSize += Cache->Size;
                } else if (Cache->Level == 2)
                {
                    cache.processorL2CacheSize += Cache->Size;
                } else if (Cache->Level == 3)
                {
                    cache.processorL3CacheSize += Cache->Size;
                }
                break;

            case RelationProcessorPackage:
                // Logical processors share a physical package.
                cache.processorPackageCount++;
                break;

            default:
                throw std::runtime_error("Error: Unsupported LOGICAL_PROCESSOR_RELATIONSHIP value.");
                break;
        }
        byteOffset += sizeof(SYSTEM_LOGICAL_PROCESSOR_INFORMATION);
        ptr++;
    }

    free(buffer);
    return cache;
}

unsigned long long CpuInfo::getSystemUptime()
{
    const ULONGLONG ms = GetTickCount64();
    return std::chrono::milliseconds(ms).count();
}

bool CpuInfo::isVirtualizationAvailable()
{
    return IsProcessorFeaturePresent(21);
}
