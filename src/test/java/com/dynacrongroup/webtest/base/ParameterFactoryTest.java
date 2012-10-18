package com.dynacrongroup.webtest.base;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import org.junit.Before;
import org.junit.Test;

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
public class ParameterFactoryTest {

    Map<String, Object> testConfigMap;
    Config testConfig;

    @Before
    public void initializeTestConfig() {
        testConfigMap = new HashMap<String, Object>();
    }

    @Test
    public void testWithoutParameters() {
        testConfig = ConfigFactory.parseMap(testConfigMap);
        List<Map<String,ConfigValue>> parameters = ParameterFactory.convertToParameters(testConfig);
        assertThat(parameters).hasSize(0);
    }

    @Test
    public void testSingleParameterSingleValue() {
        testConfigMap.put("driver", "firefox");
        testConfig = ConfigFactory.parseMap(testConfigMap);

        List<Map<String,ConfigValue>> parameters = ParameterFactory.convertToParameters(testConfig);

        assertThat(parameters).hasSize(1);
        Map<String, ConfigValue> parameter = parameters.get(0);
        assertThat(parameter).containsKey("driver");
        Object parameterValue = parameter.get("driver").unwrapped();
        assertThat(parameterValue).isInstanceOf(String.class);
        assertThat((String)parameterValue).isEqualTo((String) testConfigMap.get("driver"));
    }

    @Test
    public void testSingleParameterTwoValues() {
        List<String> driverList = Arrays.asList("firefox", "chrome");
        testConfigMap.put("driver", driverList);
        testConfig = ConfigFactory.parseMap(testConfigMap);

        List<Map<String,ConfigValue>> parameters = ParameterFactory.convertToParameters(testConfig);

        assertThat(parameters).hasSize(2);
        Map<String, ConfigValue> parameter = parameters.get(0);
        assertThat(parameter).containsKey("driver");
        Object parameterValue = parameter.get("driver").unwrapped();
        assertThat(parameterValue).isInstanceOf(String.class);
        assertThat(driverList).contains((String)parameterValue);

        parameter = parameters.get(1);
        assertThat(parameter).containsKey("driver");
        Object parameterValue2 = parameter.get("driver").unwrapped();
        assertThat(parameterValue2).isNotEqualTo(parameterValue);
        assertThat(parameterValue).isInstanceOf(String.class);
        assertThat(driverList).contains((String)parameterValue);
    }

    @Test
    public void testTwoParametersTwoValues() {
        List<String> driverList = Arrays.asList("firefox", "chrome");
        List<String> languageList = Arrays.asList("en", "fr");
        testConfigMap.put("driver", driverList);
        testConfigMap.put("language", languageList);
        testConfig = ConfigFactory.parseMap(testConfigMap);

        List<Map<String,ConfigValue>> parameters = ParameterFactory.convertToParameters(testConfig);

        assertThat(parameters).hasSize(4);
        Map<String, ConfigValue> parameter = parameters.get(0);
        assertThat(parameter).containsKey("driver");
        assertThat(parameter).containsKey("language");
        Object driver = parameter.get("driver").unwrapped();
        Object language = parameter.get("language").unwrapped();
        assertThat(driver).isInstanceOf(String.class);
        assertThat(language).isInstanceOf(String.class);
        assertThat(driverList).contains((String)driver);
        assertThat(languageList).contains((String)language);
    }

}
