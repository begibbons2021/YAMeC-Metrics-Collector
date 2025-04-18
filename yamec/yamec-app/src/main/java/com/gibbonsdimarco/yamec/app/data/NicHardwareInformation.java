package com.gibbonsdimarco.yamec.app.data;

public class NicHardwareInformation {

    /**
     * The friendly name of the NIC device associated with this NicHardwareInformation object as
     * a human-readable string
     * <br>
     * Note: In WMI, this is actually the "Interface Description"
     */
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
    private String label;

    /**
     * The locally unique hardware ID assigned to the Disk Device this DiskHardwareInformation object pertains to
     */
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
    private long nicType;

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

        if (nicType == NicType.ETHERNET
            || nicType == NicType.ETHERNET_3_MEGABIT
            || nicType == NicType.FAST_ETHERNET_FX
            || nicType == NicType.FAST_ETHERNET_T
            || nicType == NicType.GIGABIT_ETHERNET) {
            return "Ethernet";
        }
        else if (nicType == NicType.WIFI) {
            return "Wi-Fi (802.11)";
        }
        else if (nicType == NicType.LOOPBACK) {
            return "Loopback Device";
        }
        else if (nicType == NicType.TOKEN_RING) {
            return "Token Ring";
        }
        else if (nicType == NicType.WIMAX) {
            return "WiMax";
        }
        else if (nicType == NicType.CELLULAR_GSM
                    || nicType == NicType.CELLULAR_CDMA) {
            return "Cellular (GSM/CDMA)";
        }
        else if (nicType == NicType.TUNNEL) {
            return "Tunnel";
        }
        else if (nicType == NicType.MODEM) {
            return "Modem (Dial-Up)";
        }
        else if (nicType == NicType.DSL_ASYMMETRIC
                    || nicType == NicType.DSL_SYMMETRIC
                    || nicType == NicType.DSL_RATE_ADAPTIVE
                    || nicType == NicType.DSL_MULTI_RATE_SYMMETRICAL
                    || nicType == NicType.DSL_VERY_HIGH_SPEED) {
            return "DSL (Digital Subscriber Line)";
        }
        else {
            return "Unknown/Other";
        }

    }

}
