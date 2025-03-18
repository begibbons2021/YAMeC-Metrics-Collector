#include <jni.h>
#include <windows.h>
#include <intrin.h>
#include <pdh.h>
#include <processthreadsapi.h>
// #include <perflib.h>
#include <chrono>
#include <iostream>
#include <stdexcept>
#include <string>

struct SystemInfo {
    DWORD numberOfProcessors;
    WORD architecture;
};

struct CacheInfo {
    DWORD logicalProcessorCount = 0;
    DWORD numaNodeCount = 0;
    DWORD processorCoreCount = 0;
    DWORD processorL1CacheSize = 0;
    DWORD processorL2CacheSize = 0;
    DWORD processorL3CacheSize = 0;
    DWORD processorPackageCount = 0;
    DWORD byteOffset = 0;
};

SystemInfo getSystemInfo()
{
    SYSTEM_INFO sysInfo;
    GetSystemInfo(&sysInfo);

    SystemInfo info{};
    info.numberOfProcessors = sysInfo.dwNumberOfProcessors;
    info.architecture = sysInfo.wProcessorArchitecture;

    return info;
}

// Helper function to count set bits in the processor mask.
DWORD CountSetBits(ULONG_PTR bitMask)
{
    DWORD LSHIFT = sizeof(ULONG_PTR)*8 - 1;
    DWORD bitSetCount = 0;
    ULONG_PTR bitTest = (ULONG_PTR)1 << LSHIFT;
    DWORD i;

    for (i = 0; i <= LSHIFT; ++i)
    {
        bitSetCount += ((bitMask & bitTest)?1:0);
        bitTest/=2;
    }

    return bitSetCount;
}

// Inspired heavily by https://learn.microsoft.com/en-us/windows/win32/api/sysinfoapi/nf-sysinfoapi-getlogicalprocessorinformation
CacheInfo getCacheInfo() {
    BOOL done = FALSE;
    PSYSTEM_LOGICAL_PROCESSOR_INFORMATION buffer = nullptr;
    PSYSTEM_LOGICAL_PROCESSOR_INFORMATION ptr = nullptr;
    DWORD returnLength = 0;
    DWORD byteOffset = 0;
    PCACHE_DESCRIPTOR Cache;

    while (!done)
    {
        DWORD rc = GetLogicalProcessorInformation(buffer, &returnLength);

        if (FALSE == rc)
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
            }
            else
            {
                throw std::runtime_error(std::format("Error: %d", GetLastError()));
            }
        }
        else
        {
            done = TRUE;
        }
    }

    ptr = buffer;
    CacheInfo cache{};

    while (byteOffset + sizeof(SYSTEM_LOGICAL_PROCESSOR_INFORMATION) <= returnLength) {
        switch (ptr->Relationship)
        {
            case RelationNumaNode:
                // Non-NUMA systems report a single record of this type.
                    cache.numaNodeCount++;
            break;

            case RelationProcessorCore:
                cache.processorCoreCount++;

            // A hyperthreaded core supplies more than one logical processor.
            cache.logicalProcessorCount += CountSetBits(ptr->ProcessorMask);
            break;

            case RelationCache:
                // Cache data is in ptr->Cache, one CACHE_DESCRIPTOR structure for each cache.
                    Cache = &ptr->Cache;
            if (Cache->Level == 1)
            {
                cache.processorL1CacheSize += Cache->Size;
            }
            else if (Cache->Level == 2)
            {
                cache.processorL2CacheSize += Cache->Size;
            }
            else if (Cache->Level == 3)
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

unsigned long long getPhysicalMemory()
{
    ULONGLONG memory;

    if (!GetPhysicallyInstalledSystemMemory(&memory))
    {
        throw std::runtime_error("Unable to get RAM information.");
    }

    // https://learn.microsoft.com/en-us/windows/win32/api/sysinfoapi/nf-sysinfoapi-globalmemorystatusex

    return memory;
}

unsigned int getCpuUsage()
{
    PDH_HQUERY hQuery;
    PDH_HCOUNTER hCounter;
    PDH_FMT_COUNTERVALUE counterValue;

    // Open a query
    if (PdhOpenQuery(nullptr, 0, &hQuery) != ERROR_SUCCESS) {
        std::cerr << "Failed to open PDH query." << std::endl;
        return 1;
    }

    // Add a counter for "% Processor Time"
    if (PdhAddCounter(hQuery, TEXT("\\Processor(_Total)\\% Processor Time"), 0, &hCounter) != ERROR_SUCCESS) {
        std::cerr << "Failed to add counter." << std::endl;
        PdhCloseQuery(hQuery);
        return 1;
    }

    // Collect data
    if (PdhCollectQueryData(hQuery) != ERROR_SUCCESS) {
        std::cerr << "Failed to collect data." << std::endl;
        PdhCloseQuery(hQuery);
        return 1;
    }

    // Wait and collect data again
    Sleep(500);
    if (PdhCollectQueryData(hQuery) == ERROR_SUCCESS) {
        if (PdhGetFormattedCounterValue(hCounter, PDH_FMT_DOUBLE, nullptr, &counterValue) == ERROR_SUCCESS) {
            std::cout << "CPU Usage: " << counterValue.doubleValue << "%" << std::endl;
        }
    }

    // Cleanup
    PdhCloseQuery(hQuery);
    return 0;
}

unsigned int getCpuTemperature()
{
    SYSTEM_INFO info;

    // TODO

    return 0;
}

unsigned long long int getSystemUptime()
{
    const ULONGLONG ms = GetTickCount64();

    return std::chrono::milliseconds(ms).count();
}

bool isVirtualizationAvailable()
{
    return IsProcessorFeaturePresent(21);
}

std::string GetCPUBrandString()
{
    char brand[0x40] = {};
    int cpuInfo[4] = {-1};

    __cpuid(cpuInfo, 0x80000000);
    unsigned int nExIds = cpuInfo[0];

    for (unsigned int i = 0x80000000; i <= nExIds; ++i) {
        __cpuid(cpuInfo, i);
        if (i == 0x80000002)
            memcpy(brand, cpuInfo, sizeof(cpuInfo));
        else if (i == 0x80000003)
            memcpy(brand + 16, cpuInfo, sizeof(cpuInfo));
        else if (i == 0x80000004)
            memcpy(brand + 32, cpuInfo, sizeof(cpuInfo));
    }

    return std::string(brand);
}


int main()
{
    getCpuUsage();
    printf("CPU Brand String: %s\n", GetCPUBrandString().c_str());
    SystemInfo info = getSystemInfo();
    std::cout <<  "Number of processors: " << info.numberOfProcessors << "\n";
    std::cout << "Architecture: " << info.architecture << "\n";
    std::cout << "System has been up for " << getSystemUptime() << " ms" << "\n";
    std::cout << "System has " << getPhysicalMemory() / 1024.0  << "MB of RAM installed" << "\n";

    CacheInfo cache = getCacheInfo();

    std::cout << "Cache info: " << "\n";
    std::cout << "L1 cache size: " << cache.processorL1CacheSize / 1024.0 << "KB" << "\n";
    std::cout << "L2 cache size: " << cache.processorL2CacheSize / 1024.0 << "KB" << "\n";
    std::cout << "L3 cache size: " << cache.processorL3CacheSize / 1024.0 << "KB" << std::endl;

    return 0;
}