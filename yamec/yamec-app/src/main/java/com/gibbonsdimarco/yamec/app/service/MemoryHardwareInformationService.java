package com.gibbonsdimarco.yamec.app.service;

import com.gibbonsdimarco.yamec.app.data.CpuHardwareInformation;
import com.gibbonsdimarco.yamec.app.data.MemoryHardwareInformation;
import com.gibbonsdimarco.yamec.app.repository.CpuHardwareInformationRepository;
import com.gibbonsdimarco.yamec.app.repository.MemoryHardwareInformationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemoryHardwareInformationService {

    private final MemoryHardwareInformationRepository memoryRepository;

    @Autowired
    public MemoryHardwareInformationService(MemoryHardwareInformationRepository memoryRepository) {
        this.memoryRepository = memoryRepository;
    }

    @Transactional
    public MemoryHardwareInformation saveMemoryInformation(MemoryHardwareInformation memoryInformation) {
        return memoryRepository.save(memoryInformation);
    }

    public java.util.List<MemoryHardwareInformation> getStoredMemoryInformation() {
        return memoryRepository.findAll(
                org.springframework.data.domain.PageRequest.of(0, 255,
                        org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,
                                "id")))
                .getContent();
    }

    public java.util.List<MemoryHardwareInformation> getStoredMemoryInformation(int pageNumber) {
        return memoryRepository.findAll(
                        org.springframework.data.domain.PageRequest.of(pageNumber, 255,
                                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,
                                        "id")))
                .getContent();
    }

    // Other service methods as needed
}