package com.gibbonsdimarco.yamec.app.data;

import jakarta.persistence.*;

import java.io.Serializable;

/**
 * Contains hardware information for CPUs connected to the System
 */
@Entity
@Table(name = "cpu", indexes = {
        @Index(name = "idx_device_name", columnList = "device_name")
})
public class CpuHardwareInformation implements Serializable {

    /**
     * A unique identifier for use in the database to represent this Cpu on the system
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * <p>The name of the CPU this CpuHardwareInformation object pertains to as a human-readable
     * string.</p>
     *
     * <p>This is usually derived from the brand string of the CPU</p>
     */
    @Column(name = "device_name", nullable = false)
    private String friendlyName;

    /**
     * The number of physical cores (separate processors) which are present on the CPU
     *  this CpuHardwareInformation object pertains to
     */
    @Column(name = "num_cores", nullable = false)
    private long coreCount;

    /**
     * <p>The number of logical processors which are supported on the CPU this
     * CpuHardwareInformation object pertains to.</p>
     *
     * <p>Logical processors are usually facilitated through a technology such as
     *   AMD Simultaneous Multithreading, SMT, or Intel Hyperthreading</p>
     */
    @Column(name = "num_logical_processors", nullable = false)
    private long logicalProcessorCount;

    /**
     * The architecture string of the CPU this CpuHardwareInformation object
     * pertains to (i.e., x86, ARM, x64, etc.)
     */
    @Column(name = "architecture", nullable = false)
    private String architecture;

    /**
     * The number of NUMA node groupings of CPU and memory present in the system
     * and related to the CPU this CpuHardwareInformation object pertains to
     */
    @Column(name = "num_numa_nodes", nullable = false)
    private long numaNodeCount;

    /**
     * The number of bytes of level 1 cache present in the CPU this
     * CpuHardwareInformation object pertains to
     */
    @Column(name = "l1_cache_size", nullable = false)
    private long l1CacheSize;

    /**
     * The number of bytes of level 2 cache present in the CPU this
     * CpuHardwareInformation object pertains to
     */
    @Column(name = "l2_cache_size", nullable = false)
    private long l2CacheSize;


    /**
     * The amount of bytes of level 3 cache present in the CPU this
     * CpuHardwareInformation object pertains to
     */
    @Column(name = "l3_cache_size", nullable = false)
    private long l3CacheSize;

    /**
     * Whether the CPU this CpuHardwareInformation object pertains to support
     * and has enabled virtualization technology (i.e., Windows Sandbox,
     * Windows HyperV, etc.)
     */
    @Column(name = "virtualization_enabled", nullable = false)
    private boolean virtualizationEnabled;

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
     * No-Args constructor required by JPA to allow reflection
     */
    public CpuHardwareInformation() {

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

    public void setId(Long id) {
        this.id = id;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public void setCoreCount(long coreCount) {
        this.coreCount = coreCount;
    }

    public void setLogicalProcessorCount(long logicalProcessorCount) {
        this.logicalProcessorCount = logicalProcessorCount;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public void setNumaNodeCount(long numaNodeCount) {
        this.numaNodeCount = numaNodeCount;
    }

    public void setL1CacheSize(long l1CacheSize) {
        this.l1CacheSize = l1CacheSize;
    }

    public void setL2CacheSize(long l2CacheSize) {
        this.l2CacheSize = l2CacheSize;
    }

    public void setL3CacheSize(long l3CacheSize) {
        this.l3CacheSize = l3CacheSize;
    }

    public void setVirtualizationEnabled(boolean virtualizationEnabled) {
        this.virtualizationEnabled = virtualizationEnabled;
    }
}
