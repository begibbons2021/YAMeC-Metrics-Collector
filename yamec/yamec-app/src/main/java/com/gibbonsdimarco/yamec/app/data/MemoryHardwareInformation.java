package com.gibbonsdimarco.yamec.app.data;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Contains information about the system's memory hardware
 */
@Entity
@Table(name = "memory_hardware_information")
public class MemoryHardwareInformation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The number of bytes of memory present on the system
     *
     */
    @Column(name = "capacity", nullable = false)
    private long capacity;

    /**
     * The throughput of the system's memory in megatransfers per
     * second.
     */
    @Column(name = "speed", nullable = false)
    private long speed;

    /**
     * The number of slots for memory devices which are used in the system
     */
    @Column(name = "slots_used", nullable = false)
    private long slotsUsed;

    /**
     * The total number of available slots for memory devices in the system
     */
    @Column(name = "slots_total", nullable = false)
    private long slotsTotal;

    /**
     * Whether the memory capacity value should be interpreted as an unsigned value
     * (true) or not (false)
     */
    @Column(name = "capacity_is_unsigned")
    private boolean capacityIsUnsigned;

    /**
     * Whether the memory speed value should be interpreted as an unsigned value (true)
     * or not (false)
     */
    @Column(name = "speed_is_unsigned")
    private boolean speedIsUnsigned;

    /**
     * Default constructor for JPA
     */
    protected MemoryHardwareInformation() {
    }

    /**
     * Creates a MemoryHardwareInformation object with the field values passed by parameter
     *
     * @param capacity The amount of memory space in bytes
     * @param speed The operating speed of the memory in megatransfers per second
     * @param slotsUsed The number of slots used by memory devices
     * @param slotsTotal The number of total slots usable by memory devices
     * @param capacityIsUnsigned Whether the capacity should be treated as an unsigned variable
     * @param speedIsUnsigned Whether the speed should be treated as an unsigned variable
     */
    public MemoryHardwareInformation(long capacity, long speed, long slotsUsed, long slotsTotal,
                                     boolean capacityIsUnsigned, boolean speedIsUnsigned) {
        this.capacity = capacity;
        this.speed = speed;
        this.slotsUsed = slotsUsed;
        this.slotsTotal = slotsTotal;

        this.capacityIsUnsigned = capacityIsUnsigned;
        this.speedIsUnsigned = speedIsUnsigned;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getCapacity() {
        return capacity;
    }

    public String getCapacityAsUnsignedString() {
        return Long.toString(capacity);
    }

    public void setCapacity(long capacity) {
        this.capacity = capacity;
    }

    public long getSpeed() {
        return speed;
    }

    public String getSpeedAsUnsignedString() {
        return Long.toString(speed);
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public long getSlotsUsed() {
        return slotsUsed;
    }

    public void setSlotsUsed(long slotsUsed) {
        this.slotsUsed = slotsUsed;
    }

    public long getSlotsTotal() {
        return slotsTotal;
    }

    public boolean isCapacityUnsigned() {
        return capacityIsUnsigned;
    }

    public void setSlotsTotal(long slotsTotal) {
        this.slotsTotal = slotsTotal;
    }


    public void setCapacityIsUnsigned(boolean capacityIsUnsigned) {
        this.capacityIsUnsigned = capacityIsUnsigned;
    }

    public boolean isSpeedUnsigned() {
        return speedIsUnsigned;
    }

    public void setIsSpeedUnsigned(boolean speedIsUnsigned) {
        this.speedIsUnsigned = speedIsUnsigned;
    }
}
