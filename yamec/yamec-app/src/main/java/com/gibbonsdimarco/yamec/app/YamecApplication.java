package com.gibbonsdimarco.yamec.app;

import com.gibbonsdimarco.yamec.app.jni.SystemMonitorManagerJNI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.nio.file.Paths;

@SpringBootApplication
public class YamecApplication {
//    public static String getExecutableLocation()
//    {
//        try {
//            java.net.URL execURL = YamecApplication.class.getProtectionDomain().getCodeSource().getLocation();
//
//            if (execURL != null) {
//                return Paths.get(execURL.toURI()).toString();
//            }
//            else {
//                System.err.println("Failed to get executable directory: A URL could not be resolved.");
//                return null;
//            }
//
//        }
//        catch(SecurityException | URISyntaxException e)
//        {
//            System.err.println("Failed to get executable directory: " + e.getMessage());
//
//            return null;
//        }
//    }
//

//    private static void setupApplicationFileSystem() {
//        java.io.File yamecHome = new java.io.File(System.getProperty("user.home") + "/.yamec-home");
//
//        if (yamecHome.exists()) {
//            if (!yamecHome.isDirectory()) {
//                System.err.println("Cannot create the YAMEC Home directory because a file exists with the name 'yamec'.");
//                System.exit(1);
//            }
//            else {
//                return;
//            }
//        }
//
//        try {
//            yamecHome.mkdir();
//        }
//        catch (Exception e) {
//
//        }
//
//    }

// Test finite components
//

    private static SystemMonitorManagerJNI monitor;

    private static boolean testSystemMonitorManager() {
        System.err.println("Testing System Monitor Manager...");

        try {
            System.err.println("Creating System Monitor Manager...");
            monitor = new SystemMonitorManagerJNI();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to create System Monitor Manager.");
            return false;
        }

        try {
            System.err.println("Closing System Monitor Manager... ");
            if (!monitor.close()) {
                throw new Exception("The System Monitor Manager could not be closed.");
            }

            if (!monitor.isClosed()) {
                throw new Exception("The status of the System Monitor Manager did not change to closed.");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to close System Monitor Manager.");
            return false;
        }

        System.err.println("Testing complete.");
        return true;
    }

    public static void main(String[] args) {
        testSystemMonitorManager();

        System.out.println(System.getProperty("user.home"));

        SpringApplication.run(YamecApplication.class, args);
        System.out.println("Yamec Application Started");

    }

}
