package com.gibbonsdimarco.yamec.app.data;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/**
 * Enum representing the different types of network interfaces
 * 
 * @link <a href="https://www.iana.org/assignments/ianaiftype-mib/ianaiftype-mib">
 *     IANA definition describing the ifType (Interface Type) integer values and
 *     their mappings to different types of network adapters
 *     </a>
 *
 * @link <a href="https://learn.microsoft.com/en-us/dotnet/api/system.net.networkinformation.networkinterfacetype">
 *     Microsoft .NET framework definitions of Network Interface Types
 *     </a>
 */
public enum NicType {
    UNKNOWN(1),
    ETHERNET(6),
    TOKEN_RING(9),
    FDDI(15),
    BASIC_ISDN(20),
    PRIMARY_ISDN(21),
    PPP(23),
    LOOPBACK(24),
    ETHERNET_3_MEGABIT(26),
    SLIP(28),
    ATM(37),
    MODEM(48),
    FAST_ETHERNET_T(62),
    ISDN(63),
    FAST_ETHERNET_FX(69),
    WIFI(71),
    DSL_ASYMMETRIC(94),
    DSL_RATE_ADAPTIVE(95),
    DSL_SYMMETRIC(96),
    DSL_VERY_HIGH_SPEED(97),
    IP_OVER_ATM(114),
    GIGABIT_ETHERNET(117),
    TUNNEL(131),
    DSL_MULTI_RATE_SYMMETRICAL(143),
    HIGH_PERFORMANCE_SERIAL_BUS(144),
    WIMAX(237),
    CELLULAR_GSM(243),
    CELLULAR_CDMA(244);

    private final long value;

    NicType(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public static NicType fromValue(long value) {
        for (NicType type : NicType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
