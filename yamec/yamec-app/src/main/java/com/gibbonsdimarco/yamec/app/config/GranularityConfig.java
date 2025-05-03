package com.gibbonsdimarco.yamec.app.config;

import jakarta.persistence.*;

import java.util.UUID;

/**
 * <p>A class/database table which controls the amount of time records of a specific
 * Granularity level take to age.</p>
 *
 * @implNote <p>The GranularityConfig schema is designed in this manner to separate the user-configurable
 * granularity settings of the application from the labels themselves, which are not user-configurable.</p>
 */
@Entity
@Table(name="granularity_config")
public class GranularityConfig {

    /**
     * The ID of the Granularity level this GranularityConfig is associated with
     */
    @Id
    @Column(name = "granularity_id")
    private UUID granularityId;

    /**
     * The Granularity entity this GranularityConfig is associated with
     */
    @OneToOne
    @MapsId
    @JoinColumn(name = "granularity_id")
    private Granularity granularity;

    /**
     * Defines the duration of metrics at this Granularity level (how much time their data represents)
     * by default
     */
    @Column(name="record_timespan", nullable=false)
    private Integer recordTimespan;

    /**
     * Defines the number of seconds it takes for metrics at this Granularity to age
     * (be moved to the next granularity level or deleted)
     */
    @Column(name="time_to_age")
    private Integer timeToAge;

    public GranularityConfig() {
    }

    public UUID getGranularityId() {
        return granularityId;
    }

    public void setGranularityId(UUID granularityId) {
        this.granularityId = granularityId;
    }

    public Granularity getGranularity() {
        return granularity;
    }

    public void setGranularity(Granularity granularity) {
        this.granularity = granularity;
    }

    public int getRecordTimespan() {
        return recordTimespan;
    }

    public void setRecordTimespan(int recordTimespan) {
        this.recordTimespan = recordTimespan;
    }

    public int getTimeToAge() {
        return timeToAge;
    }

    public void setTimeToAge(int timeToAge) {
        this.timeToAge = timeToAge;
    }
}
