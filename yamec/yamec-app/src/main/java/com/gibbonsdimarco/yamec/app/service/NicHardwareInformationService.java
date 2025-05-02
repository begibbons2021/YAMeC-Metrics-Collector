package com.gibbonsdimarco.yamec.app.service;

import com.gibbonsdimarco.yamec.app.data.NicHardwareInformation;
import com.gibbonsdimarco.yamec.app.repository.NicHardwareInformationRepository;
import com.gibbonsdimarco.yamec.app.repository.SystemNicMetricRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class NicHardwareInformationService {

    private final SystemNicMetricRepository nicMetricRepository;
    private final NicHardwareInformationRepository nicHardwareInformationRepository;

    @Autowired
    public NicHardwareInformationService(SystemNicMetricRepository nicMetricRepository,
                                         NicHardwareInformationRepository nicHardwareInformationRepository) {
        this.nicMetricRepository = nicMetricRepository;
        this.nicHardwareInformationRepository = nicHardwareInformationRepository;
    }

    @Transactional
    public java.util.List<NicHardwareInformation>
            saveNicInformation(java.util.List<NicHardwareInformation> nicInformation) {

        java.util.ArrayList<NicHardwareInformation> nicsToSave = new java.util.ArrayList<>();

        for (NicHardwareInformation nicHardwareInformation : nicInformation) {
            // Query for all disks detected
            NicHardwareInformation matchingConfiguration
                    = nicHardwareInformationRepository.findByUniqueId(nicHardwareInformation.getUniqueId());

            // Update pre-existing disks and add new ones to the database
            nicsToSave.add(Objects.requireNonNullElse(matchingConfiguration, nicHardwareInformation));
        }

        // Save all changes to the database
        return nicHardwareInformationRepository.saveAllAndFlush(nicsToSave);

    }

    public java.util.List<NicHardwareInformation> getStoredDiskInformation() {
        return nicHardwareInformationRepository.findAll(
                org.springframework.data.domain.PageRequest.of(0, 255,
                        org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,
                                "id")))
                .getContent();
    }

    public java.util.List<NicHardwareInformation> getStoredDiskInformation(int pageNumber) {
        return nicHardwareInformationRepository.findAll(
                        org.springframework.data.domain.PageRequest.of(pageNumber, 255,
                                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,
                                        "id")))
                .getContent();
    }

    // Other service methods as needed
}