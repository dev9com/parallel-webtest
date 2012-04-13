package com.dynacrongroup.webtest.suite;

import org.apache.commons.lang.StringUtils;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.junit.runners.model.Statement;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * User: yurodivuie
 * Date: 4/13/12
 * Time: 10:43 AM
 */
public class WebDriverSuiteRunnerBuilder extends RunnerBuilder {


    String[] parameters;


    private class TestClassRunnerForParameter extends
            BlockJUnit4ClassRunner {

        private final String[] fParameters;

        private final String fParameterDescription;

        private final Class<?> type;

        TestClassRunnerForParameter(Class<?> type,
                                    String[] parameters) throws InitializationError {
            super(type);
            this.type = type;
            this.fParameters = parameters;
            fParameterDescription = formatParams(parameters);
        }



        @Override
        public Object createTest() throws Exception {
            return getTestClass().getOnlyConstructor().newInstance(
                    fParameters);
        }

        /**
         * Override this method to provide custom parameter formatting.
         *
         * @param params
         * @return A formatted string to be appended to the test name.
         */
        public String formatParams(String[] params) {
            String formattedParams;
            if (params[1].contains("Driver")) {
                String driver = params[1];
                formattedParams = driver.substring(driver.lastIndexOf(".") + 1, driver.lastIndexOf("Driver"));
            } else {

                formattedParams = StringUtils.join(params, "|");
            }
            return formattedParams;
        }

        @Override
        protected String getName() {
            return String.format("%s[%s]", type.getSimpleName(), fParameterDescription);
        }

        @Override
        protected String testName(final FrameworkMethod method) {
            return String.format("%s[%s]", method.getName(),
                    fParameterDescription);
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

    public WebDriverSuiteRunnerBuilder(String[] parameters) {
        this.parameters = parameters;
    }

    @Override
    public Runner runnerForClass(Class<?> testClass) throws Throwable {
        return new TestClassRunnerForParameter(testClass, parameters);
    }
}
