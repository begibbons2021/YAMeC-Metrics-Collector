package com.gibbonsdimarco.yamec.app.jni;
import com.gibbonsdimarco.yamec.app.data.*;
import com.github.fommil.jni.JniLoader;

public class SystemMonitorManagerJNI {
    static {
        try {
            JniLoader.load("native/windows/x64/yamecjni.dll");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load: " + e);
        }
    }

    public native void sayHello();

    public native boolean initialize();

    public native SystemCpuMetric getCpuMetrics();

    public native SystemGpuMetric getGpuMetrics();

    public native java.util.ArrayList<SystemMemoryMetric> getMemoryMetrics();

    public native java.util.ArrayList<SystemDiskMetric> getDiskMetrics();

    public native java.util.ArrayList<SystemNicMetric> getNicMetrics();


}
