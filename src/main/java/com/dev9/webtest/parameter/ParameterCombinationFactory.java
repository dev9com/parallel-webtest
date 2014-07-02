package com.dev9.webtest.parameter;

import com.dev9.webtest.util.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.rits.cloning.Cloner;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;
//import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.SetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * This class figures out which WebDriver(s) to set up.
 */
public final class ParameterCombinationFactory {

    private static final String ALL_COMBINATIONS_STRATEGY = "all";
    private static final String ALL_PAIRS_STRATEGY = "all-pairs";
    private static final String EACH_ONCE_STRATEGY = "each-once";
    private static final Logger LOG = LoggerFactory.getLogger(ParameterCombinationFactory.class);
    private static final String parametersKey = "parameters";
    private static final Cloner cloner = new Cloner();
    private static ObjectMapper mapper = new ObjectMapper();

    private Config config;
    private Class testClass;
    private Class parameterCombinationClass;

    public ParameterCombinationFactory(Class testClass) {
        this.testClass = testClass;
        setConfig(Configuration.getConfigForClass(testClass));
        this.parameterCombinationClass = inferParameterCombinationClass();
    }

    public <T extends ParameterCombination> List<T> make() {
        Map<String, List<ConfigValue>> rawParameterLists = getParameterLists();
        return getCombinations(rawParameterLists);
    }

    @VisibleForTesting
    protected void setConfig(Config config) {
        this.config = config;
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
                String.format("Test class %s did not have constructor with arg assignable from ParameterCombination",
                        testClass.getSimpleName()));
    }

    private Map<String, List<ConfigValue>> getParameterLists() {
        Map<String, List<ConfigValue>> parameterLists = new HashMap<String, List<ConfigValue>>();
        for (Map.Entry<String, ConfigValue> parameterEntry : config.getConfig(parametersKey).root().entrySet()) {
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

    private <T extends ParameterCombination> List<T> getCombinations(Map<String, List<ConfigValue>> rawParameterLists) {
        String combinationStrategy = config.getString("combination-strategy");
        List<T> combinations = new ArrayList<T>();
        if (ALL_PAIRS_STRATEGY.equalsIgnoreCase(combinationStrategy)) {
            combinations = getAllPairs(rawParameterLists);
        }
        else if (EACH_ONCE_STRATEGY.equalsIgnoreCase(combinationStrategy)) {
            combinations = getEachOnce(rawParameterLists);
        }
        else {
            combinations = getAllCombinations(rawParameterLists);
        }
        return combinations;
    }



    private <T extends ParameterCombination> List<T> getEachOnce(Map<String, List<ConfigValue>> rawParameterLists) {
        List<T> combinations = new ArrayList<T>();
        Integer largestListSize = rawParameterLists.get(getKeyForLargestList(rawParameterLists)).size();
        Integer offset = Math.abs(new Random().nextInt());
        for (int i = 0; i < largestListSize; i++) {
            T parameterCombination = createParameterCombination(parameterCombinationClass);
            for (String currentProperty : rawParameterLists.keySet()) {
                List<ConfigValue> currentList = rawParameterLists.get(currentProperty);
                Integer index = (i + offset) % currentList.size();
                parameterCombination = addToParameters(currentProperty, currentList.get(index), parameterCombination);
            }
            combinations.add(parameterCombination);
        }
        return combinations;
    }



    private <T extends ParameterCombination> List<T> getAllPairs(Map<String, List<ConfigValue>> rawParameterLists) {
        List<T> combinations = new ArrayList<T>();
        if (rawParameterLists.size() <= 2) {
            combinations = getAllCombinations(rawParameterLists);
        }
        else {
            String largestListKey = getKeyForLargestList(rawParameterLists);
            List<ConfigValue> largestList = rawParameterLists.remove(largestListKey);
            String secondLargestListKey = getKeyForLargestList(rawParameterLists);
            List<ConfigValue> secondLargestList = rawParameterLists.remove(secondLargestListKey);

            for (int i = 0; i < largestList.size(); i++) {
                for (int j = 0; j < secondLargestList.size(); j++) {
                    T combination = createParameterCombination(parameterCombinationClass);
                    combination = addToParameters(largestListKey, largestList.get(i), combination);
                    combination = addToParameters(secondLargestListKey, secondLargestList.get(j), combination);
                    if (!rawParameterLists.isEmpty()) {
                        final Set<String> keys = rawParameterLists.keySet();
                        for (int depth = 0; depth < keys.size(); depth++) {
                            String currentKey = (String) SetUtils.orderedSet(keys).toArray()[depth];
                            List<ConfigValue> currentList = rawParameterLists.get(currentKey);
                            ConfigValue currentValue = currentList.get((j + i ^ depth) % currentList.size());
                            combination = addToParameters(currentKey, currentValue, combination);
                        }
                    }
                    combinations.add(combination);
                }
            }
        }
        return combinations;
    }

    private String getKeyForLargestList(Map<String, List<ConfigValue>> lists) {
        String largestListKey = null;
        if (!lists.isEmpty()) {
            for (String key : lists.keySet()) {
                if (largestListKey == null || lists.get(key).size() > lists.get(largestListKey).size()) {
                    largestListKey = key;
                }
            }
        }
        return largestListKey;
    }

    private <T extends ParameterCombination> List<T> getAllCombinations(Map<String, List<ConfigValue>> rawParameterLists) {
        List<T> combinations = new ArrayList<T>();
        if (!rawParameterLists.isEmpty()) {
            String currentKey = getFirstKey(rawParameterLists);
            List<ConfigValue> currentList = rawParameterLists.remove(currentKey);
            List<T> combinationsOfSubList = getAllCombinations(rawParameterLists);
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
                T newParameterCombination = addToParameters(currentKey, configValue, parameterCombination);
                newCombinations.add(newParameterCombination);
            }
        } else {
            T newParameterCombination = createParameterCombination(parameterCombinationClass);
            newParameterCombination = addToParameters(currentKey, configValue, newParameterCombination);
            newCombinations.add(newParameterCombination);
        }
        return newCombinations;
    }

    private <T extends ParameterCombination> T addToParameters(String currentKey, ConfigValue configValue, T parameterCombination) {
        T newParameterCombination = cloner.deepClone(parameterCombination);
        try {
            final String valueJson = String.format("{\"%s\":%s}", currentKey, configValue.render(ConfigRenderOptions.concise()));
            newParameterCombination = mapper.readerForUpdating(newParameterCombination).readValue(valueJson);
        } catch (Exception e) {
            LOG.warn("{} does not support writing to parameter \'{}\'",
                    parameterCombinationClass.getSimpleName(),
                    currentKey + ": " + e.getMessage());
        }
        return newParameterCombination;
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
