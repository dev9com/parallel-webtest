package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.util.ConfigurationValue;
import com.google.common.annotations.VisibleForTesting;

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

    @VisibleForTesting
    static List<String[]> TARGETS;

    @VisibleForTesting
    String classDriver;

    @VisibleForTesting
    String singleSauce;

    public List<String[]> getDriverTargets() {

        if (TARGETS == null) {
            getConfigurationValues();
            createDriverTargets();
        }
        return TARGETS;
    }

    @VisibleForTesting
    void createDriverTargets() {
        if (classDriver != null) {
            TARGETS = getClassDriverTargets();
        } else if (singleSauce != null) {
            TARGETS = getSingleSauceTargets();
        } else {
            TARGETS = getStandardSauceLabsTargets();
        }
    }

    private void getConfigurationValues() {
        classDriver = ConfigurationValue.getConfigurationValue(
                WEBDRIVER_DRIVER, null);
        singleSauce = ConfigurationValue.getConfigurationValue(
                SINGLE_SAUCE, null);
    }

    private List<String[]> getClassDriverTargets() {
        return getSingleDriverList(BY_CLASS, classDriver);
    }

    private List<String[]> getSingleSauceTargets() {
        return getSingleDriverList(convertToParameters(singleSauce));
    }


    /**
     * These are the standard SauceLabs targets for running a multi-browser test.
     * They can be configured using the DEFAULT_TARGETS config value.
     *
     * @return A list of string arrays; each list element is a paired browser/version.
     */
    private List<String[]> getStandardSauceLabsTargets() {
        List<String[]> result = new ArrayList<String[]>();
        String[] targets = ConfigurationValue.getConfigurationValue(DEFAULT_TARGETS,
                NO_DEFAULT_SPECIFIED_TARGETS).split(",");

        for (String target : targets) {
            result.add(convertToParameters(target));
        }

        return result;
    }

    private List<String[]> getSingleDriverList(String... parameters) {
        return Arrays.asList(new String[][]{parameters});
    }

    /**
     * Splits a target string into browser/version String pair, with version set
     * to null if version is "*".
     *
     * @param target a browser:version string (iexplore:8, for example)
     * @return target split into separate strings for browser and version.
     */
    private String[] convertToParameters(String target) {
        String[] items = target.split(":");
        if (items.length != 2) {
            throw new IllegalArgumentException("Target " + target + " should have one colon in browser:version format." );
        }
        items[1] = items[1].replaceAll("\\*", "");  //* used to refer to "any browser".  Now null is used.
        return items;
    }
}
