package com.gibbonsdimarco.yamec.app.data;

public class MemoryHardwareInformation {

    /**
     * The number of bytes of memory present on the system
     *
     */
    private long capacity;

    /**
     * The throughput of the system's memory in megatransfers per
     * second.
     */
    private long speed;

    /**
     * The number of slots for memory devices which are used in the system
     */
    private long slotsUsed;

    /**
     * The total number of available slots for memory devices in the system
     */
    private long slotsTotal;

    /**
     * Whether the memory capacity value should be interpreted as an unsigned value
     * (true) or not (false)
     */
    private boolean capacityIsUnsigned;

    /**
     * Whether the memory speed value should be interpreted as an unsigned value (true)
     * or not (false)
     */
    private boolean speedIsUnsigned;

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


    public long getCapacity() {
        return capacity;
    }

    public long getSpeed() {
        return speed;
    }

    public long getSlotsUsed() {
        return slotsUsed;
    }

    public long getSlotsTotal() {
        return slotsTotal;
    }

    public boolean isCapacityIsUnsigned() {
        return capacityIsUnsigned;
    }

    public boolean isSpeedIsUnsigned() {
        return speedIsUnsigned;
    }
}
