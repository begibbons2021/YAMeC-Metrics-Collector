package com.gibbonsdimarco.yamec.app.jni;

import com.github.fommil.jni.JniLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemMonitorManagerJNI {
    private static final Logger logger = LoggerFactory.getLogger(SystemMonitorManagerJNI.class);

    static {
        try {
            JniLoader.load("native/windows/x64/yamecjni.dll");
            initLogger(logger);
        } catch (UnsatisfiedLinkError e) {
            logger.error("Native code library failed to load", e);
        }
    }

    private static native void initLogger(Object javaLogger);
    public native void sayHello();
}
