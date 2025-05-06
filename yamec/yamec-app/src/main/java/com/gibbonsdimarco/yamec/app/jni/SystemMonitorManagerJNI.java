package com.gibbonsdimarco.yamec.app.jni;

import com.gibbonsdimarco.yamec.app.data.*;
import com.github.fommil.jni.JniLoader;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.websocket.OnClose;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.ArrayList;


/**
 * TODO:
 * This class should never be instantiated more than once! It will fail to run if it is
 * re-initiated due to a native dependency's behavior.
 */
//@Service
public class SystemMonitorManagerJNI implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(SystemMonitorManagerJNI.class);



    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Fields/Variables
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private long monitorAddress = -1;
    private boolean closed = false;
    private final ExecutorService jniExecutor = Executors.newSingleThreadExecutor();
    private Timestamp lastCollectionTime = null;

    /**
     * Instantiates the System Monitor Manager.
     *
     * @throws RuntimeException If the System Monitor Manager cannot be instantiated
     *                          via the JNI call to native code
     */
    public SystemMonitorManagerJNI() {
        try {
            // Load the library and initialize everything in the executor thread
            Future<?> loadFuture = jniExecutor.submit(() -> {
                try {
                    JniLoader.load("native/windows/x64/yamecjni.dll");
                    initLogger(logger);
                    long ptr = initialize();
                    if (ptr == -1) {
                        throw new RuntimeException("Failed to initialize native monitor");
                    }
                    return ptr;
                } catch (UnsatisfiedLinkError e) {
                    logger.error("Native code library failed to load", e);
                    throw e;
                }
            });

            // Wait for initialization to complete
            monitorAddress = (Long) loadFuture.get();
        } catch (Exception e) {
            logger.error("Error initializing SystemMonitorManagerJNI", e);
            throw new RuntimeException("Unable to initialize system monitor manager", e);
        }
    }

    //    @PostConstruct
    protected void initCollectCounterData() {
        int status = this.collectCounterData();

        if (status != 0) {
            logger.error("System Monitor Manager - Initial collection of counter data failed: Code: {}", status);
        }
    }

    public int collectCounterData() {
        if (closed) {
            logger.warn("Attempt to collect counter data on closed SystemMonitorManagerJNI");
            return -1;
        }

        try {
            // Run the native call on our dedicated thread for JNI operations
            Future<Integer> future = jniExecutor.submit(() -> collectCounterData(monitorAddress));
            int response = future.get(); // Wait for the result

            if (response == 0) {
                // Only update the last collection time if the collection was successful
                this.setLastCollectionTime(new Timestamp(System.currentTimeMillis()));
            } else {
                logger.error("System Monitor Manager - Failed to collect counter data: Code: {}", response);
            }

            return response;
        } catch (Exception e) {
            logger.error("Error collecting counter data", e);
            return -1;
        }
    }


    /**
     * Returns a boolean variable containing whether this SystemMonitorManager is closed
     *
     * @return True if this instance is open; otherwise, this returns false
     */
    public boolean isOpen() {
        return !closed;
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
    public void close() throws RuntimeException {
        if (closed) {
            logger.warn("Attempting to call SystemMonitorManagerJNI#close() when closed");
            return;
        }

        try {
            Future<Boolean> future = jniExecutor.submit(() -> release(monitorAddress));
            boolean success = future.get();

            if (success) {
                logger.info("Successfully released native resources");
                closed = true;
            } else {
                logger.error("Failed to release native resources");
                throw new RuntimeException("Unable to release system monitor manager memory");
            }
        } catch (Exception e) {
            logger.error("Error while closing SystemMonitorManagerJNI", e);
            throw new RuntimeException("Error releasing native resources", e);
        } finally {
            jniExecutor.shutdown();
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
            logger.warn("Attempting to call SystemMonitorManagerJNI#getCpuMetrics() when closed");
            return null;
        }

        try {
            Future<SystemCpuMetric> future = jniExecutor.submit(() -> getCpuMetrics(monitorAddress));
            return future.get();
        } catch (Exception e) {
            logger.error("Error getting CPU metrics", e);
            return null;
        }
    }

    /**
     * Retrieves the current system metrics for the system's GPU devices
     *
     * @return A SystemGpuMetric object containing the system's GPU information.
     * If the GPU metrics cannot be retrieved, this returns null.
     */
    public SystemGpuMetric getGpuMetrics() {
        if (closed) {
            logger.warn("Attempting to call SystemMonitorManagerJNI#getGpuMetrics() when closed");
            return null;
        }

        try {
            Future<SystemGpuMetric> future = jniExecutor.submit(() -> getGpuMetrics(monitorAddress));
            return future.get();
        } catch (Exception e) {
            logger.error("Error getting GPU metrics", e);
            return null;
        }
    }

    /**
     * Retrieves the current system metrics for the system's primary memory
     *
     * @return A SystemMemoryMetric object containing the system's memory information.
     * If the memory metrics cannot be retrieved, this returns null.
     */
    public SystemMemoryMetric getMemoryMetrics() {
        if (closed) {
            logger.warn("Attempting to call SystemMonitorManagerJNI#getMemoryMetrics() when closed");
            return null;
        }

        try {
            Future<SystemMemoryMetric> future = jniExecutor.submit(() -> getMemoryMetrics(monitorAddress));
            return future.get();
        } catch (Exception e) {
            logger.error("Error getting memory metrics", e);
            return null;
        }
    }

    public ArrayList<SystemDiskMetric> getDiskMetrics() {
        if (closed) {
            logger.warn("Attempting to call SystemMonitorManagerJNI#getDiskMetrics() when closed");
            return null;
        }

        try {
            Future<ArrayList<SystemDiskMetric>> future = jniExecutor.submit(() -> getDiskMetrics(monitorAddress));
            return future.get();
        } catch (Exception e) {
            logger.error("Error getting disk metrics", e);
            return null;
        }
    }

    public ArrayList<SystemNicMetric> getNicMetrics() {
        if (closed) {
            logger.warn("Attempting to call SystemMonitorManagerJNI#getNicMetrics() when closed");
            return null;
        }

        try {
            Future<ArrayList<SystemNicMetric>> future = jniExecutor.submit(() -> getNicMetrics(monitorAddress));
            return future.get();
        } catch (Exception e) {
            logger.error("Error getting NIC metrics", e);
            return null;
        }
    }

    public ArrayList<ProcessMetric> getProcessMetrics() {
        if (closed) {
            logger.warn("Attempting to call SystemMonitorManagerJNI#getProcessMetrics() when closed");
            return null;
        }

        try {
            Future<ArrayList<ProcessMetric>> future = jniExecutor.submit(() -> getProcessMetrics(monitorAddress));
            return future.get();
        } catch (Exception e) {
            logger.error("Error getting process metrics", e);
            return null;
        }
    }

    public CpuHardwareInformation getCpuHardwareInformation() {
        if (closed) {
            logger.warn("Attempting to call SystemMonitorManagerJNI#getCpuHardwareInformation() when closed");
            return null;
        }

        try {
            Future<CpuHardwareInformation> future = jniExecutor.submit(() -> getHardwareCpuInformation(monitorAddress));
            return future.get();
        } catch (Exception e) {
            logger.error("Error getting CPU hardware information", e);
            return null;
        }
    }

    public MemoryHardwareInformation getMemoryHardwareInformation() {
        if (closed) {
            logger.warn("Attempting to call SystemMonitorManagerJNI#getMemoryHardwareInformation() when closed");
            return null;
        }

        try {
            Future<MemoryHardwareInformation> future = jniExecutor.submit(() -> getHardwareMemoryInformation(monitorAddress));
            return future.get();
        } catch (Exception e) {
            logger.error("Error getting memory hardware information", e);
            return null;
        }
    }

    public ArrayList<DiskHardwareInformation> getDiskHardwareInformation() {
        if (closed) {
            logger.warn("Attempting to call SystemMonitorManagerJNI#getDiskHardwareInformation() when closed");
            return null;
        }

        try {
            Future<ArrayList<DiskHardwareInformation>> future = jniExecutor.submit(() -> getHardwareDiskInformation(monitorAddress));
            return future.get();
        } catch (Exception e) {
            logger.error("Error getting disk hardware information", e);
            return null;
        }
    }

    public ArrayList<NicHardwareInformation> getNicHardwareInformation() {
        if (closed) {
            logger.warn("Attempting to call SystemMonitorManagerJNI#getNicHardwareInformation() when closed");
            return null;
        }

        try {
            Future<ArrayList<NicHardwareInformation>> future = jniExecutor.submit(() -> getHardwareNicInformation(monitorAddress));
            return future.get();
        } catch (Exception e) {
            logger.error("Error getting NIC hardware information", e);
            return null;
        }
    }

    public Timestamp getLastCollectionTime() {
        return lastCollectionTime;
    }

    public void setLastCollectionTime(Timestamp lastCollectionTime) {
        this.lastCollectionTime = lastCollectionTime;
    }


// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// Native Calls
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * A test function which prints "Hello World" from native code
     */
    private static native void initLogger(Object javaLogger);

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

    private native int collectCounterData(long ptr);

    private native SystemCpuMetric getCpuMetrics(long ptr);

    private native SystemGpuMetric getGpuMetrics(long ptr);

    private native SystemMemoryMetric getMemoryMetrics(long ptr);

    private native java.util.ArrayList<SystemDiskMetric> getDiskMetrics(long ptr);

    private native java.util.ArrayList<SystemNicMetric> getNicMetrics(long ptr);

    private native java.util.ArrayList<ProcessMetric> getProcessMetrics(long ptr);

    private native CpuHardwareInformation getHardwareCpuInformation(long ptr);

    private native MemoryHardwareInformation getHardwareMemoryInformation(long ptr);

    private native java.util.ArrayList<DiskHardwareInformation> getHardwareDiskInformation(long ptr);

    private native java.util.ArrayList<NicHardwareInformation> getHardwareNicInformation(long ptr);

    /**
     * Releases the memory allocated to the SystemMonitorManager
     *
     * @param ptr The memory address where the SystemMonitorManager Native object
     *            is stored
     * @return True if the memory was released successfully; otherwise, this returns false
     */
    private native boolean release(long ptr);

}