//
// Created by cmarc on 3/23/2025.
//

#ifndef PDHQUERYMANAGER_H
#define PDHQUERYMANAGER_H
#include <pdh.h>
#include <string>


class PdhQueryManager
{
public:
    PdhQueryManager();

    ~PdhQueryManager();

    bool initialize();

    bool addCounter(const std::string &counterPath, PDH_HCOUNTER *pCounter) const;

    [[nodiscard]] bool collectData() const;

    bool getCounterValue(PDH_HCOUNTER counter, unsigned int *value) const;
    bool getCounterValue(PDH_HCOUNTER counter, int *value) const;
    bool getCounterValue(PDH_HCOUNTER counter, unsigned long long *value) const;
    bool getCounterValue(PDH_HCOUNTER counter, long long *value) const;
    bool getCounterValue(PDH_HCOUNTER counter, double *value) const;

private:
    PDH_HQUERY m_query;
    bool m_initialized;
};


#endif //PDHQUERYMANAGER_H
