package com.dynacrongroup.webtest.suite;

import com.dynacrongroup.webtest.WebDriverBase;
import com.dynacrongroup.webtest.WebDriverParameterFactory;
import org.apache.commons.lang.StringUtils;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Based on a cutdown version of ClasspathSuite: http://johanneslink.net/projects/cpsuite.html
 * Mixed with a version of Parameterized test runner from JUnit
 * <p/>
 * Runs all files in the classpath that extend WebDriverBase, with parameters.
 * <p/>
 * User: yurodivuie
 * Date: 4/12/12
 * Time: 2:47 PM
 */
public class WebDriverParameterizedSuite extends Suite {


    private static final int CLASS_SUFFIX_LENGTH = ".class".length();
    private static final String DEFAULT_CLASSPATH_PROPERTY = "java.class.path";

    private final ArrayList<Runner> runners = new ArrayList<Runner>();

    private class TestClassRunnerForParameters extends
            BlockJUnit4ClassRunner {
        private final int fParameterSetNumber;

        private final List<String[]> fParameterList;

        private final String fParameterDescription;

        private final Class<?> type;

        TestClassRunnerForParameters(Class<?> type,
                                     List<String[]> parameterList, int i) throws InitializationError {
            super(type);
            this.type = type;
            fParameterList = parameterList;
            fParameterSetNumber = i;
            fParameterDescription = describeParams();
        }

        @Override
        public Object createTest() throws Exception {
            return getTestClass().getOnlyConstructor().newInstance(
                    computeParams());
        }

        private Object[] computeParams() throws Exception {
            try {
                return fParameterList.get(fParameterSetNumber);
            } catch (ClassCastException e) {
                throw new Exception(String.format(
                        "%s must provide a Collection of arrays.",
                        this.getClass().getName()));
            }
        }

        /* Try to describe the parameters by converting to String; if this
         *  fails, fall back to describing using fParameterSetNumber
         */
        private String describeParams() {
            String returnValue;

            try {
                Object[] params = computeParams();
                returnValue = formatParams(params);
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
         *
         * @param params
         * @return A formatted string to be appended to the test name.
         */
        public String formatParams(Object[] params) {
            String formattedParams;
            String[] stringParams = (String[]) params;
            if (stringParams[1].contains("Driver")) {
                String driver = stringParams[1];
                formattedParams = driver.substring(driver.lastIndexOf(".") + 1, driver.lastIndexOf("Driver"));
            } else {

                formattedParams = StringUtils.join(stringParams, "|");
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


    /**
     * Used by JUnit
     */
    public WebDriverParameterizedSuite(Class<?> suiteClass) throws InitializationError {
        super(suiteClass, Collections.<Runner>emptyList());
        List<String[]> parametersList = WebDriverParameterFactory.getDriverTargets();
        for (int i = 0; i < parametersList.size(); i++) {
            for (Class testClass : getSortedTestClasses()) {
                runners.add(new TestClassRunnerForParameters(testClass,
                        parametersList, i));
            }
        }
    }

    @Override
    protected List<Runner> getChildren() {
        return runners;
    }

    private static Class<?>[] getSortedTestClasses() {
        List<Class<?>> testClasses = findClassesInClasspath();
        Collections.sort(testClasses, getClassComparator());
        return testClasses.toArray(new Class[testClasses.size()]);
    }


    private static Comparator<Class<?>> getClassComparator() {
        return new Comparator<Class<?>>() {
            public int compare(Class<?> o1, Class<?> o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
    }

    private static List<Class<?>> findClassesInClasspath() {
        String classPath = System.getProperty("java.class.path");
        System.out.println(classPath);
        return findClassesInRoots(splitClassPath(classPath));
    }

    private static List<String> splitClassPath(String classPath) {
        final String separator = System.getProperty("path.separator");
        return Arrays.asList(classPath.split(separator));
    }

    private static List<Class<?>> findClassesInRoots(List<String> roots) {
        List<Class<?>> classes = new ArrayList<Class<?>>(100);
        for (String root : roots) {
            gatherClassesInRoot(new File(root), classes);
        }
        return classes;
    }

    private static void gatherClassesInRoot(File classRoot, List<Class<?>> classes) {
        if (classRoot.isDirectory()) {
            Iterable<String> relativeFilenames = new RecursiveFilenameIterator(classRoot);
            gatherClasses(classes, relativeFilenames);
        }
    }

    private static void gatherClasses(List<Class<?>> classes, Iterable<String> filenamesIterator) {
        for (String fileName : filenamesIterator) {
            if (!isClassFile(fileName)) {
                continue;
            }
            String className = classNameFromFile(fileName);
            if (isInnerClass(className)) {
                continue;
            }
            try {
                Class<?> clazz = Class.forName(className);
                if (clazz == null || clazz.isLocalClass() || clazz.isAnonymousClass()) {
                    continue;
                }
                if (WebDriverBase.class.isAssignableFrom(clazz) && !WebDriverBase.class.equals(clazz)) {
                    classes.add(clazz);
                }
            } catch (ClassNotFoundException cnfe) {
                // ignore not instantiable classes
            } catch (NoClassDefFoundError ncdfe) {
                // ignore not instantiable classes
            } catch (ExceptionInInitializerError ciie) {
                // ignore not instantiable classes
            } catch (UnsatisfiedLinkError ule) {
                // ignore not instantiable classes
            }
        }
    }

    private static boolean isInnerClass(String className) {
        return className.contains("$");
    }

    private static boolean isClassFile(String classFileName) {
        return classFileName.endsWith(".class");
    }

    private static String classNameFromFile(String classFileName) {
        // convert /a/b.class to a.b
        String s = replaceFileSeparators(cutOffExtension(classFileName));
        if (s.startsWith("."))
            return s.substring(1);
        return s;
    }

    private static String replaceFileSeparators(String s) {
        String result = s.replace(File.separatorChar, '.');
        if (File.separatorChar != '/') {
            // In Jar-Files it's always '/'
            result = result.replace('/', '.');
        }
        return result;
    }

    private static String cutOffExtension(String classFileName) {
        return classFileName.substring(0, classFileName.length() - CLASS_SUFFIX_LENGTH);
    }

}
