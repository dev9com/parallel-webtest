package com.dynacrongroup.webtest.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tries to load a configuration value. You can pass in a configuration value
 * from either a Java system property or an environment property. Java
 * properties have priority over environment properties.
 * <p/>
 * Note that system property values that contain a $ in the result are ignored.
 * This is to work around an issue with Maven, where non-existent environment
 * values are passed in as literals.
 * <p/>
 * You can also pass in the text string "null" as an override mechanism (because
 * there is no "unset" option to completely null out an already set value in
 * Maven)
 */
public class ConfigurationValue {

    private final static Logger log = LoggerFactory
            .getLogger(ConfigurationValue.class);

    private ConfigurationValue() {
        // Checkstyle
    }

    ;

    private static String getValueIfSet(String value) {
        String result = null;

        if (value == null) {
            return result;
        }

        if (value.isEmpty()) {
            return null;
        }

        // Check for unexpanded Maven property
        if (value.contains("${")) {
            return null;
        }

        // Simple "wipe out" override mechanism
        if ("null".equals(value)) {
            return null;
        }

        return value;
    }

    public static String getConfigurationValue(String key, String defaultValue) {

        String result = null;

        result = getValueIfSet(System.getProperty(key));

        if (result != null) {
            log.trace("found System Property key " + key + " set to " + result);
            return result;
        }

        result = getValueIfSet(System.getenv(key));
        if (result != null) {
            log.trace("found System Environment key " + key + " set to "
                    + result);
            return result;
        }

        log.trace("using default key " + key + " set to " + defaultValue);

        return defaultValue;
    }
}
