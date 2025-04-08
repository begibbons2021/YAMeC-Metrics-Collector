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
     * Release the memory allocated to the SystemMonitorManager in C++
     * and stops further operations.
     *
     * @return True if the SystemMonitorManager was closed successfully;
     * If the closure fails or the SystemMonitorManager is already
     * closed, this returns false
     */
    public boolean close() {
        if (closed) {
            return false;
        }

        if (this.release(this.monitorAddress)) {
            closed = true;
            return true;
        }
        else {
            return false;
        }

    }

    public SystemCpuMetric getCpuMetrics() {
        return null;
    }

    public SystemGpuMetric getGpuMetrics() {
        return null;
    }

    public java.util.ArrayList<SystemMemoryMetric> getMemoryMetrics() {
        return null;
    }

    public java.util.ArrayList<SystemDiskMetric> getDiskMetrics() {
        return null;
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
     *
     * If the instance cannot be initialized, this returns -1
     *
     * @return A long containing the memory address of the native object
     * created, or -1 if creation fails
     */
    private native long initialize();

//    private native SystemCpuMetric getCpuMetrics(long ptr);
//
//    private native SystemGpuMetric getGpuMetrics(long ptr);
//
//    private native java.util.ArrayList<SystemMemoryMetric> getMemoryMetrics(long ptr);
//
//    private native java.util.ArrayList<SystemDiskMetric> getDiskMetrics(long ptr);
//
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
