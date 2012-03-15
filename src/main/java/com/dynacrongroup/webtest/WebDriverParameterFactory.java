package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.util.ConfigurationValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class figures out which WebDriver(s) to set up.
 */
public class WebDriverParameterFactory {

    public static final String WEBDRIVER_DRIVER = "WEBDRIVER_DRIVER";
    public static final String SINGLE_SAUCE = "SINGLE_SAUCE";
    public static final String DEFAULT_TARGETS = "DEFAULT_TARGETS";
    public static final String BY_CLASS = "byclass";
    public static final String NO_DEFAULT_SPECIFIED_TARGETS = "firefox:5,iexplore:7,iexplore:8,iexplore:9,chrome:*";

    public static List<String[]> TARGETS;

    private static final String CONFIGURED_CLASS_DRIVER = ConfigurationValue.getConfigurationValue(
            WEBDRIVER_DRIVER, null);

    private static final String CONFIGURED_SINGLE_SAUCE = ConfigurationValue.getConfigurationValue(
            SINGLE_SAUCE, null);

    public static List<String[]> getDriverTargets() {

        if (TARGETS == null) {
            createDriverTargets();
        }
        return TARGETS;
    }

    private static final void createDriverTargets() {
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

        TARGETS = result;
    }

    private static void createSingleTarget(String... parameters) {
        TARGETS = Arrays.asList(new String[][]{parameters});
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
        if (items.length != 2) {
            throw new IllegalArgumentException("Target " + target + " should have one colon in browser:version format." );
        }
        items[1] = items[1].replaceAll("\\*", "");  //* used to refer to "any browser".  Now null is used.
        return items;
    }
}
