package com.gibbonsdimarco.yamec.app.data;

import jakarta.persistence.*;

import java.io.Serializable;

/**
 * Contains information about specific applications on the system for use in defining ApplicationMetric
 * instances
 */
@Entity
@Table(name = "application", indexes = {
        @Index(name = "idx_application_name", columnList = "application_name")
})
public class Application implements Serializable {
    /**
     * A unique identifier for use in the database to represent this Application on the system
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the application's processes on the system
     */
    @Column(name = "application_name", nullable = false)
    private String applicationName;

    /**
     * Instantiates a new Application entity with the application name
     * passed by parameter and a default ID
     *
     * @param applicationName The name of the application's processes as a String
     */
    public Application(String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * No-Args constructor required by JPA to allow reflection
     */
    public Application() {

    }

    /**
     * Retrieves the assigned ID of this Application
     * @return The long integer ID of this Application
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of this Application to the ID passed by parameter
     * @param id A long integer ID for this Application
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retrieves the name of this application's processes
     * @return A String containing this Application's processes' name
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * Sets the name of the application's associated processes
     * @param applicationName A String containing the name of this Application's processes
     */
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

}
