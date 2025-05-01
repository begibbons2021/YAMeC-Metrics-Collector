package com.gibbonsdimarco.yamec.app.service;

import com.gibbonsdimarco.yamec.app.data.DiskHardwareInformation;
import com.gibbonsdimarco.yamec.app.data.MemoryHardwareInformation;
import com.gibbonsdimarco.yamec.app.data.SystemDiskMetric;
import com.gibbonsdimarco.yamec.app.repository.DiskHardwareInformationRepository;
import com.gibbonsdimarco.yamec.app.repository.MemoryHardwareInformationRepository;
import com.gibbonsdimarco.yamec.app.repository.SystemDiskMetricRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class DiskHardwareInformationService {

    private final SystemDiskMetricRepository diskMetricRepository;
    private final DiskHardwareInformationRepository diskHardwareInformationRepository;

    @Autowired
    public DiskHardwareInformationService(SystemDiskMetricRepository diskMetricRepository,
                                          DiskHardwareInformationRepository diskHardwareInformationRepository) {
        this.diskMetricRepository = diskMetricRepository;
        this.diskHardwareInformationRepository = diskHardwareInformationRepository;
    }

    @Transactional
    public java.util.List<DiskHardwareInformation>
            saveDiskInformation(java.util.List<DiskHardwareInformation> diskInformation) {

        java.util.ArrayList<DiskHardwareInformation> disksToSave = new java.util.ArrayList<>();

        for (DiskHardwareInformation diskHardwareInformation : diskInformation) {
            // Query for all disks detected
            DiskHardwareInformation matchingConfiguration
                    = diskHardwareInformationRepository.findByUniqueId(diskHardwareInformation.getUniqueId());

            // Update pre-existing disks and add new ones to the database
            disksToSave.add(Objects.requireNonNullElse(matchingConfiguration, diskHardwareInformation));
        }

        // Save all changes to the database
        return diskHardwareInformationRepository.saveAllAndFlush(disksToSave);

    }

    public java.util.List<DiskHardwareInformation> getStoredDiskInformation() {
        return diskHardwareInformationRepository.findAll(
                org.springframework.data.domain.PageRequest.of(0, 255,
                        org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,
                                "id")))
                .getContent();
    }

    public java.util.List<DiskHardwareInformation> getStoredDiskInformation(int pageNumber) {
        return diskHardwareInformationRepository.findAll(
                        org.springframework.data.domain.PageRequest.of(pageNumber, 255,
                                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,
                                        "id")))
                .getContent();
    }

    // Other service methods as needed
}