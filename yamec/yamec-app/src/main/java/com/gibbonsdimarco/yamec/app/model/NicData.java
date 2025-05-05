package com.gibbonsdimarco.yamec.app.model;

import java.util.UUID;

public class NicData {
    private UUID nicId;
    private String friendlyName;
    private String label;
    private String nicType;
    private long avgNetworkSent;
    private long maxNetworkSent;
    private long minNetworkSent;
    private long avgNetworkReceived;
    private long maxNetworkReceived;
    private long minNetworkReceived;

    public NicData() {
        this.nicId = null;
        this.friendlyName = "";
        this.label = "";
        this.nicType = "";
        this.avgNetworkSent = 0;
        this.maxNetworkSent = 0;
        this.minNetworkSent = 0;
        this.avgNetworkReceived = 0;
        this.maxNetworkReceived = 0;
        this.minNetworkReceived = 0;
    }

    public NicData(String friendlyName, String label, String nicType, long networkSent, long networkReceived) {
        this.nicId = null;
        this.friendlyName = friendlyName;
        this.label = label;
        this.nicType = nicType;
        this.avgNetworkSent = networkSent;
        this.avgNetworkReceived = networkReceived;
    }

    public NicData(UUID nicId,
                   String friendlyName,
                   String label,
                   String nicType,
                   long networkSent,
                   long networkReceived,
                   long maxNetworkSent,
                   long minNetworkSent,
                   long maxNetworkReceived,
                   long minNetworkReceived) {
        this.nicId = nicId;
        this.friendlyName = friendlyName;
        this.label = label;
        this.nicType = nicType;
        this.avgNetworkSent = networkSent;
        this.avgNetworkReceived = networkReceived;
        this.maxNetworkSent = maxNetworkSent;
        this.minNetworkSent = minNetworkSent;
        this.maxNetworkReceived = maxNetworkReceived;
        this.minNetworkReceived = minNetworkReceived;
    }

    public UUID getNicId() {
        return nicId;
    }

    public void setNicId(UUID nicId) {
        this.nicId = nicId;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getNicType() {
        return nicType;
    }

    public void setNicType(String nicType) {
        this.nicType = nicType;
    }

    public long getAvgNetworkSent() {
        return avgNetworkSent;
    }

    public void setAvgNetworkSent(long avgNetworkSent) {
        this.avgNetworkSent = avgNetworkSent;
    }

    public long getMaxNetworkSent() {
        return maxNetworkSent;
    }

    public void setMaxNetworkSent(long maxNetworkSent) {
        this.maxNetworkSent = maxNetworkSent;
    }

    public long getMinNetworkSent() {
        return minNetworkSent;
    }

    public void setMinNetworkSent(long minNetworkSent) {
        this.minNetworkSent = minNetworkSent;
    }

    public long getAvgNetworkReceived() {
        return avgNetworkReceived;
    }

    public void setAvgNetworkReceived(long avgNetworkReceived) {
        this.avgNetworkReceived = avgNetworkReceived;
    }

    public long getMaxNetworkReceived() {
        return maxNetworkReceived;
    }

    public void setMaxNetworkReceived(long maxNetworkReceived) {
        this.maxNetworkReceived = maxNetworkReceived;
    }

    public long getMinNetworkReceived() {
        return minNetworkReceived;
    }

    public void setMinNetworkReceived(long minNetworkReceived) {
        this.minNetworkReceived = minNetworkReceived;
    }
}

