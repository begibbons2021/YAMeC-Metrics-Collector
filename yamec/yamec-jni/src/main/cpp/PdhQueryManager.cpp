//
// Created by cmarc on 3/23/2025.
//

#include "PdhQueryManager.h"

#include <iostream>

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
