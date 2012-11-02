package com.dynacrongroup.webtest.parameter;

import com.dynacrongroup.webtest.util.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.rits.cloning.Cloner;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class figures out which WebDriver(s) to set up.
 */
public final class ParameterCombinationFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ParameterCombinationFactory.class);
    private static final String parametersKey = "parameters";
    private static final Cloner cloner = new Cloner();
    private static ObjectMapper mapper = new ObjectMapper();

    private Config config;
    private Class testClass;
    private Class parameterCombinationClass;

    public ParameterCombinationFactory(Class testClass) {
        this.testClass = testClass;
        this.config = Configuration.getConfigForClass(testClass);
        this.parameterCombinationClass = inferParameterCombinationClass();
    }

    public <T extends ParameterCombination> List<T> make() {
        return convertToParameterCombinations(config.getConfig(parametersKey));
    }

    private Class inferParameterCombinationClass() {
        List<Constructor> constructors = Arrays.asList(testClass.getDeclaredConstructors());
        for (Constructor constructor : constructors) {
            for (Class constructorArgumentClass : constructor.getParameterTypes()) {
                if (ParameterCombination.class.isAssignableFrom(constructorArgumentClass)) {
                    return constructorArgumentClass;
                }
            }
        }
        throw new ExceptionInInitializerError(
                String.format("Test class %s did not have constructor with argument assignable from ParameterCombination", testClass.getSimpleName()));
    }

    @VisibleForTesting
    <T extends ParameterCombination> List<T> convertToParameterCombinations(Config config) {
        Map<String, List<ConfigValue>> rawParameterLists = getParameterLists(config);
        return getCombinations(rawParameterLists);
    }

    private static Map<String, List<ConfigValue>> getParameterLists(Config config) {
        Map<String, List<ConfigValue>> parameterLists = new HashMap<String, List<ConfigValue>>();
        for (Map.Entry<String, ConfigValue> parameterEntry : config.root().entrySet()) {
            parameterLists.put(parameterEntry.getKey(), convertToList(parameterEntry.getValue()));
        }
        return parameterLists;
    }

    private static List<ConfigValue> convertToList(ConfigValue configValue) {
        List<ConfigValue> list;
        if (configValue.valueType().equals(ConfigValueType.LIST)) {
            list = (ConfigList) configValue;
        }
        else {
            list = Arrays.asList(configValue);
        }
        return list;
    }

    private <T extends ParameterCombination> List<T> getCombinations(Map<String, List<ConfigValue>> rawParameterLists) {
        List<T> combinations = new ArrayList<T>();
        if (!rawParameterLists.isEmpty()) {
            String currentKey = getFirstKey(rawParameterLists);
            List<ConfigValue> currentList = rawParameterLists.remove(currentKey);
            List<T> combinationsOfSubList = getCombinations(rawParameterLists);
            for (ConfigValue currentValue : currentList) {
                combinations.addAll(addEntryToPermutations(currentKey, currentValue, combinationsOfSubList));
            }
        }
        return combinations;
    }

    private static String getFirstKey(Map<String, List<ConfigValue>> map) {
        return map.keySet().toArray(new String[0])[0];
    }

    private <T extends ParameterCombination> List<T> addEntryToPermutations(String currentKey, ConfigValue configValue, List<T> combinations) {
        List<T> newCombinations = new ArrayList<T>();
        if (combinations.size() > 0) {
            for (T parameterCombination : combinations) {
                T newParameterCombination = addParameterToParameterCombination(currentKey, configValue, parameterCombination);
                newCombinations.add(newParameterCombination);
            }
        } else {
            T newParameterCombination = createParameterCombination(parameterCombinationClass);
            newParameterCombination = addParameterToParameterCombination(currentKey, configValue, newParameterCombination);
            newCombinations.add(newParameterCombination);
        }
        return newCombinations;
    }

    private <T extends ParameterCombination> T addParameterToParameterCombination(String currentKey, ConfigValue configValue, T parameterCombination) {
        T newParameterCombination = cloner.deepClone(parameterCombination);

        try {
            Class propertyType = PropertyUtils.getPropertyType(newParameterCombination, currentKey);
            LOG.trace(currentKey + ":" + configValue.render(ConfigRenderOptions.concise()));
            Object newProperty = mapper.readValue(configValue.render(ConfigRenderOptions.concise()), propertyType);
            LOG.trace("about to write to combination");
/*            PropertyUtils.getWriteMethod(PropertyUtils.getPropertyDescriptor(newParameterCombination, currentKey))
                    .invoke(newParameterCombination,newProperty);*/
            PropertyUtils.setProperty(newParameterCombination, currentKey, newProperty);
        } catch (Exception e) {
            LOG.warn("{} does not support writing to parameter \'{}\'", parameterCombinationClass.getSimpleName(), currentKey + ": " + e.getMessage());
        }

        return newParameterCombination;
    }

    private void logUnsupportedParameter(String parameter) {
    }

    private static <T extends ParameterCombination> T createParameterCombination(Class parameterCombinationClass) {
        T combination = null;
        try {
            combination = (T) parameterCombinationClass.newInstance();
        } catch (Exception ex) {
            LOG.error("Failed to instantiate {}: {}", parameterCombinationClass.getSimpleName(), ex.getMessage());
        }
        return combination;
    }

}
