package com.dynacrongroup.webtest.base;

import com.dynacrongroup.webtest.util.Configuration;
import com.google.common.annotations.VisibleForTesting;
import com.rits.cloning.Cloner;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class figures out which WebDriver(s) to set up.
 */
public final class ParameterFactory {

    private ParameterFactory() {
        throw new IllegalAccessError("utility class should not be constructed");
    }

    private static final String parametersKey = "parameters";
    private static final Cloner cloner = new Cloner();

    public static List<Map<String, ConfigValue>> buildParameters(Class testClass) {
        Config config = Configuration.getConfigForClass(testClass);
        return convertToParameters(config.getConfig(parametersKey));
    }

    @VisibleForTesting
    static List<Map<String, ConfigValue>> convertToParameters(Config config) {
        Map<String, List<ConfigValue>> rawParameterLists = getParameterLists(config);
        return getPermutations(rawParameterLists);
    }

    private static Map<String, List<ConfigValue>> getParameterLists(Config config) {
        Map<String, List<ConfigValue>> parameterLists = new HashMap<String, List<ConfigValue>>();
        for (Map.Entry<String, ConfigValue> parameterEntry : config.entrySet()) {
            parameterLists.put(parameterEntry.getKey(), convertToList(parameterEntry.getValue()));
        }
        return parameterLists;
    }

    private static List<ConfigValue> convertToList(ConfigValue configValue) {
        List<ConfigValue> list;
        if (configValue.valueType().equals(ConfigValueType.LIST)) {
            list = (ConfigList) configValue;
        } else {
            list = Arrays.asList(configValue);
        }
        return list;
    }

    private static List<Map<String, ConfigValue>> getPermutations(Map<String, List<ConfigValue>> rawParameterLists) {
        List<Map<String, ConfigValue>> permutationsOfList = new ArrayList<Map<String, ConfigValue>>();
        if (!rawParameterLists.isEmpty()) {
            String currentKey = rawParameterLists.keySet().toArray(new String[0])[0];
            List<ConfigValue> currentList = rawParameterLists.remove(currentKey);
            List<Map<String, ConfigValue>> permutationsOfSubList = getPermutations(rawParameterLists);
            for (ConfigValue currentValue : currentList) {
                permutationsOfList.addAll(addEntryToPermutations(currentKey, currentValue, permutationsOfSubList));
            }
        }
        return permutationsOfList;
    }

    private static List<Map<String, ConfigValue>> addEntryToPermutations(String currentKey, ConfigValue configValue, List<Map<String, ConfigValue>> permutations) {
        List<Map<String, ConfigValue>> newPermutations = new ArrayList<Map<String, ConfigValue>>();
        if (permutations.size() > 0 ) {
            for (Map<String, ConfigValue> parameterEntry : permutations) {
                Map<String, ConfigValue> parameterEntryClone = cloner.deepClone(parameterEntry);
                parameterEntryClone.put(currentKey, configValue);
                newPermutations.add(parameterEntryClone);
            }
        } else {
            Map<String, ConfigValue> firstParameterMap = new HashMap<String, ConfigValue>();
            firstParameterMap.put(currentKey, configValue);
            newPermutations.add(firstParameterMap);
        }
        return newPermutations;
    }
}
