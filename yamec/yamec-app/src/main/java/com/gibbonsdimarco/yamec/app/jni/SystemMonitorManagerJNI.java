package com.gibbonsdimarco.yamec.app.jni;
import com.github.fommil.jni.JniLoader;
import com.gibbonsdimarco.yamec.app.YamecApplication;

public class SystemMonitorManagerJNI {
    static {
        try {
            JniLoader.load("native/windows/x64/yamecjni.dll");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load: " + e);
        }
    }

    public native void sayHello();
}
