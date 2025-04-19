//
// Created by cmarc on 3/23/2025.
//

#ifndef PDHQUERYMANAGER_H
#define PDHQUERYMANAGER_H
#include <map>
#include <vector>
#include <pdh.h>
#include <string>


class PdhQueryManager
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

    bool getCounterValues(PDH_HCOUNTER counter,
        std::vector<std::wstring> *instanceNames, std::vector<double> *instanceValues) const;

private:
    PDH_HQUERY m_query;
    bool m_initialized;
};


#endif //PDHQUERYMANAGER_H
