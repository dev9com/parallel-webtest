package com.dynacrongroup.webtest.base;

import com.dynacrongroup.webtest.util.ConfigurationValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class figures out which WebDriver(s) to set up.
 */
public final class WebDriverParameterFactory {

    private WebDriverParameterFactory() {
        throw new IllegalAccessError("utility class should not be constructed");
    }

    public static final String WEBDRIVER_DRIVER = "WEBDRIVER_DRIVER";
    public static final String SINGLE_SAUCE = "SINGLE_SAUCE";
    public static final String DEFAULT_TARGETS = "DEFAULT_TARGETS";
    public static final String BY_CLASS = "byclass";
    public static final String NO_DEFAULT_SPECIFIED_TARGETS = "firefox:5,iexplore:7,iexplore:8,iexplore:9,chrome:*";

    private static final String CONFIGURED_CLASS_DRIVER = ConfigurationValue.getConfigurationValue(
            WEBDRIVER_DRIVER, null);

    private static final String CONFIGURED_SINGLE_SAUCE = ConfigurationValue.getConfigurationValue(
            SINGLE_SAUCE, null);

    private static List<String[]> driverTargets;


    public static List<String[]> getDriverTargets() {

        if (driverTargets == null) {
            createDriverTargets();
        }
        return driverTargets;
    }

    private static void createDriverTargets() {
        if (CONFIGURED_CLASS_DRIVER != null) {
            createClassDriverTarget();
        } else if (CONFIGURED_SINGLE_SAUCE != null) {
            createSingleSauceTarget();
        } else {
            createStandardSauceTargets();
        }
    }

    private static void createClassDriverTarget() {
        createSingleTarget(BY_CLASS, CONFIGURED_CLASS_DRIVER);
    }

    private static void createSingleSauceTarget() {
        createSingleTarget(convertToParameters(CONFIGURED_SINGLE_SAUCE));
    }


    /**
     * These are the standard SauceLabs targets for running a multi-browser test.
     * They can be configured using the DEFAULT_TARGETS config value.
     *
     * @return A list of string arrays; each list element is a paired browser/version.
     */
    private static void createStandardSauceTargets() {
        List<String[]> result = new ArrayList<String[]>();
        String[] targets = ConfigurationValue.getConfigurationValue(DEFAULT_TARGETS,
                NO_DEFAULT_SPECIFIED_TARGETS).split(",");

        for (String target : targets) {
            result.add(convertToParameters(target));
        }

        driverTargets = result;
    }

    private static void createSingleTarget(String... parameters) {
        driverTargets = Arrays.asList(new String[][]{parameters});
    }

    /**
     * Splits a target string into browser/version String pair, with version set
     * to null if version is "*".
     *
     * @param target a browser:version string (iexplore:8, for example)
     * @return target split into separate strings for browser and version.
     */
    private static String[] convertToParameters(String target) {
        String[] items = target.split(":");
        if (items.length != 2 && items.length != 3) {
            throw new IllegalArgumentException("Target " + target + " should be in browser:version" +
                    " or browser:version:platform format." );
        }
        items[1] = items[1].replaceAll("\\*", "");  //* used to refer to "any browser".  Now null is used.
        return items;
    }
}
