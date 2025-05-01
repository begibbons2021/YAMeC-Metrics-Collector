package com.gibbonsdimarco.yamec.app.service;

import com.gibbonsdimarco.yamec.app.data.CpuHardwareInformation;
import com.gibbonsdimarco.yamec.app.data.MemoryHardwareInformation;
import com.gibbonsdimarco.yamec.app.repository.CpuHardwareInformationRepository;
import com.gibbonsdimarco.yamec.app.repository.MemoryHardwareInformationRepository;
import com.gibbonsdimarco.yamec.app.repository.SystemMemoryMetricRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class MemoryHardwareInformationService {

    private final SystemMemoryMetricRepository memoryMetricRepository;
    private final MemoryHardwareInformationRepository memoryHardwareInformationRepository;

    @Autowired
    public MemoryHardwareInformationService(SystemMemoryMetricRepository memoryMetricRepository,
                                            MemoryHardwareInformationRepository memoryHardwareInformationRepository) {
        this.memoryMetricRepository = memoryMetricRepository;
        this.memoryHardwareInformationRepository = memoryHardwareInformationRepository;
    }

    @Transactional
    public MemoryHardwareInformation saveMemoryInformation(MemoryHardwareInformation memoryInformation) {

        MemoryHardwareInformation matchingConfiguration
                = memoryHardwareInformationRepository.findMatchingMemoryHardwareInformation(memoryInformation);

        // Returns the matching hardware object if it exists; otherwise, it saves it to the database
        return Objects.requireNonNullElseGet(matchingConfiguration,
                () -> memoryHardwareInformationRepository.save(memoryInformation));

    }

    public java.util.List<MemoryHardwareInformation> getStoredMemoryInformation() {
        return memoryHardwareInformationRepository.findAll(
                org.springframework.data.domain.PageRequest.of(0, 255,
                        org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,
                                "id")))
                .getContent();
    }

    public java.util.List<MemoryHardwareInformation> getStoredMemoryInformation(int pageNumber) {
        return memoryHardwareInformationRepository.findAll(
                        org.springframework.data.domain.PageRequest.of(pageNumber, 255,
                                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,
                                        "id")))
                .getContent();
    }

    // Other service methods as needed
}