package com.dynacrongroup.webtest.util;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * User: yurodivuie
 * Date: 10/16/12
 * Time: 12:40 PM
 */
public class Configuration {

    private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);
    private static final String confExtension = ".conf";
    private static final String profile = System.getProperty("config.profile", null);

    private static Config config = null;

    public static Config getConfig() {
        if (config == null) {
            config = addProfileToConfig(ConfigFactory.load());
        }
        return config;
    }

    public static Config getConfigForClass(Class klass) {
        return ConfigFactory.defaultOverrides()
                .withFallback(addProfileToConfig(ConfigFactory.parseFile(getClassConfigFile(klass))))
                .withFallback(getConfig());
    }

    private static File getClassConfigFile(Class klass) {
        URL classFileUrl = klass.getResource(klass.getSimpleName() + confExtension);
        File classFile = null;
        if (classFileUrl != null) {
            try {
                classFile = new File(classFileUrl.toURI());
            }
            catch (URISyntaxException ex) {
                LOG.warn("Failed to open config file for " + klass.getSimpleName() + ": {}", ex.getMessage());
            }
        }
        return classFile;
    }

    private static Config addProfileToConfig(Config config) {
        if (profile != null && config.hasPath(profile)) {
            return config.getConfig(profile).withFallback(config);
        }
        return config;
    }

}
