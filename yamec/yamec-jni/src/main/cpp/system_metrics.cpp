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

unsigned int getGpuUsage()
{
    PDH_HQUERY hQuery;
    PDH_HCOUNTER hCounter;
    PDH_FMT_COUNTERVALUE counterValue;

    // https://kb.paessler.com/en/topic/50673-how-can-i-find-out-the-names-of-available-performance-counters
    // https://stackoverflow.com/questions/77643749/getting-gpu-usage-statistics-with-pdh

    // Todo: Enumerate through each "GPU Engine"
    // Todo: Select the engine corresponding to a hardware GPU with the highest utilization
    //          This is how Task Manager does it
    //          https://devblogs.microsoft.com/directx/gpus-in-the-task-manager/
    // Todo: Get shared and dedicated usage per GPU device
    // Concern: GPU Adapters list more than the number of devices actually present

    // Open a query
    if (PdhOpenQuery(nullptr, 0, &hQuery) != ERROR_SUCCESS) {
        std::cerr << "Failed to open PDH query." << std::endl;
        return 1;
    }

    // Add a counter for "% Processor Time"
    if (PdhAddCounter(hQuery, TEXT("\\GPU Engine(*)\\Utilization Percentage"), 0, &hCounter) != ERROR_SUCCESS) {
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
            std::cout << "GPU Usage: " << counterValue.doubleValue << "%" << std::endl;
        }
    }

    // Cleanup
    PdhCloseQuery(hQuery);
    return 0;
}

unsigned int getAllGpuDevicesUsage() {
    PDH_HQUERY query;
    PDH_STATUS status;
    DWORD counterListSize = 0;
    DWORD instanceListSize = 0;

    // Initialize the query
    status = PdhOpenQuery(nullptr, 0, &query);
    if (status != ERROR_SUCCESS) {
        std::cerr << "Failed to open PDH query. Error code: " << status << std::endl;
        return 0;
    }

    // Get the required buffer sizes
    status = PdhEnumObjectItems(nullptr, nullptr, TEXT("GPU Engine"), nullptr, &counterListSize, nullptr, &instanceListSize, PERF_DETAIL_WIZARD, 0);
    if (status != ERROR_SUCCESS && status != PDH_MORE_DATA) {
        std::cerr << "Failed to get buffer size. Error code: " << status << std::endl;
        PdhCloseQuery(query);
        return 0;
    }

    // Allocate buffers
    std::vector<WCHAR> counterList(counterListSize);
    std::vector<WCHAR> instanceList(instanceListSize);

    // Enumerate the instances
    status = PdhEnumObjectItemsW(nullptr, nullptr, L"GPU Engine", counterList.data(), &counterListSize, instanceList.data(), &instanceListSize, PERF_DETAIL_WIZARD, 0);
    if (status != ERROR_SUCCESS) {
        std::cerr << "Failed to enumerate objects. Error code: " << status << std::endl;
        PdhCloseQuery(query);
        return 0;
    }

    // Parse the instance list (null-separated string)
    std::vector<std::wstring> gpuInstances;
    WCHAR* instance = instanceList.data();
    while (*instance) {
        gpuInstances.emplace_back(instance);
        instance += wcslen(instance) + 1;
    }

    // Vector to store counter handles
    std::vector<PDH_HCOUNTER> counters;

    // Add counters for each GPU instance
    for (const auto& instance : gpuInstances) {
        PDH_HCOUNTER counter;
        std::wstring counterPath = L"\\GPU Engine(" + instance + L")\\Utilization Percentage";
        status = PdhAddCounterW(query, counterPath.c_str(), 0, &counter);
        if (status == ERROR_SUCCESS) {
            counters.push_back(counter);
        } else {
            std::wcerr << L"Failed to add counter for " << instance << L". Error code: " << status << std::endl;
        }
    }

    if (counters.empty()) {
        std::cerr << "No valid GPU counters found." << std::endl;
        PdhCloseQuery(query);
        return 0;
    }

    // First collection to establish baseline
    status = PdhCollectQueryData(query);
    if (status != ERROR_SUCCESS) {
        std::cerr << "Failed first query data collection. Error code: " << status << std::endl;
        PdhCloseQuery(query);
        return 0;
    }

    Sleep(1000); // Wait for data collection

    // Second collection to get actual values
    status = PdhCollectQueryData(query);
    if (status != ERROR_SUCCESS) {
        std::cerr << "Failed second query data collection. Error code: " << status << std::endl;
        PdhCloseQuery(query);
        return 0;
    }

    // Process the collected data
    unsigned int totalUsage = 0;
    unsigned int gpuCount = 0;

    for (size_t i = 0; i < counters.size(); i++) {
        PDH_FMT_COUNTERVALUE value;
        status = PdhGetFormattedCounterValue(counters[i], PDH_FMT_DOUBLE, NULL, &value);
        if (status == ERROR_SUCCESS) {
            std::wcout << L"GPU " << gpuInstances[i] << L" Usage: " << value.doubleValue << L"%" << std::endl;
            totalUsage += static_cast<unsigned int>(value.doubleValue);
            gpuCount++;
        } else {
            std::wcerr << L"Failed to get counter value for " << gpuInstances[i] << L". Error code: " << status << std::endl;
        }
    }

    // Clean up
    PdhCloseQuery(query);

    // Return average GPU usage if there are any valid readings
    return gpuCount > 0 ? totalUsage / gpuCount : 0;
}

unsigned int getCpuTemperature()
{
    SYSTEM_INFO info;

    // TODO
    // https://cplusplus.com/forum/general/146576/

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

unsigned int getSystemNicRecvBandwidth()
{
    PDH_HQUERY hquery;

    // Open the PDH query connection
    PDH_STATUS status = PdhOpenQuery(nullptr, 0, &hquery);
    if (status != ERROR_SUCCESS)
    {
        std::wcerr << "Failed to open the PDH query connection. Error code: " << std::hex << status << std::endl;
        return 0;
    }

    // Get a list of Pdh counters for NIC devices
    // First get the counts for the buffer length of the list of counters and instances
    DWORD counterNameCharsSize = 0;
    DWORD instanceNameCharsSize = 0;
    status = PdhEnumObjectItems(nullptr, nullptr, TEXT("Network Interface"),
                                    nullptr, &counterNameCharsSize,
                                    nullptr, &instanceNameCharsSize,
                                    PERF_DETAIL_WIZARD, 0);
    if (status != ERROR_SUCCESS && status != PDH_MORE_DATA)
    {
        std::wcerr << "Failed to get NIC instance list. Error code: " << std::hex << status << std::endl;
        PdhCloseQuery(hquery);
        return 0;
    }

    // Then fill the buffer of counter and instance characters
    std::vector<WCHAR> counterNameChars(counterNameCharsSize);
    std::vector<WCHAR> instanceNameChars(instanceNameCharsSize);
    status = PdhEnumObjectItemsW(nullptr, nullptr, L"Network Interface",
                                    counterNameChars.data(), &counterNameCharsSize,
                                    instanceNameChars.data(), &instanceNameCharsSize,
                                    PERF_DETAIL_WIZARD, 0);
    if (status != ERROR_SUCCESS)
    {
        std::wcerr << "Failed to fill NIC instance list. Error code: " << std::hex <<  status << std::endl;
        PdhCloseQuery(hquery);
        return 0;
    }

    // Finally, separate instance names into separate strings in a vector
    std::vector<std::wstring> nicNames;
    WCHAR* currentNamePtr = instanceNameChars.data();
    while (*currentNamePtr)
    {
        nicNames.emplace_back(currentNamePtr);
        currentNamePtr += wcslen(currentNamePtr) + 1;
    }

    // Set up counters for data collection
    std::vector<HCOUNTER> counters;

    for (const auto & nicName : nicNames)
    {
        auto counter = HCOUNTER();
        std::wstring counterPath = L"\\Network Interface(" + nicName + L")\\Bytes Received/sec";
        status = PdhAddCounterW(hquery, counterPath.c_str(), 0, &counter);
        if (status == ERROR_SUCCESS)
        {
            counters.push_back(counter);
        }
        else
        {
            std::wcerr << L"Could not add counter for NIC receive bandwidth for " << nicName
                           << L". Error Code: " << std::hex << status << std::endl;
        }

    }

    if (counters.empty())
    {
        std::wcerr << "No valid NIC counters found." << std::endl;
        PdhCloseQuery(hquery);
        return 0;
    }

    // Start collection of data from counters
    status = PdhCollectQueryData(hquery);
    if (status != ERROR_SUCCESS)
    {
        std::wcerr << "Could not initialize query for NIC receive bandwidth data. Error code: " << std::hex << status << std::endl;
        PdhCloseQuery(hquery);
        return 0;
    }

    Sleep(1000); // Wait for data to be collected/calculated

    // Collect data from counters
    status = PdhCollectQueryData(hquery);
    if (status != ERROR_SUCCESS)
    {
        std::wcerr << "Could not query NIC receive bandwidth data. Error code: " << std::hex << status << std::endl;
        PdhCloseQuery(hquery);
        return 0;
    }

    // Output counter data
    for (size_t i = 0; i < counters.size(); i++)
    {
        PDH_FMT_COUNTERVALUE counterValue;
        status = PdhGetFormattedCounterValue(counters.at(i), PDH_FMT_DOUBLE, nullptr, &counterValue);
        if (status == ERROR_SUCCESS)
        {
            std::wcout << L"NIC: " << nicNames.at(i) << L" | Receive Bandwidth: "
                        << counterValue.doubleValue << L" bytes/sec" << std::endl;
        }
        else {
            std::wcerr << L"Failed to get counter value for " << nicNames.at(i)
                        << L". Error code: " << std::hex << status << std::endl;
        }
    }

    PdhCloseQuery(hquery);

    return 1;

}

unsigned int getSystemNicSendBandwidth()
{
    PDH_HQUERY hquery;

    // Open the PDH query connection
    PDH_STATUS status = PdhOpenQuery(nullptr, 0, &hquery);
    if (status != ERROR_SUCCESS)
    {
        std::wcerr << "Failed to open the PDH query connection. Error code: " << std::hex << status << std::endl;
        return 0;
    }

    // Get a list of Pdh counters for NIC devices
    // First get the counts for the buffer length of the list of counters and instances
    DWORD counterNameCharsSize = 0;
    DWORD instanceNameCharsSize = 0;
    status = PdhEnumObjectItems(nullptr, nullptr, TEXT("Network Interface"),
                                    nullptr, &counterNameCharsSize,
                                    nullptr, &instanceNameCharsSize,
                                    PERF_DETAIL_WIZARD, 0);
    if (status != ERROR_SUCCESS && status != PDH_MORE_DATA)
    {
        std::wcerr << "Failed to get NIC instance list. Error code: " << std::hex << status << std::endl;
        PdhCloseQuery(hquery);
        return 0;
    }

    // Then fill the buffer of counter and instance characters
    std::vector<WCHAR> counterNameChars(counterNameCharsSize);
    std::vector<WCHAR> instanceNameChars(instanceNameCharsSize);
    status = PdhEnumObjectItemsW(nullptr, nullptr, L"Network Interface",
                                    counterNameChars.data(), &counterNameCharsSize,
                                    instanceNameChars.data(), &instanceNameCharsSize,
                                    PERF_DETAIL_WIZARD, 0);
    if (status != ERROR_SUCCESS)
    {
        std::wcerr << "Failed to fill NIC instance list. Error code: " << std::hex <<  status << std::endl;
        PdhCloseQuery(hquery);
        return 0;
    }

    // Finally, separate instance names into separate strings in a vector
    std::vector<std::wstring> nicNames;
    WCHAR* currentNamePtr = instanceNameChars.data();
    while (*currentNamePtr)
    {
        nicNames.emplace_back(currentNamePtr);
        currentNamePtr += wcslen(currentNamePtr) + 1;
    }

    // Set up counters for data collection
    std::vector<HCOUNTER> counters;

    for (const auto & nicName : nicNames)
    {
        auto counter = HCOUNTER();
        std::wstring counterPath = L"\\Network Interface(" + nicName + L")\\Bytes Sent/sec";
        status = PdhAddCounterW(hquery, counterPath.c_str(), 0, &counter);
        if (status == ERROR_SUCCESS)
        {
            counters.push_back(counter);
        }
        else
        {
            std::wcerr << L"Could not add counter for NIC send bandwidth for " << nicName
                           << L". Error Code: " << std::hex << status << std::endl;
        }

    }

    if (counters.empty())
    {
        std::wcerr << "No valid NIC counters found." << std::endl;
        PdhCloseQuery(hquery);
        return 0;
    }

    // Start collection of data from counters
    status = PdhCollectQueryData(hquery);
    if (status != ERROR_SUCCESS)
    {
        std::wcerr << "Could not initialize query for NIC send bandwidth data. Error code: " << std::hex << status << std::endl;
        PdhCloseQuery(hquery);
        return 0;
    }

    Sleep(1000); // Wait for data to be collected/calculated

    // Collect data from counters
    status = PdhCollectQueryData(hquery);
    if (status != ERROR_SUCCESS)
    {
        std::wcerr << "Could not query NIC send bandwidth data. Error code: " << std::hex << status << std::endl;
        PdhCloseQuery(hquery);
        return 0;
    }

    // Output counter data
    for (size_t i = 0; i < counters.size(); i++)
    {
        PDH_FMT_COUNTERVALUE counterValue;
        status = PdhGetFormattedCounterValue(counters.at(i), PDH_FMT_DOUBLE, nullptr, &counterValue);
        if (status == ERROR_SUCCESS)
        {
            std::wcout << L"NIC: " << nicNames.at(i) << L" | Send Bandwidth: "
                        << counterValue.doubleValue << L" bytes/sec" << std::endl;
        }
        else {
            std::wcerr << L"Failed to get counter value for " << nicNames.at(i)
                        << L". Error code: " << std::hex << status << std::endl;
        }
    }

    PdhCloseQuery(hquery);

    return 1;

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