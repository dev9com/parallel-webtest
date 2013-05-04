package com.dynacrongroup.webtest.parameter;

import org.apache.commons.lang.StringUtils;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This is modification of org.junit.runners.Parameterized to descriptively parameterize tests.
 * The purpose of this modification is to use parameters in the test names instead of set number,
 * so that failures can be traced back to the parameters used.
 * <p/>
 * Note that this class must be used with String parameters; it's not intended for general use,
 * only for use with ParallelRunner and WebDriverBase.
 *
 * To back out the inclusion of this class, you'll need to change the extension for ParallelRunner
 * back to "Parameterized" and change the annotation in WebDriverBase back to "Parameterized".
 */

@SuppressWarnings("all")
public class ParameterCombinationRunner extends Suite {

    private static final Logger LOG = LoggerFactory.getLogger(ParameterCombinationRunner.class);

    /**
     * Annotation for a method which provides parameters to be injected into the
     * test class constructor by <code>ParameterCombinationRunner</code>
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public static @interface Parameters {
    }

    private class TestClassRunnerForParameters extends
            BlockJUnit4ClassRunner {
        private final int fParameterSetNumber;

        private final List<Object> fParameterList;

        private final String fParameterDescription;

        TestClassRunnerForParameters(Class<?> type,
                                     List<Object> parameterList, int i) throws InitializationError {
            super(type);
            fParameterList = parameterList;
            fParameterSetNumber = i;
            fParameterDescription = describeParams();
        }

        /**
         * Modified version from Parameterized to pad with nulls if list is short.
         * @return
         * @throws Exception
         */
        @Override
        public Object createTest() throws Exception {
            Constructor<?> testConstructor = getTestClass().getOnlyConstructor();
            List<Object> params = new ArrayList<Object>();
            params.addAll(Arrays.asList(computeParams()));

            if (params.size() > testConstructor.getParameterTypes().length) {
                LOG.error("Parameter count exceeds constructor parameters; trimming extra parameters.");
                LOG.error("For WebDriverBase tests, this likely means the platform was specified, but the test class" +
                        " did not use a constructor supporting platforms.");
                while(testConstructor.getParameterTypes().length < params.size()) {
                    params.remove(params.size() - 1);
                }
            }

            while (testConstructor.getParameterTypes().length > params.size()) {
                params.add(null);
            }

            return testConstructor.newInstance(params.toArray());
        }

        private Object computeParams() throws Exception {
            try {
                return fParameterList.get(fParameterSetNumber);
            } catch (ClassCastException e) {
                throw new Exception(String.format(
                        "%s.%s() must return a Collection of arrays.",
                        getTestClass().getName(), getParametersMethod(
                        getTestClass()).getName()));
            }
        }

        /* Try to describe the parameters by converting to String; if this
         *  fails, fall back to describing using fParameterSetNumber
         */
        private String describeParams() {
            String returnValue;

            try {
                Object params = computeParams();
                returnValue = params.toString();
            } catch (Exception e) {
                returnValue = null;
            }

            if (returnValue == null) {
                returnValue = ((Integer) fParameterSetNumber).toString();
            }

            return returnValue;
        }

        /**
         * Override this method to provide custom parameter formatting.
         * @param params
         * @return A formatted string to be appended to the test name.
         */
        public String formatParams(Object params) {
            String formattedParams;
            String[] stringParams = (String[]) params;
            if (stringParams[1].contains("Driver")) {
                String driver = stringParams[1];
                formattedParams = driver.substring(driver.lastIndexOf(".") + 1, driver.lastIndexOf("Driver"));
            }
            else {

            formattedParams = StringUtils.join(stringParams, "|");
            }
            return formattedParams;
        }

        @Override
        protected String getName() {
            return String.format("[%s]", fParameterDescription);
        }

        @Override
        protected void validateConstructor(List<Throwable> errors) {
            validateOnlyOneConstructor(errors);
        }

        @Override
        protected Statement classBlock(RunNotifier notifier) {
            return childrenInvoker(notifier);
        }

        @Override
        protected Annotation[] getRunnerAnnotations() {
            return new Annotation[0];
        }
    }

    private final ArrayList<Runner> runners = new ArrayList<Runner>();

    /**
     * Only called reflectively. Do not use programmatically.
     */
    public ParameterCombinationRunner(Class<?> klass) throws Throwable {
        super(klass, Collections.<Runner>emptyList());
        List<Object> parametersList = getParametersList(getTestClass());
        for (int i = 0; i < parametersList.size(); i++)
            runners.add(new TestClassRunnerForParameters(getTestClass().getJavaClass(),
                    parametersList, i));
    }

    @Override
    protected List<Runner> getChildren() {
        return runners;
    }

    @SuppressWarnings("unchecked")
    private List<Object> getParametersList(TestClass klass)
            throws Throwable {
        return (List<Object>) getParametersMethod(klass).invokeExplosively(
                null, klass.getJavaClass());
    }

    private FrameworkMethod getParametersMethod(TestClass testClass)
            throws Exception {
        List<FrameworkMethod> methods = testClass
                .getAnnotatedMethods(Parameters.class);
        for (FrameworkMethod each : methods) {
            int modifiers = each.getMethod().getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers))
                return each;
        }

        throw new Exception("No public static parameters method on class "
                + testClass.getName());
    }

}
