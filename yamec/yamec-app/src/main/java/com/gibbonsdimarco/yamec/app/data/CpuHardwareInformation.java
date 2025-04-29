package com.gibbonsdimarco.yamec.app.data;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.UUID;
/**
 * Contains hardware information for CPUs connected to the System
 */
@Entity
@Table(name = "cpu_hardware_information")
public class CpuHardwareInformation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * The name of the CPU this CpuHardwareInformation object pertains to as a human-readable
     * string.
     * <br><br>
     * This is usually derived from the brand string of the CPU.
     */
    @Column(name = "name", nullable = false)
    private String friendlyName;

    /**
     * The number of physical cores (separate processors) which are present on the CPU
     *  this CpuHardwareInformation object pertains to
     */
    @Column(name = "cores")
    private long coreCount;

    /**
     * The number of logical processors which are supported on the CPU this
     * CpuHardwareInformation object pertains to.
     * <br>
     * Logical processors are usually facilitated through a technology such as
     *   AMD Simultaneous Multithreading, SMT, or Intel Hyperthreading
     */
    @Column(name = "logical_processors")
    private long logicalProcessorCount;

    /**
     * The architecture string of the CPU this CpuHardwareInformation object
     * pertains to (i.e., x86, ARM, x64, etc.)
     */
    @Transient
    private String architecture;

    /**
     * The number of NUMA node groupings of CPU and memory present in the system
     * and related to the CPU this CpuHardwareInformation object pertains to
     */
    @Transient
    private long numaNodeCount;

    /**
     * The number of bytes of level 1 cache present in the CPU this
     * CpuHardwareInformation object pertains to
     */
    @Column(name = "l1_cache_size")
    private long l1CacheSize;

    /**
     * The number of bytes of level 2 cache present in the CPU this
     * CpuHardwareInformation object pertains to
     */
    @Column(name = "l2_cache_size")
    private long l2CacheSize;

    /**
     * The number of bytes of level 3 cache present in the CPU this
     * CpuHardwareInformation object pertains to
     */
    @Column(name = "l3_cache_size")
    private long l3CacheSize;

    /**
     * Whether the CPU this CpuHardwareInformation object pertains to support
     * and has enabled virtualization technology (i.e., Windows Sandbox,
     * Windows HyperV, etc.)
     */
    @Column(name = "virtualization")
    private boolean virtualizationEnabled;

    protected CpuHardwareInformation() {}

    /**
     * Creates a new CpuHardwareInformation object instance from the hardware
     * information passed by parameter
     * @param friendlyName The name of the system's CPU as a String
     * @param coreCount The number of cores present in the CPU
     * @param logicalProcessorCount The number of threads supported by the CPU
     * @param architecture The architecture of the CPU as a String
     * @param numaNodeCount The number of NUMA nodes (groupings of CPU and Memory)
     *                      present in the CPU
     * @param l1CacheSize The number of bytes of level 1 cache in the CPU
     * @param l2CacheSize The number of bytes of level 2 cache in the CPU
     * @param l3CacheSize The number of bytes of level 3 cache in the CPU
     * @param virtualizationEnabled A boolean value representing whether hardware
     *                              virtualization technology is supported by
     *                              the CPU
     */
    public CpuHardwareInformation(String friendlyName,
                                  long coreCount, long logicalProcessorCount,
                                  String architecture,
                                  long numaNodeCount,
                                  long l1CacheSize, long l2CacheSize, long l3CacheSize,
                                  boolean virtualizationEnabled) {
        this.friendlyName = friendlyName;
        this.coreCount = coreCount;
        this.logicalProcessorCount = logicalProcessorCount;
        this.architecture = architecture;
        this.numaNodeCount = numaNodeCount;
        this.l1CacheSize = l1CacheSize;
        this.l2CacheSize = l2CacheSize;
        this.l3CacheSize = l3CacheSize;
        this.virtualizationEnabled = virtualizationEnabled;
    }

    /**
     * Retrieves the assigned ID of this Application
     * @return The UUID of this Application
     */
    public UUID getId() {
        return id;
    }

    /**
     * Sets the ID of this Application to the ID passed by parameter
     * @param id A UUID for this Application
     */
    public void setId(UUID id) {
        this.id = id;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public long getCoreCount() {
        return coreCount;
    }

    public long getLogicalProcessorCount() {
        return logicalProcessorCount;
    }

    public String getArchitecture() {
        return architecture;
    }

    public long getNumaNodeCount() {
        return numaNodeCount;
    }

    public long getL1CacheSize() {
        return l1CacheSize;
    }

    public long getL2CacheSize() {
        return l2CacheSize;
    }

    public long getL3CacheSize() {
        return l3CacheSize;
    }

    public boolean isVirtualizationEnabled() {
        return virtualizationEnabled;
    }

    @Override
    public String toString() {
        return String.format(
                "Cpu[id=%s, friendlyName='%s', coreCount='%s', logicalProcessorCount='%s', l1CacheSize='%s', l2CacheSize='%s', l3CacheSize='%s', virtualizationEnabled='%s']",
                id, friendlyName, coreCount, logicalProcessorCount, l1CacheSize, l2CacheSize, l3CacheSize, virtualizationEnabled);
    }
}
