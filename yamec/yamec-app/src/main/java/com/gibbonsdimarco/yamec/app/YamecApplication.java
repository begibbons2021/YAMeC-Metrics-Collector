package com.gibbonsdimarco.yamec.app;

import com.gibbonsdimarco.yamec.app.jni.SystemMonitorManagerJNI;
import jakarta.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;


@SpringBootApplication
@EnableScheduling
public class YamecApplication {
    private static final Logger logger = LoggerFactory.getLogger(YamecApplication.class);

    private static ApplicationContext context;

    private static File yamecHome;

    private static int serverPort;

    /**
     * <p>Prepares the application to store all necessary files in the application home directory.</p>
     * <p>This is a hidden folder in the user's home directory. If the hidden folder already exists,
     * the folder is loaded. If not, it is created. </p>
     * <p>If the folder cannot be loaded or cannot be created, the program displays an error message
     * and exits gracefully.</p>
     */
    private static void setupApplicationFileSystem() {
        System.setProperty("java.awt.headless", "false");
        yamecHome = new File(System.getProperty("user.home") + "/.yamec-home");

        logger.info("SETUP - Attempting to access YAMeC Home directory: {}", yamecHome);

        boolean homeDirectoryExists = yamecHome.exists();
        if (homeDirectoryExists) {
            if (!yamecHome.isDirectory()) {
                logger.error("SETUP - Cannot create the YAMeC Home directory because " + "a file exists with the name 'yamec'.");
                logger.error("SETUP - Exiting due to application setup failure (cannot load home directory).");
                JOptionPane.showMessageDialog(null, """
                        YAMeC cannot be loaded because there is a file with the name '.yamec-home' \
                        in your home directory.
                        The file conflicts with the home directory of the application.
                        Please delete or rename the file, then try running YAMeC again.""",
                        "YAMeC - Error Starting Up", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
            }
        }

        if (!homeDirectoryExists) {
            // Create the '.yamec-home' folder
            logger.info("SETUP - No YAMeC Home directory found. Creating the YAMeC Home directory in: {}", yamecHome);
            boolean directoryCreationSuccess = false;
            try {
                directoryCreationSuccess = yamecHome.mkdir();
            } catch (Exception e) {
                logger.error("SETUP - Creation of the YAMeC Home directory failed because "
                                + "of an exception: {} - {}", e.getCause(), e.getMessage());
                // Log stack trace contents
                StackTraceElement[] stackTraceElements = e.getStackTrace();
                logger.error("SETUP - Directory Creation Stack Trace [0]: ");
                for (int i = 0; i < stackTraceElements.length; i++) {
                    logger.error("SETUP - Directory Creation Stack Trace [{}]: {}", i + 1, stackTraceElements[i]);
                }
            }

            if (!directoryCreationSuccess) {
                logger.error("SETUP - Exiting due to application setup failure (cannot create home directory).");
                JOptionPane.showMessageDialog(null, """
                        YAMeC cannot be loaded because the '.yamec-home' directory could not be created \
                        during first-time application setup.
                        Please try running YAMeC again and ensure your anti-virus is not blocking \
                        YAMeC from running and creating files.""",
                        "YAMeC - Error Starting Up", JOptionPane.ERROR_MESSAGE);
                System.exit(-2);
            }
        }


        logger.info("SETUP - YAMeC Home directory found and loaded successfully: {}", yamecHome);
        System.setProperty("java.awt.headless", "true");

    }

    /**
     * Attempts to initialize the System Monitor component of YAMeC. If it cannot be initialized or appears
     * not to be initialized properly, the application will be exited.
     */
    private static void initializeSystemMonitorManager(ApplicationContext context) {
        SystemMonitorManagerJNI monitor = context.getBean(SystemMonitorManagerJNI.class);
        try {
            System.err.println("Verifying System Monitor Manager...");
            if (monitor == null) {
                throw new NullPointerException("System Monitor Manager is null.");
            }
            System.err.println("System Monitor Manager Verified.");
        } catch (Exception e) {
            logger.error("SETUP - Cannot load the SystemMonitorManager because of an exception: {} - {}",
                            e.getCause(), e.getMessage());
            // Log stack trace contents
            StackTraceElement[] stackTraceElements = e.getStackTrace();
            logger.error("SETUP - Monitor Creation Stack Trace [0]: ");
            for (int i = 0; i < stackTraceElements.length; i++) {
                logger.error("SETUP - Monitor Creation Stack Trace [{}]: {}", i + 1, stackTraceElements[i]);
            }
        }

        if (monitor == null) {
            logger.error("SETUP - Exiting due to application setup failure (cannot initialize system monitor).");
            System.setProperty("java.awt.headless", "false");
            JOptionPane.showMessageDialog(null, """
                    YAMeC cannot be loaded because it could not load the system monitoring\
                    components.
                    Please try running YAMeC again and ensure your anti-virus is not blocking\
                    YAMeC from running and creating files.""",
                    "YAMeC - Error Starting Up", JOptionPane.ERROR_MESSAGE);
            System.exit(-3);
        }

        // Manually collect counter data (acts as a bit of a health check)
        // It's okay if this gets interrupted this once
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            logger.error("""
                   SETUP - System Monitor - The timer to wait to collect data\
                    from the SystemMonitorManager was interrupted.""");
        }

        try {
            int dataCollectionSuccess = monitor.collectCounterData();
            if (dataCollectionSuccess != 0) {
                logger.error("SETUP - System Monitor - Data collection failed with status code: {}",
                        dataCollectionSuccess);
            }
        } catch (Exception e) {
            logger.error("SETUP - System Monitor - Cannot collect data because of an exception: {} - {}",
                    e.getCause(), e.getMessage());
            // Log stack trace contents
            StackTraceElement[] stackTraceElements = e.getStackTrace();
            logger.error("SETUP - System Monitor Test Stack Trace [0]: ");
            for (int i = 0; i < stackTraceElements.length; i++) {
                logger.error("SETUP - System Monitor Test Stack Trace [{}]: {}", i + 1, stackTraceElements[i]);
            }
            System.setProperty("java.awt.headless", "false");
            // Exit with error message
            JOptionPane.showMessageDialog(null, """
                    YAMeC cannot be loaded because the system monitoring components\
                    are not working as expected.
                    Please try running YAMeC again and ensure your anti-virus is not blocking\
                    YAMeC from running and creating files.""",
                    "YAMeC - Error Starting Up", JOptionPane.ERROR_MESSAGE);

            System.exit(-4);
        }

    }

    /**
     * <p>Closes all resources and ends all background processes pertaining to YAMeC to prepare
     * the system for shutdown/exit.</p>
     * <p>This should be run whenever YAMeC is to be closed. It should never throw an
     * exception and should ensure a graceful exit of the application with minimal possibility
     * of a resource leak (within our [YAMeC team's] control).</p>
     */
    @PreDestroy
    private static void cleanUp() {
        logger.info("CLEANUP - YamecApplication is shutting down...");
    }

//    /**
//     * A function which
//     */
//    private static void testSystemMonitorManager() {
//        System.err.println("Testing System Monitor Manager...");
//
//        try {
//            System.err.println("Testing CPU Metrics Retrieval...");
//            SystemCpuMetric cpuMetrics = monitor.getCpuMetrics();
//            if (cpuMetrics != null) {
//                System.err.printf("CPU Information: \n\t%s\n\t\tUsage: %.1f%%\n",
//                        cpuMetrics.getDeviceName(), cpuMetrics.getAverageUtilization());
//            } else {
//                System.err.println("CPU Information: \n\tNo CPU Metrics Found");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.err.println("Failed to retrieve CPU Metrics.");
//            return;
//        }
//
//        try {
//            System.err.println("Testing GPU Metrics Retrieval...");
//            SystemGpuMetric gpuMetrics = monitor.getGpuMetrics();
//            if (gpuMetrics != null) {
//                System.err.printf("GPU Information: \n\t%s\n\t\tUsage: %.1f%%\n",
//                        gpuMetrics.getDeviceName(), gpuMetrics.getUsage());
//            } else {
//                System.err.println("GPU Information: \n\tNo GPU Metrics Found");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.err.println("Failed to retrieve GPU Metrics.");
//            return;
//        }
//
//        try {
//            System.err.println("Testing Memory Metrics Retrieval...");
//            SystemMemoryMetric memoryMetrics = monitor.getMemoryMetrics();
//            if (memoryMetrics != null) {
//                // Calculate the actual virtual memory use from the amount of committed memory used.
//                double bytesVirtualMemoryInUse = memoryMetrics.getCommittedVirtualMemoryBytes();
//
//                System.err.println("Memory Information:");
//                System.err.printf("\tAvailable Memory (Physical Memory): %s bytes\n",
//                        memoryMetrics.getPhysicalMemoryAvailableUnsigned());
//                System.err.printf("\tVirtual Memory Committed: %s\n",
//                        memoryMetrics.getVirtualMemoryCommitted());
//                System.err.printf("\tVirtual Memory In-Use: %f%% (~%.0f bytes)\n",
//                        memoryMetrics.getCommittedVirtualMemoryUsage(),
//                        bytesVirtualMemoryInUse);
//            } else {
//                System.err.println("Memory Information: \n\tNo Memory Metrics Found");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.err.println("Failed to retrieve Memory Metrics.");
//            return;
//        }
//
//        try {
//            System.err.println("Testing Disk Metrics Retrieval...");
//            ArrayList<SystemDiskMetric> diskMetrics = monitor.getDiskMetrics();
//            if (diskMetrics != null) {
//
//                System.err.println("Disk Information:");
//                System.err.printf("\tDisk Instances Present (including _Total): %d\n", diskMetrics.size());
//
//                for (SystemDiskMetric diskMetric : diskMetrics) {
//                    // Skip total system disk use
//                    if (diskMetric.getDeviceName().compareTo("_Total") == 0) {
//                        continue;
//                    }
//
//                    System.err.printf("\t%s\n", diskMetric.getDeviceName());
//                    System.err.printf("\t\tUsage: %f%%\n", diskMetric.getUsage());
//                    System.err.printf("\t\tRead Bandwidth: %s bytes/sec\n", diskMetric.getReadBandwidthUnsigned());
//                    System.err.printf("\t\tWrite Bandwidth: %s bytes/sec\n", diskMetric.getWriteBandwidthUnsigned());
//                    System.err.printf("\t\tAverage Transfer Rate: %f bytes/sec\n", diskMetric.getAverageTimeToTransfer());
//                }
//            } else {
//                System.err.println("Disk Information: \n\tNo Disk Metrics Found");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.err.println("Failed to retrieve Disk Metrics.");
//            return;
//        }
//
//        try {
//            System.err.println("Testing NIC Metrics Retrieval...");
//            ArrayList<SystemNicMetric> nicMetrics = monitor.getNicMetrics();
//            if (nicMetrics != null) {
//
//                System.err.println("NIC Information:");
//                System.err.printf("\tNIC Instances Present (including _Total): %d\n", nicMetrics.size());
//
//                for (SystemNicMetric nicMetric : nicMetrics) {
//                    // Skip total system NIC use
//                    if (nicMetric.getDeviceName().compareTo("_Total") == 0) {
//                        continue;
//                    }
//
//                    System.err.printf("\t%s\n", nicMetric.getDeviceName());
//                    System.err.printf("\t\tCurrent Operation Bandwidth: %s bps\n", nicMetric.getNicBandwidthUnsigned());
//                    System.err.printf("\t\tBytes Sent: %s bytes/sec\n", nicMetric.getBytesSentUnsigned());
//                    System.err.printf("\t\tWrite Bandwidth: %s bytes/sec\n", nicMetric.getBytesReceivedUnsigned());
//                }
//            } else {
//                System.err.println("NIC Information: \n\tNo NIC Metrics Found");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.err.println("Failed to retrieve NIC Metrics.");
//            return;
//        }
//
//
//        System.err.println("Testing complete.");
//    }


    public static void main(String[] args) {

        try {
            setupApplicationFileSystem();

            // Start Spring application first, so beans are available
            context = SpringApplication.run(YamecApplication.class, args);

            serverPort = context.getBean(ServerProperties.class).getPort();

            // Get monitor bean from Spring context and initialize it
            initializeSystemMonitorManager(context);


            logger.info("User home directory: {}", System.getProperty("user.home"));
//        testSystemMonitorManager();
            logger.info("Yamec Application Started");

            // now we enter our URL that we want to open in our
            // default browser
            System.setProperty("java.awt.headless", "false");
            try {
                Desktop desk = Desktop.getDesktop();
                desk.browse(new URI("http://localhost:" + serverPort + "/"));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        String.format("""
                        YAMeC started successfully, but the default browser could not be opened.
                        To view system metrics, please open the following URL in your browser: \
                        http://localhost:%d/""", serverPort),
                        "YAMeC", JOptionPane.WARNING_MESSAGE);
            }
            System.setProperty("java.awt.headless", "true");
        } catch (Exception e) {
            // http://www.java2s.com/example/java/swing/opens-a-joptionpane-with-the-error-message-and-formatted-stack-trace-o.html

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            pw.println("""
                        YAMeC has ran into a problem and must be closed.
                        
                        For assistance and to report the bug to the YAMeC team, \
                        please send this crash log to them:
                        """);

            pw.println("Exception Type: " + e.getClass().getName());
            pw.println("Exception Message: " + e.getMessage());
            pw.println("\nStack Trace: ");
            e.printStackTrace(pw);
            pw.flush();

            JFrame frame = new JFrame();
            JTextArea textArea = new JTextArea();
            JScrollPane scrollPane = new JScrollPane(textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);


            System.setProperty("java.awt.headless", "false");
            JOptionPane.showMessageDialog(null, scrollPane,
                    "YAMeC Has Crashed!", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }



    }

}
