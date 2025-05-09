//
// Created by cmarc on 3/23/2025.
//

#ifndef PDHQUERYMANAGER_H
#define PDHQUERYMANAGER_H

#ifdef _WIN32
    #ifdef BUILDING_DLL
        #define YAMEC_API __declspec(dllexport)
    #else
        #define YAMEC_API __declspec(dllimport)
    #endif
#else
    #define YAMEC_API
#endif

#include <map>
#include <vector>
#include <pdh.h>
#include <string>
#include <unordered_map>


class YAMEC_API PdhQueryManager
{
public:
    PdhQueryManager();

    ~PdhQueryManager();

    bool initialize();

    size_t getInstances(const std::string& objectName, std::vector<std::wstring> &instanceList) const;

    bool addCounter(const std::string &counterPath, PDH_HCOUNTER *pCounter) const;

    bool addCounter(const std::wstring &counterPath, PDH_HCOUNTER *pCounter) const;


    [[nodiscard]] bool collectData() const;

    bool getCounterValue(PDH_HCOUNTER counter, unsigned int *value) const;
    bool getCounterValue(PDH_HCOUNTER counter, int *value) const;
    bool getCounterValue(PDH_HCOUNTER counter, unsigned long long *value) const;
    bool getCounterValue(PDH_HCOUNTER counter, long long *value) const;
    bool getCounterValue(PDH_HCOUNTER counter, double *value) const;

    bool getCounterValues(PDH_HCOUNTER counter,
        std::vector<std::wstring> *instanceNames, std::vector<long long> *instanceValues) const;

    bool getCounterValues(PDH_HCOUNTER counter,
        std::vector<std::wstring> *instanceNames, std::vector<int> *instanceValues) const;

    bool getCounterValues(PDH_HCOUNTER counter, std::unordered_map<std::wstring, double> *instanceValues) const;

    bool getCounterValues(PDH_HCOUNTER counter, std::unordered_map<std::wstring, long long> *instanceValues) const;

    bool getCounterValues(PDH_HCOUNTER counter, std::unordered_map<std::wstring, unsigned long long> *instanceValues) const;

    bool getCounterValues(PDH_HCOUNTER counter, std::unordered_map<std::wstring, int> *instanceValues) const;

    bool getCounterValues(PDH_HCOUNTER counter, std::unordered_map<std::wstring, unsigned int> *instanceValues) const;

    bool getCounterValues(PDH_HCOUNTER counter,
                          std::vector<std::wstring> *instanceNames, std::vector<double> *instanceValues) const;

private:
    PDH_HQUERY m_query;
    bool m_initialized;
};


#endif //PDHQUERYMANAGER_H
