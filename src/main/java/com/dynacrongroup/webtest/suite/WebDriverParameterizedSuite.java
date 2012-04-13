package com.dynacrongroup.webtest.suite;

import com.dynacrongroup.webtest.WebDriverBase;
import com.dynacrongroup.webtest.WebDriverParameterFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.internal.runners.statements.RunAfters;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.runner.Runner;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

import java.io.File;
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
    private final Class<?>[] testClasses;

    /**
     * Used by JUnit
     */
    public WebDriverParameterizedSuite(Class<?> suiteClass) throws InitializationError {
        super(suiteClass, Collections.<Runner>emptyList());
        List<String[]> parametersList = WebDriverParameterFactory.getDriverTargets();
        testClasses = getSortedTestClasses();

        for (int i = 0; i < parametersList.size(); i++) {
            runners.add( new SingleBrowserSuite(parametersList.get(i) , testClasses ) );
        }
    }

    @Override
    protected List<Runner> getChildren() {
        return runners;
    }

    @Override
    protected Statement withBeforeClasses(Statement statement) {
        List<FrameworkMethod> befores =  new ArrayList<FrameworkMethod>();
        for (Class testClass : testClasses) {
            befores.addAll(new TestClass(testClass).getAnnotatedMethods(BeforeClass.class));
        }
        return befores.isEmpty() ? statement :
                new RunBefores(statement, befores, null);
    }

    @Override
    protected Statement withAfterClasses(Statement statement) {
        List<FrameworkMethod> afters= new ArrayList<FrameworkMethod>();
        for (Class testClass : testClasses) {
            afters.addAll(new TestClass(testClass).getAnnotatedMethods(AfterClass.class));
        }
        return afters.isEmpty() ? statement :
                new RunAfters(statement, afters, null);
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
        String classPath = System.getProperty(DEFAULT_CLASSPATH_PROPERTY);
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
