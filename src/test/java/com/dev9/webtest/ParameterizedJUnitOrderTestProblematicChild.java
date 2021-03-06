package com.dev9.webtest;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Sample of the order of execution of JUnit test cases.
 * <p/>
 * The order that various options in JUnit are executed can be confusing. For
 * example, are parameters generated before or after the BeforeClass options are
 * exercised?
 * <p/>
 * This class technically doesn't have a lot to do with Selenium, but I used it
 * to clarify things when developing the Selenium classes, and so here it is for
 * future reference.
 */
@RunWith(value = Parameterized.class)
public class ParameterizedJUnitOrderTestProblematicChild extends ParameterizedJUnitOrderTest {

    private static final Logger log = LoggerFactory
            .getLogger(ParameterizedJUnitOrderTestProblematicChild.class);

    private final String parameter;

    public ParameterizedJUnitOrderTestProblematicChild(String paramter) {
        super(paramter);
        this.parameter = paramter;
        log.info("constructor / " + parameter);
    }

    @BeforeClass
    public static void beforeClassChild() {
        log.info("beforeClassChild");
    }

    @Before
    public void beforeChild() {
        log.info("beforeChild");
    }

    @After
    public void afterChild() {
        log.info("afterChild");
    }

    @AfterClass
    static public void afterClassChild() {
        log.info("afterClassChild");
    }

    @Ignore("Failing. Reason: Bad thing!")
    @Test(expected = NullPointerException.class)
    public void testChild() throws Exception {
        log.info("test child / " + parameter);

        throw new IllegalAccessError("Bad thing!");
    }

    @Parameters
    static public List<String[]> parametersChild() {
        log.info("parametersChild");
        List<String[]> result = new ArrayList<String[]>();
        result.add(new String[]{"one"});
        result.add(new String[]{"two"});
        return result;
    }
}
