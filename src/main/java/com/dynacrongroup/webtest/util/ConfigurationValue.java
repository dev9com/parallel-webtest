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
public final class ConfigurationValue {

    private final static Logger log = LoggerFactory
            .getLogger(ConfigurationValue.class);

    private ConfigurationValue() {
        throw new UnsupportedOperationException("Should not create utility class");
    }

    /**
     * Sets value to null if the value is empty, an unexpanded maven property, or
     * is set to the string "null".
     *
     * @param value
     *          A configuration value to be filtered.
     * @return value if it does not match filter
     */
    private static String getValueIfSet(String value) {
        String result = value;

        if ( value.isEmpty()
                || value.contains("${")
                || "null".equalsIgnoreCase(value)) {
            result = null;
        }

        return result;
    }

    /**
     * Retrieves configuration according to the following rules.  If a System Property Variable
     * is present with the key, use that property.  Otherwise, if an environment variable is
     * present with the key, use that property.  Otherwise, use the default given.
     * @param key
     *          Key to search for.
     * @param defaultValue
     *          Default to be used if system and env properties are missing.
     * @return  String value of configuration; may be null.
     */
    public static String getConfigurationValue(String key, String defaultValue) {
        String result;

        if (System.getProperties().containsKey(key)) {
            result = getValueIfSet(System.getProperty(key));
            log.trace("found System Property key [{}] set to [{}]", key, result);
        }
        else if (System.getenv().containsKey(key)) {
            result = getValueIfSet(System.getenv(key));
            log.trace("found Environment Property key [{}] set to [{}]", key, result);
        }
        else {
            result = defaultValue;
            log.trace("Using default key [{}] set to [{}]", key, result);
        }

        return result;
    }
}
