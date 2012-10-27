package com.dynacrongroup.webtest.parameter;

import com.dynacrongroup.webtest.base.WebDriverBase;
import com.dynacrongroup.webtest.browser.WebDriverConfig;
import com.dynacrongroup.webtest.parameter.ParameterCombination;
import com.dynacrongroup.webtest.parameter.ParameterCombinationFactory;
import com.rits.cloning.Cloner;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * User: yurodivuie
 * Date: 10/17/12
 * Time: 10:53 PM
 */
public class ParameterCombinationFactoryTest {

    Map<String, Object> driverConfigMap;
    Map<String, Object> testConfigMap;
    Config testConfig;
    Cloner cloner = new Cloner();

    private class TestClass extends WebDriverBase {
        TestClass(ParameterCombination p) {
            super(p);
        }
    }

    @Before
    public void initializeTestConfig() {
        testConfigMap = new HashMap<String, Object>();
        driverConfigMap = new HashMap<String, Object>();
    }

    @Test
    public void testWithoutParameters() {
        testConfig = ConfigFactory.parseMap(testConfigMap);
        List<ParameterCombination> parameters = new ParameterCombinationFactory(TestClass.class).
                convertToParameterCombinations(testConfig);
        assertThat(parameters).hasSize(0);
    }

    @Test
    public void testSingleParameterSingleValue() {
        driverConfigMap.put("browser", "safari");
        driverConfigMap.put("type", "remote");
        testConfigMap.put("webDriverConfig", driverConfigMap);
        testConfig = ConfigFactory.parseMap(testConfigMap);

        List<ParameterCombination> parameters = new ParameterCombinationFactory(TestClass.class).
                convertToParameterCombinations(testConfig);

        assertThat(parameters).hasSize(1);
        ParameterCombination parameterCombination = parameters.get(0);
        assertThat(parameterCombination.getWebDriverConfig().getBrowser()).isEqualTo(WebDriverConfig.Browser.SAFARI);
    }

    @Test
    public void testSingleParameterTwoValues() {
        List<String> driverList = Arrays.asList("firefox", "chrome");
        List<Map<String, Object>> driverConfigs = new ArrayList<Map<String, Object>>();
        for( String browser : driverList) {
            driverConfigMap.put("browser", browser);
            driverConfigs.add(cloner.deepClone(driverConfigMap));
        }
        testConfigMap.put("webDriverConfig",driverConfigs);
        testConfig = ConfigFactory.parseMap(testConfigMap);

        List<ParameterCombination> parameters = new ParameterCombinationFactory(TestClass.class).
                convertToParameterCombinations(testConfig);

        assertThat(parameters).hasSize(2);
        for (ParameterCombination parameterCombination : parameters ) {
            assertThat(driverList).contains(parameterCombination.getWebDriverConfig().getBrowser().name().toLowerCase());
        }
    }

    @Test
    public void testTwoParametersTwoValues() {
        List<String> driverList = Arrays.asList("firefox", "chrome");
        List<Map<String, Object>> driverConfigs = new ArrayList<Map<String, Object>>();
        for( String browser : driverList) {
            driverConfigMap.put("browser", browser);
            driverConfigs.add(cloner.deepClone(driverConfigMap));
        }
        testConfigMap.put("webDriverConfig",driverConfigs);
        List<String> languageList = Arrays.asList("en", "fr");
        testConfigMap.put("language", languageList);
        testConfig = ConfigFactory.parseMap(testConfigMap);

        List<ParameterCombination> parameters = new ParameterCombinationFactory(TestClass.class).
                convertToParameterCombinations(testConfig);

        assertThat(parameters).hasSize(4);
        for (ParameterCombination parameterCombination : parameters ) {
            assertThat(driverList).contains(parameterCombination.getWebDriverConfig().getBrowser().name().toLowerCase());
            assertThat(languageList).contains(parameterCombination.getLanguage());
        }
    }

}
