package com.gibbonsdimarco.yamec.app.repository;

import com.gibbonsdimarco.yamec.app.data.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for ApplicationMetric entities
 */
@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    // Add custom query methods if needed

    Application findByApplicationName(String name);

} 