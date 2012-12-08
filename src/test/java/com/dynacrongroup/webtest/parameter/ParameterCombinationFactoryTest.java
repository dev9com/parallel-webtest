package com.dynacrongroup.webtest.parameter;

import com.dynacrongroup.webtest.WebDriverBase;
import com.dynacrongroup.webtest.browser.Browser;
import com.rits.cloning.Cloner;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * User: yurodivuie
 * Date: 10/17/12
 * Time: 10:53 PM
 */
public class ParameterCombinationFactoryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParameterCombinationFactoryTest.class);

    Map<String, Object> driverConfigMap;
    Map<String, Object> testConfigMap;
    Map<String, Object> parameterMap;
    Config testConfig;
    Cloner cloner = new Cloner();

    public class TestClass extends WebDriverBase {
        public TestClass(TestCombo p) {
            super(p);
        }
    }

    @Before
    public void initializeTestConfig() {
        testConfigMap = new HashMap<String, Object>();
        parameterMap = new HashMap<String, Object>();
        driverConfigMap = new HashMap<String, Object>();
    }

    @Test
    public void testAllConfigsWithoutParameters() {
        testConfigMap.put("parameters", parameterMap);
        testConfigMap.put("combination-strategy", "all");
        testConfig = ConfigFactory.parseMap(testConfigMap);
        ParameterCombinationFactory factory = new ParameterCombinationFactory(TestClass.class);
        factory.setConfig(testConfig);
        List<TestCombo> parameters = factory.make();
        assertThat(parameters).hasSize(0);
    }

    @Test
    public void testAllConfigsSingleParameterSingleValue() {
        driverConfigMap.put("browser", "safari");
        driverConfigMap.put("type", "remote");
        parameterMap.put("webDriverConfig", driverConfigMap);
        testConfigMap.put("parameters", parameterMap);
        testConfigMap.put("combination-strategy", "all");
        testConfig = ConfigFactory.parseMap(testConfigMap);

        ParameterCombinationFactory factory = new ParameterCombinationFactory(TestClass.class);
        factory.setConfig(testConfig);
        List<TestCombo> parameters = factory.make();

        assertThat(parameters).hasSize(1);
        TestCombo parameterCombination = parameters.get(0);
        assertThat(parameterCombination.getWebDriverConfig().getBrowser()).isEqualTo(Browser.SAFARI);
    }

    @Test
    public void testAllConfigsSingleParameterTwoValues() {
        List<String> driverList = Arrays.asList("firefox", "chrome");
        List<Map<String, Object>> driverConfigs = new ArrayList<Map<String, Object>>();
        for( String browser : driverList) {
            driverConfigMap.put("browser", browser);
            driverConfigs.add(cloner.deepClone(driverConfigMap));
        }
        parameterMap.put("webDriverConfig",driverConfigs);
        testConfigMap.put("parameters", parameterMap);
        testConfigMap.put("combination-strategy", "all");
        testConfig = ConfigFactory.parseMap(testConfigMap);

        ParameterCombinationFactory factory = new ParameterCombinationFactory(TestClass.class);
        factory.setConfig(testConfig);
        List<TestCombo> parameters = factory.make();

        assertThat(parameters).hasSize(2);
        for (TestCombo parameterCombination : parameters ) {
            assertThat(driverList).contains(parameterCombination.getWebDriverConfig().getBrowser().name().toLowerCase());
        }
    }

    @Test
    public void testAllConfigsTwoParametersTwoValues() {
        List<String> driverList = Arrays.asList("firefox", "chrome");
        List<Map<String, Object>> driverConfigs = new ArrayList<Map<String, Object>>();
        for( String browser : driverList) {
            driverConfigMap.put("browser", browser);
            driverConfigs.add(cloner.deepClone(driverConfigMap));
        }
        parameterMap.put("webDriverConfig",driverConfigs);
        List<String> languageList = Arrays.asList("en-us", "fr-FR");
        parameterMap.put("browserLocale", languageList);
        testConfigMap.put("parameters", parameterMap);
        testConfigMap.put("combination-strategy", "all");
        testConfig = ConfigFactory.parseMap(testConfigMap);

        ParameterCombinationFactory factory = new ParameterCombinationFactory(TestClass.class);
        factory.setConfig(testConfig);
        List<TestCombo> parameters = factory.make();

        assertThat(parameters).hasSize(4);
        for (TestCombo parameterCombination : parameters ) {
            assertThat(driverList).contains(parameterCombination.getWebDriverConfig().getBrowser().name().toLowerCase());
            assertThat(languageList).contains(parameterCombination.getBrowserLocale().toString());
        }
    }

    @Test
    public void testAllConfigsThreeParametersTwoValues() {
        List<String> driverList = Arrays.asList("firefox", "chrome");
        List<Map<String, Object>> driverConfigs = new ArrayList<Map<String, Object>>();
        for( String browser : driverList) {
            driverConfigMap.put("browser", browser);
            driverConfigs.add(cloner.deepClone(driverConfigMap));
        }
        parameterMap.put("webDriverConfig",driverConfigs);
        List<String> languageList = Arrays.asList("en-us", "fr-FR");
        parameterMap.put("browserLocale", languageList);
        List<String> customParamList = Arrays.asList("p1", "p2");
        parameterMap.put("param", customParamList);
        testConfigMap.put("parameters", parameterMap);
        testConfigMap.put("combination-strategy", "all");
        testConfig = ConfigFactory.parseMap(testConfigMap);

        ParameterCombinationFactory factory = new ParameterCombinationFactory(TestClass.class);
        factory.setConfig(testConfig);
        List<TestCombo> parameters = factory.make();

        assertThat(parameters).hasSize(8);
        for (TestCombo parameterCombination : parameters ) {
            assertThat(driverList).contains(parameterCombination.getWebDriverConfig().getBrowser().name().toLowerCase());
            assertThat(languageList).contains(parameterCombination.getBrowserLocale().toString());
        }
    }

    @Test
    public void testAllPairsThreeParametersTwoValues() {
        List<String> driverList = Arrays.asList("firefox", "chrome");
        List<Map<String, Object>> driverConfigs = new ArrayList<Map<String, Object>>();
        for( String browser : driverList) {
            driverConfigMap.put("browser", browser);
            driverConfigs.add(cloner.deepClone(driverConfigMap));
        }
        parameterMap.put("webDriverConfig",driverConfigs);
        List<String> languageList = Arrays.asList("en-us", "fr-FR");
        parameterMap.put("browserLocale", languageList);
        List<String> customParamList = Arrays.asList("p1", "p2");
        parameterMap.put("param", customParamList);
        testConfigMap.put("parameters", parameterMap);
        testConfigMap.put("combination-strategy", "all-pairs");
        testConfig = ConfigFactory.parseMap(testConfigMap);

        ParameterCombinationFactory factory = new ParameterCombinationFactory(TestClass.class);
        factory.setConfig(testConfig);
        List<TestCombo> parameters = factory.make();

        assertThat(parameters).hasSize(4);
        for (TestCombo parameterCombination : parameters ) {
            LOGGER.info(parameterCombination.toString());
            assertThat(driverList).contains(parameterCombination.getWebDriverConfig().getBrowser().name().toLowerCase());
            assertThat(languageList).contains(parameterCombination.getBrowserLocale().toString());
        }
    }

    @Test
    public void testAllPairsFourParametersDifferentValueSizes() {
        List<String> driverList = Arrays.asList("firefox", "chrome");
        List<Map<String, Object>> driverConfigs = new ArrayList<Map<String, Object>>();
        for( String browser : driverList) {
            driverConfigMap.put("browser", browser);
            driverConfigs.add(cloner.deepClone(driverConfigMap));
        }
        parameterMap.put("webDriverConfig",driverConfigs);
        List<String> languageList = Arrays.asList("en-us", "fr-FR", "es-US", "lang");
        parameterMap.put("browserLocale", languageList);
        List<String> customParamList = Arrays.asList("p1", "p2", "p3");
        parameterMap.put("param", customParamList);
        List<String> customAnotherParamList = Arrays.asList("ap1", "ap2", "ap3", "ap4", "ap5");
        parameterMap.put("anotherParam", customAnotherParamList);
        testConfigMap.put("parameters", parameterMap);
        testConfigMap.put("combination-strategy", "all-pairs");
        testConfig = ConfigFactory.parseMap(testConfigMap);

        ParameterCombinationFactory factory = new ParameterCombinationFactory(TestClass.class);
        factory.setConfig(testConfig);
        List<TestCombo> parameters = factory.make();

        assertThat(parameters).hasSize(20);
        int i = 1;
        for (TestCombo parameterCombination : parameters ) {
            LOGGER.info("{}: {}",i, parameterCombination.toString());
            assertThat(driverList).contains(parameterCombination.getWebDriverConfig().getBrowser().name().toLowerCase());
            assertThat(languageList).contains(parameterCombination.getBrowserLocale().toString());
            i++;
        }
    }

}
