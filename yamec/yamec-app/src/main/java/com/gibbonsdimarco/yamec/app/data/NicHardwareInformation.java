package com.gibbonsdimarco.yamec.app.data;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.UUID;

/**
 * Contains hardware information for Network Interface Cards (NICs) connected to the System
 */
@Entity
@Table(name = "nic_hardware_information")
public class NicHardwareInformation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * The friendly name of the NIC device associated with this NicHardwareInformation object as
     * a human-readable string
     * <br>
     * Note: In WMI, this is actually the "Interface Description"
     */
    @Column(name = "friendly_name", nullable = false)
    private String friendlyName;

    /**
     * A string which represents how the underlying operating system
     * identifies the NIC device associated with this
     * NicHardwareInformation object.
     * <br>
     * The label depends on the operating system. On Windows, this
     * is the "Name" field in the MSFT_NetAdapter object type, and
     * can be something like "Ethernet 3". On Linux, a similar label
     * would be something like "eth3"
     *
     */
    @Column(name = "label", nullable = false)
    private String label;

    /**
     * The locally unique hardware ID assigned to the NIC Device this NicHardwareInformation object pertains to
     */
    @Column(name = "unique_id", nullable = false)
    private String uniqueId;

    /**
     * A positive long integer representation of the interface type of the NIC Device this
     * NicHardwareInformation object pertains to
     * <br>
     *
     * The numbers utilized to represent different types of NIC Devices are based
     * on the IEEE ifType definition
     *
     * @link <a href="https://www.iana.org/assignments/ianaiftype-mib/ianaiftype-mib">
     *     IANA definition describing the ifType (Interface Type) integer values and
     *     their mappings to different types of network adapters
     *     </a>
     */
    @Column(name = "nic_type", nullable = false)
    private long nicType;

    /**
     * Default constructor for JPA
     */
    protected NicHardwareInformation() {
    }

    /**
     *
     * @param friendlyName A string containing the human-readable name of the NIC Device this NicHardwareInformation
     *                     object pertains to
     * @param label A string containing an identifier used by the system to represent the NIC Device this
     *              NicHardwareInformation object pertains to
     * @param uniqueId A string containing the locally unique hardware ID assigned to the NIC Device this
     *                 NicHardwareInformation instance pertains to
     * @param nicType A positive long integer representing the type of the NIC Device this NicHardwareInformation
     *                 instance pertains to
     *
     * @throws IllegalArgumentException If the NicType is a negative number.
     */
    public NicHardwareInformation(String friendlyName, String label, String uniqueId, long nicType) {
        this.friendlyName = friendlyName;
        this.label = label;
        this.uniqueId = uniqueId;

        if (nicType < 0) {
            throw new IllegalArgumentException("NIC Type should be a positive number, but is "
                    + nicType + " instead (32-bit unsigned value).");
        }
        this.nicType = nicType;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public String getLabel() {
        return label;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public long getNicType() {
        return nicType;
    }

    /**
     * Returns the IEEE NIC interface type passed by parameter as its string equivalent.
     *
     * @param nicType An integer representing the interface type of a NicHardwareInformation object
     *                  or NIC device.
     *
     * @return A string representation of the NIC type passed by parameter
     *
     * @see NicHardwareInformation#nicType
     */
    public static String getNicTypeString(long nicType) {
        // Switch doesn't support long but we need 32 bit unsigned value

        if (nicType == NicType.ETHERNET.getValue()
            || nicType == NicType.ETHERNET_3_MEGABIT.getValue()
            || nicType == NicType.FAST_ETHERNET_FX.getValue()
            || nicType == NicType.FAST_ETHERNET_T.getValue()
            || nicType == NicType.GIGABIT_ETHERNET.getValue()) {
            return "Ethernet";
        }
        else if (nicType == NicType.WIFI.getValue()) {
            return "Wi-Fi (802.11)";
        }
        else if (nicType == NicType.LOOPBACK.getValue()) {
            return "Loopback Device";
        }
        else if (nicType == NicType.TOKEN_RING.getValue()) {
            return "Token Ring";
        }
        else if (nicType == NicType.WIMAX.getValue()) {
            return "WiMax";
        }
        else if (nicType == NicType.CELLULAR_GSM.getValue()
                    || nicType == NicType.CELLULAR_CDMA.getValue()) {
            return "Cellular (GSM/CDMA)";
        }
        else if (nicType == NicType.TUNNEL.getValue()) {
            return "Tunnel";
        }
        else if (nicType == NicType.MODEM.getValue()) {
            return "Modem (Dial-Up)";
        }
        else if (nicType == NicType.DSL_ASYMMETRIC.getValue()
                    || nicType == NicType.DSL_SYMMETRIC.getValue()
                    || nicType == NicType.DSL_RATE_ADAPTIVE.getValue()
                    || nicType == NicType.DSL_MULTI_RATE_SYMMETRICAL.getValue()
                    || nicType == NicType.DSL_VERY_HIGH_SPEED.getValue()) {
            return "DSL (Digital Subscriber Line)";
        }
        else {
            return "Unknown/Other";
        }

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
