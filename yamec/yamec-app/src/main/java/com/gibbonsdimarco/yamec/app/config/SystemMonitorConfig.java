package com.gibbonsdimarco.yamec.app.config;

import com.gibbonsdimarco.yamec.app.jni.SystemMonitorManagerJNI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SystemMonitorConfig {

    private static final Logger logger = LoggerFactory.getLogger(SystemMonitorConfig.class);

    @Bean(destroyMethod = "close")
    public SystemMonitorManagerJNI systemMonitorManager() {
        try {
            logger.info("Creating singleton instance of SystemMonitorManagerJNI");
            SystemMonitorManagerJNI monitorManager = new SystemMonitorManagerJNI();

            // Verify it was created properly
            if (monitorManager == null || !monitorManager.isOpen()) {
                logger.error("SystemMonitorManagerJNI created but not in open state");
                throw new RuntimeException("Failed to properly initialize SystemMonitorManagerJNI");
            }

            monitorManager.collectCounterData();

            Thread.sleep(1000);

//            monitorManager.collectCounterData();

            return monitorManager;
        } catch (Exception e) {
            logger.error("Failed to create SystemMonitorManagerJNI", e);
            // In a bean creation method, it's better to throw an exception than return null
            // This will cause Spring to fail fast rather than with NPEs later
            throw new BeanCreationException("SystemMonitorManagerJNI bean creation failed", e);
        }
    }
}