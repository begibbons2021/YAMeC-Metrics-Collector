package com.gibbonsdimarco.yamec.app.jni;
import com.gibbonsdimarco.yamec.app.data.*;
import com.github.fommil.jni.JniLoader;
import jakarta.websocket.OnClose;

public class SystemMonitorManagerJNI implements java.io.Closeable {
    static {
        try {
            JniLoader.load("native/windows/x64/yamecjni.dll");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load: " + e);
        }
    }


    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Fields/Variables
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private long monitorAddress = -1;
    private boolean closed = false;

    /**
     * Instantiates the System Monitor Manager.
     *
     * @throws RuntimeException If the System Monitor Manager cannot be instantiated
     * via the JNI call to C++.
     *
     */
    public SystemMonitorManagerJNI()
    {
        long ptrAddress = initialize();

        if (ptrAddress == -1)
        {
            throw new RuntimeException("Unable to initialize system monitor manager");
        }

        this.monitorAddress = ptrAddress;
    }

    /**
     * Returns a boolean variable containing whether this SystemMonitorManager is closed
     *
     * @return True if this instance is closed; otherwise, this returns false
     */
    public boolean isClosed() {
        return closed;
    }


    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Function Calls
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Releases the memory allocated to the native SystemMonitorManager
     * and stops further use of this class
     *
     * @throws RuntimeException if the resource cannot be closed
     */
    @OnClose
    public void close() {
        if (closed) {
            return;
        }

        if (this.release(this.monitorAddress)) {
            closed = true;
        }
        else {
            throw new RuntimeException("Unable to release system monitor manager memory");
        }

    }

    /**
     * Retrieves the current system metrics for the system's CPU devices
     *
     * @return A SystemCpuMetric object containing the system's CPU information.
     * If the CPU metrics cannot be retrieved, this returns null.
     */
    public SystemCpuMetric getCpuMetrics() {
        if (closed) {
            return null;
        }

        return getCpuMetrics(this.monitorAddress);
    }

    /**
     * Retrieves the current system metrics for the system's GPU devices
     *
     * @return A SystemGpuMetric object containing the system's GPU information.
     * If the GPU metrics cannot be retrieved, this returns null.
     */
    public SystemGpuMetric getGpuMetrics() {
        if (closed) {
            return null;
        }

        return getGpuMetrics(this.monitorAddress);
    }

    /**
     * Retrieves the current system metrics for the system's primary memory
     *
     * @return A SystemMemoryMetric object containing the system's memory information.
     * If the memory metrics cannot be retrieved, this returns null.
     */
    public SystemMemoryMetric getMemoryMetrics() {
        if (closed) {
            return null;
        }

        return getMemoryMetrics(this.monitorAddress);
    }

    public java.util.ArrayList<SystemDiskMetric> getDiskMetrics() {
        if (closed) {
            return null;
        }

        return getDiskMetrics(this.monitorAddress);
    }

    public java.util.ArrayList<SystemNicMetric> getNicMetrics() {
        return null;
    }


    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Native Calls
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * A test function which prints "Hello World" from native code
     */
    public native void sayHello();

    /**
     * Instantiates the native SystemMonitorManager object and returns the
     * address which the SystemMonitorManager is stored at as a long
     * integer.
     * <p>
     * If the instance cannot be initialized, this returns -1
     *
     * @return A long containing the memory address of the native object
     * created, or -1 if creation fails
     */
    private native long initialize();

    private native SystemCpuMetric getCpuMetrics(long ptr);

    private native SystemGpuMetric getGpuMetrics(long ptr);

    private native SystemMemoryMetric getMemoryMetrics(long ptr);

    private native java.util.ArrayList<SystemDiskMetric> getDiskMetrics(long ptr);

//    private native java.util.ArrayList<SystemNicMetric> getNicMetrics(long ptr);

    /**
     * Releases the memory allocated to the SystemMonitorManager
     *
     * @param ptr The memory address where the SystemMonitorManager Native object
     *            is stored
     * @return True if the memory was released successfully; otherwise, this returns false
     */
    private native boolean release(long ptr);

}

/*
 * Signatures:
 * public class com.gibbonsdimarco.yamec.app.jni.SystemMonitorManagerJNI {
 *   public com.gibbonsdimarco.yamec.app.jni.SystemMonitorManagerJNI();
 *     descriptor: ()V
 *
 *   public boolean isClosed();
 *     descriptor: ()Z
 *
 *   public boolean close();
 *     descriptor: ()Z
 *
 *   public com.gibbonsdimarco.yamec.app.data.SystemCpuMetric getCpuMetrics();
 *     descriptor: ()Lcom/gibbonsdimarco/yamec/app/data/SystemCpuMetric;
 *
 *   public com.gibbonsdimarco.yamec.app.data.SystemGpuMetric getGpuMetrics();
 *     descriptor: ()Lcom/gibbonsdimarco/yamec/app/data/SystemGpuMetric;
 *
 *   public java.util.ArrayList<com.gibbonsdimarco.yamec.app.data.SystemMemoryMetric> getMemoryMetrics();
 *     descriptor: ()Ljava/util/ArrayList;
 *
 *   public java.util.ArrayList<com.gibbonsdimarco.yamec.app.data.SystemDiskMetric> getDiskMetrics();
 *     descriptor: ()Ljava/util/ArrayList;
 *
 *   public java.util.ArrayList<com.gibbonsdimarco.yamec.app.data.SystemNicMetric> getNicMetrics();
 *     descriptor: ()Ljava/util/ArrayList;
 *
 *   public native void sayHello();
 *     descriptor: ()V
 *
 *   static {};
 *     descriptor: ()V
 * }
 *
 */
