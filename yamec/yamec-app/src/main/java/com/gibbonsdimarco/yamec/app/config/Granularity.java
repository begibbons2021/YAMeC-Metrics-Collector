package com.gibbonsdimarco.yamec.app.config;

import jakarta.persistence.*;

import java.util.UUID;

/**
 * A class/database table which indicates the level of granularity of a record
 */
@Entity
@Table(name = "granularity", indexes = {
        @Index(name = "idx_granularity_label", columnList = "label")
})
public class Granularity {

    /**
     * <p>A unique identifier for this Granularity level.</p>
     *
     * @implNote <p>Since granularity level cannot be set by users,
     * the ID is set with an integer rather than a UUID and is simply
     * assigned a value upon creation. This also makes it easier to
     * associate granularity configurations with specific levels.</p>
     */
    @Id
    @Column(name = "id")
    private long id;

    @OneToOne(mappedBy = "granularity", cascade=CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private GranularityConfig granularityConfig;

    /**
     * A string of up to 255 characters in length acting as a label
     * for this Granularity level
     */
    @Column(name="label", length = 255, nullable = false, unique = true)
    private String label;

    /**
     * No-Args constructor required by JPA to allow reflection
     */
    public Granularity() {

    }

    /**
     * Creates a Granularity level with the label passed by parameter
     * @param label The label for this Granularity level
     */
    public Granularity(String label) {
        this.label = label;
    }

    /**
     * Returns the unique identifier for this Granularity level
     * @return This Granularity level's ID
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the value of this Granularity level
     * @param id This Granularity level's ID
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Returns the label associated with this Granularity level
     * @return The label of this Granularity level as a string
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label associated with this Granularity level
     * @param label The label to associate with this Granularity level
     */
    public void setLabel(String label) {
        this.label = label;
    }

    public GranularityConfig getGranularityConfig() {
        return granularityConfig;
    }

    public void setGranularityConfig(GranularityConfig granularityConfig) {
        this.granularityConfig = granularityConfig;
    }
}
