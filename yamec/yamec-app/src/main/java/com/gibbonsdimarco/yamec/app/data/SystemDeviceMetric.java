package com.gibbonsdimarco.yamec.app.data;

public abstract class SystemDeviceMetric {

    /**
     * The name of this SystemDeviceMetric's source hardware device
     */
    private String deviceName;

    public SystemDeviceMetric(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceName() {
        return deviceName;
    }

}
