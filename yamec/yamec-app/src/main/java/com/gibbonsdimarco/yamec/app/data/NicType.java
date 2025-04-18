package com.gibbonsdimarco.yamec.app.data;

/**
 *
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
public class NicType {

    public final static long UNKNOWN = 1;
    public final static long ETHERNET = 6;
    public final static long TOKEN_RING = 9;
    /**Fiber Distributed Data Interface*/
    public final static long FDDI = 15;
    /**Integrated Services Digital Network (ISDN)*/
    public final static long BASIC_ISDN = 20;
    /**Primary rate as opposed to basic rate*/
    public final static long PRIMARY_ISDN = 21;
    /**Point-to-Point Protocol*/
    public final static long PPP = 23;
    public final static long LOOPBACK = 24;

    /**
     *  (definition incorporated into Ethernet - 6)
     */
    public final static long ETHERNET_3_MEGABIT = 26;

    /**
     * Serial Line Internet Protocol (IETF RFC 1055)
     */
    public final static long SLIP = 28;
    /**
     * Asynchronous Transfer Mode
     */
    public final static long ATM = 37;
    public final static long MODEM = 48;
    /**
     * Fast Ethernet over a Twisted Pair copper cable
     *  (definition incorporated into Ethernet - 6)
     */
    public final static long FAST_ETHERNET_T = 62;
    /**
     * ISDN and X.25 (Allows communication between computers
     * on a public network using an intermediary computer)
     */
    public final static long ISDN = 63;

    /**
     * Fast Ethernet over a Fiber Optic cable (definition incorporated into Ethernet - 6)
     */
    public final static long FAST_ETHERNET_FX = 69;

    /**
     * 802.11 Wireless
     */
    public final static long WIFI = 71;

    public final static long DSL_ASYMMETRIC = 94;
    public final static long DSL_RATE_ADAPTIVE = 95;
    public final static long DSL_SYMMETRIC = 96;
    public final static long DSL_VERY_HIGH_SPEED = 97;

    /**
     * Asynchronous Transfer Mode using IP
     */
    public final static long IP_OVER_ATM = 114;

    /**
     *  (definition incorporated into Ethernet - 6)
     */
    public final static long GIGABIT_ETHERNET = 117;

    public final static long TUNNEL = 131;
    public final static long DSL_MULTI_RATE_SYMMETRICAL = 143;
    public final static long HIGH_PERFORMANCE_SERIAL_BUS = 144;
    /**
     * Wman - WiMax Mobile Broadband
     */
    public final static long WIMAX = 237;

    /**
     * Wwanpp - GSM Mobile Broadband
     */
    public final static long CELLULAR_GSM = 243;

    /**
     * Wwanpp2 - CDMA Mobile Broadband
     */
    public final static long CELLULAR_CDMA = 244;

}
