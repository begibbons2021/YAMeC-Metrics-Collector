package com.gibbonsdimarco.yamec.app.service;

import com.gibbonsdimarco.yamec.app.data.CpuHardwareInformation;
import com.gibbonsdimarco.yamec.app.repository.CpuHardwareInformationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CpuHardwareInformationService {

    private final CpuHardwareInformationRepository cpuRepository;

    @Autowired
    public CpuHardwareInformationService(CpuHardwareInformationRepository cpuRepository) {
        this.cpuRepository = cpuRepository;
    }

    @Transactional
    public CpuHardwareInformation saveCpuInformation(CpuHardwareInformation cpuInfo) {
        return cpuRepository.save(cpuInfo);
    }

    public java.util.List<CpuHardwareInformation> getStoredCpuInformation() {
        return cpuRepository.findAll(
                org.springframework.data.domain.PageRequest.of(0, 255,
                        org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,
                                "id")))
                .getContent();
    }

    public java.util.List<CpuHardwareInformation> getStoredCpuInformation(int pageNumber) {
        return cpuRepository.findAll(
                        org.springframework.data.domain.PageRequest.of(pageNumber, 255,
                                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,
                                        "id")))
                .getContent();
    }

    // Other service methods as needed
}