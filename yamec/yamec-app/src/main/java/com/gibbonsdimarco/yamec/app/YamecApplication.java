package com.gibbonsdimarco.yamec.app;

import com.gibbonsdimarco.yamec.app.jni.SystemMonitorManagerJNI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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

    private static final Logger logger = LoggerFactory.getLogger(YamecApplication.class);

    public static void main(String[] args) {
        logger.info("User home directory: {}", System.getProperty("user.home"));

        SpringApplication.run(YamecApplication.class, args);
        logger.info("Yamec Application Started");

        new SystemMonitorManagerJNI().sayHello();
    }

}
