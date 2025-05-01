package com.gibbonsdimarco.yamec.app.data;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Contains hardware information for Disk Devices connected to the System
 *
 */
@Entity
@Table(name = "disk_hardware_information", indexes = {
        @Index(name = "idx_disk_unique_id", columnList = "unique_id"),
        @Index(name = "idx_disk_number", columnList = "disk_number")
})
public class DiskHardwareInformation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * The name of the Disk Device this DiskHardwareInformation object pertains to as a human-readable
     * string
     */
    @Column(name = "friendly_name", nullable = false)
    private String friendlyName;

    /**
     * The locally unique hardware ID assigned to the Disk Device this DiskHardwareInformation object pertains to
     */
    @Column(name = "unique_id", nullable = false)
    private String uniqueId;

    /**
     * The positive number associated with the Disk Device this DiskHardwareInformation object pertains to
     */
    @Column(name = "disk_number", nullable = false)
    private long diskNumber;

    /**
     * A positive long integer representation of the media type of the Disk Device this DiskHardwareInformation
     * object pertains to
     * <br>
     *
     * The numbers utilized to represent different types of Disk Devices are based
     * on the WMI type mappings from the WMI MSFT_PhysicalDisk object class
     * @see <a href="https://learn.microsoft.com/en-us/windows-hardware/drivers/storage/msft-physicaldisk">
     *          WMI MSFT_PhysicalDisk</a>}
     */
    @Column(name = "media_type", nullable = false)
    private long mediaType;

    /**
     * The amount of storage, in bytes, of the Disk Device this DiskHardwareInformation object pertains to
     */
    @Column(name = "capacity", nullable = false)
    private long capacity;

    /**
     * Whether this DiskHardwareInformation object's storage capacity should be treated
     * as an unsigned value (true) or not (false)
     */
    @Column(name = "capacity_is_unsigned")
    private boolean capacityIsUnsigned;

    /**
     * The partitions (drive letters, system paths, etc.) associated
     * with the Disk Device this DiskHardwareInformation object pertains to
     */
    @ElementCollection
    @CollectionTable(name = "disk_partitions", joinColumns = @JoinColumn(name = "disk_id"))
    @Column(name = "partition")
    private List<String> partitions;

    /**
     * Default constructor for JPA
     */
    protected DiskHardwareInformation() {
    }

    /**
     *
     * @param friendlyName A string containing the human-readable name of the DiskDevice this DiskHardwareInformation
     *                     object pertains to
     * @param uniqueId A string containing the locally unique hardware ID assigned to the Disk Device this
     *                 DiskHardwareInformation instance pertains to
     * @param diskNumber A positive long integer containing the
     * @param mediaType A positive long integer representing the type of the storage medium
     * @param capacity A long integer of the storage capacity of the drive, in bytes
     * @param capacityIsUnsigned A boolean representing whether capacity is unsigned (true) or not (false)
     * @param partitions An ArrayList containing all partitions (drive names, drive paths, etc.) associated with the
     *                   DiskDevice this DiskHardwareInformation object pertains to
     *
     * @throws IllegalArgumentException If the MediaType or DiskNumber is a negative number.
     */
    public DiskHardwareInformation(String friendlyName,
                                   String uniqueId,
                                   long diskNumber,
                                   long mediaType,
                                   long capacity,
                                   boolean capacityIsUnsigned,
                                   java.util.ArrayList<String> partitions) {
        this.friendlyName = friendlyName;
        this.uniqueId = uniqueId;
        if (diskNumber < 0)
        {
            throw new IllegalArgumentException("Disk Number should be a positive number, but is "
                                                + diskNumber + " instead (32-bit unsigned value).");
        }
        this.diskNumber = diskNumber;
        if (mediaType < 0)
        {
            throw new IllegalArgumentException("Media Type should be a positive number, but is "
                                                + mediaType + " instead (32-bit unsigned value).");
        }
        this.mediaType = mediaType;
        this.capacity = capacity;
        this.capacityIsUnsigned = capacityIsUnsigned;
        this.partitions = partitions;
    }

    /**
     * Returns the media type passed by parameter as its string equivalent.
     *
     * @param mediaType An integer representing the media type of a DiskHardwareInformation object
     *                  or disk device.
     *
     * @return A string representation of the media type passed by parameter
     * 
     * @see DiskHardwareInformation#mediaType
     */
    public static String getMediaTypeString(long mediaType) {
        // Switch doesn't support long but we need 32 bit unsigned value

        if (mediaType == MediaType.HDD.getValue()) {
            return "HDD";
        }
        else if (mediaType == MediaType.SSD.getValue()) {
            return "SSD";
        }
        else if (mediaType == MediaType.SCM.getValue()) {
            return "SCM";
        }
        else if (mediaType == MediaType.UNSPECIFIED.getValue()) {
            return "Unspecified";
        }
        else {
            return "Unknown/Other";
        }

    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public long getDiskNumber() {
        return diskNumber;
    }

    public long getMediaType() {
        return mediaType;
    }

    public long getCapacity() {
        return capacity;
    }

    /**
     * Returns the capacity of the Disk Device associated with this DiskHardwareInformation
     * instance as an unsigned value represented as a String
     *
     * @return A string containing the unsigned value of the Disk Device's storage capacity
     */
    public String getCapacityAsUnsignedString() {
        return Long.toString(capacity);
    }

    public boolean isCapacityUnsigned() {
        return capacityIsUnsigned;
    }

    public List<String> getPartitions() {
        // Since Strings are immutable, this is safe
        return new ArrayList<>(partitions);
    }

    public void setPartitions(List<String> partitions) {
        this.partitions.clear();
        this.partitions.addAll(partitions);
    }

}
