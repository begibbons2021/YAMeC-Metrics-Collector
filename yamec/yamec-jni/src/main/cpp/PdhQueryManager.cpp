//
// Created by cmarc on 3/23/2025.
//

#include "PdhQueryManager.h"

#include <iostream>
#include <vector>
#include <pdhmsg.h>

PdhQueryManager::PdhQueryManager() : m_initialized(false) {}

PdhQueryManager::~PdhQueryManager()
{
    if (m_initialized)
    {
        PdhCloseQuery(m_query);
    }
}

bool PdhQueryManager::initialize()
{
    if (m_initialized)
    {
        return true;
    }

    if (PdhOpenQuery(nullptr, 0, &m_query) != ERROR_SUCCESS)
    {
        std::cerr << "Failed to open PDH query." << std::endl;
        return false;
    }

    m_initialized = true;
    return true;
}

size_t PdhQueryManager::getInstances(const std::string& objectName, std::vector<std::wstring> &instanceList) const
{
    if (!m_initialized)
    {
        std::cerr << "Query not initialized." << std::endl;
        return 0;
    }

    // Get a list of Pdh counters for Disk devices
    // First get the counts for the buffer length of the list of counters and instances
    DWORD counterNameCharsSize = 0;
    DWORD instanceNameCharsSize = 0;
    PDH_STATUS status = PdhEnumObjectItems(nullptr, nullptr, TEXT(objectName.c_str()),
                                    nullptr, &counterNameCharsSize,
                                    nullptr, &instanceNameCharsSize,
                                    PERF_DETAIL_WIZARD, 0);
    if (status != ERROR_SUCCESS && status != PDH_MORE_DATA)
    {
        std::wcerr << "Failed to get " << TEXT(objectName.c_str()) << " instance list. Error code: " << std::hex << status << std::endl;
        return 0;
    }

    // Convert Object Name retrieved to a wide string
    auto objectNamePtr = objectName.c_str();
    auto objectNameLength = objectName.length();
    auto objectNameAsWchar = new wchar_t[objectNameLength + 1];
    size_t convertedChars = 0;
    mbstowcs_s(&convertedChars, objectNameAsWchar, objectNameLength + 1, objectNamePtr, objectNameLength);


    // Then fill the buffer of counter and instance characters
    std::vector<WCHAR> counterNameChars(counterNameCharsSize);
    std::vector<WCHAR> instanceNameChars(instanceNameCharsSize);
    status = PdhEnumObjectItemsW(nullptr, nullptr, objectNameAsWchar,
                                    counterNameChars.data(), &counterNameCharsSize,
                                    instanceNameChars.data(), &instanceNameCharsSize,
                                    PERF_DETAIL_WIZARD, 0);
    if (status != ERROR_SUCCESS)
    {
        std::wcerr << "Failed to fill " << objectNameAsWchar << " instance list. Error code: " << std::hex <<  status << std::endl;
        return 0;
    }

    // Finally, separate instance names into separate string;
    std::vector<std::wstring> diskNames;
    WCHAR* currentNamePtr = instanceNameChars.data();
    while (*currentNamePtr)
    {
        diskNames.emplace_back(currentNamePtr);
        currentNamePtr += wcslen(currentNamePtr) + 1;
    }

    const size_t numDisks = diskNames.size();
    instanceList = diskNames;

    return numDisks;

}

bool PdhQueryManager::addCounter(const std::string &counterPath, PDH_HCOUNTER *pCounter) const
{
    if (!m_initialized)
    {
        std::cerr << "Query not initialized." << std::endl;
        return false;
    }

    if (PdhAddCounter(m_query, counterPath.c_str(), 0, pCounter) != ERROR_SUCCESS)
    {
        std::cerr << "Failed to add counter: " << counterPath << std::endl;
        return false;
    }

    return true;
}

bool PdhQueryManager::addCounter(const std::wstring &counterPath, PDH_HCOUNTER *pCounter) const
{
    if (!m_initialized)
    {
        std::cerr << "Query not initialized." << std::endl;
        return false;
    }

    if (PdhAddCounterW(m_query, counterPath.c_str(), 0, pCounter) != ERROR_SUCCESS)
    {
        std::wcerr << "Failed to add counter: " << counterPath << std::endl;
        return false;
    }

    return true;
}

bool PdhQueryManager::collectData() const
{
    if (!m_initialized)
    {
        std::cerr << "Query not initialized." << std::endl;
        return false;
    }

    if (PdhCollectQueryData(m_query) != ERROR_SUCCESS)
    {
        std::cerr << "Failed to collect data." << std::endl;
        return false;
    }

    return true;
}


bool PdhQueryManager::getCounterValue(const PDH_HCOUNTER counter, int *value) const
{
    if (!m_initialized)
    {
        std::cerr << "Query not initialized." << std::endl;
        return false;
    }

    PDH_FMT_COUNTERVALUE counterValue;
    if (PdhGetFormattedCounterValue(counter, PDH_FMT_LONG, nullptr, &counterValue) != ERROR_SUCCESS)
    {
        std::cerr << "Failed to get formatted counter value." << std::endl;
        return false;
    }

    *value = counterValue.longValue;
    return true;
}

bool PdhQueryManager::getCounterValue(const PDH_HCOUNTER counter, unsigned int *value) const
{
    return getCounterValue(counter, reinterpret_cast<int *>(value));
}

bool PdhQueryManager::getCounterValue(const PDH_HCOUNTER counter, long long *value) const
{
    if (!m_initialized)
    {
        std::cerr << "Query not initialized." << std::endl;
        return false;
    }

    PDH_FMT_COUNTERVALUE counterValue;
    if (PdhGetFormattedCounterValue(counter, PDH_FMT_LARGE, nullptr, &counterValue) != ERROR_SUCCESS)
    {
        std::cerr << "Failed to get formatted counter value." << std::endl;
        return false;
    }

    *value = counterValue.largeValue;
    return true;
}

bool PdhQueryManager::getCounterValue(const PDH_HCOUNTER counter, unsigned long long *value) const
{
    return getCounterValue(counter, reinterpret_cast<long long *>(value));
}

bool PdhQueryManager::getCounterValue(const PDH_HCOUNTER counter, double *value) const
{
    if (!m_initialized)
    {
        std::cerr << "Query not initialized." << std::endl;
        return false;
    }

    PDH_FMT_COUNTERVALUE counterValue;
    if (PdhGetFormattedCounterValue(counter, PDH_FMT_DOUBLE, nullptr, &counterValue) != ERROR_SUCCESS)
    {
        std::cerr << "Failed to get formatted counter value." << std::endl;
        return false;
    }

    *value = counterValue.doubleValue;
    return true;
}