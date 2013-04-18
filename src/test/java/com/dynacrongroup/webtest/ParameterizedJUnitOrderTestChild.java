package com.dynacrongroup.webtest;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sample of the order of execution of JUnit test cases.
 * 
 * The order that various options in JUnit are executed can be confusing. For
 * example, are parameters generated before or after the BeforeClass options are
 * exercised?
 * 
 * This class technically doesn't have a lot to do with Selenium, but I used it
 * to clarify things when developing the Selenium classes, and so here it is for
 * future reference.
 */
@RunWith(value = Parameterized.class)
public class ParameterizedJUnitOrderTestChild extends ParameterizedJUnitOrderTest {
    private static final Logger log = LoggerFactory
	    .getLogger(ParameterizedJUnitOrderTestChild.class);

    private final String parameter;

    public ParameterizedJUnitOrderTestChild(String paramter) {
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

    @Test
    public void testChild() {
	log.info("testChild / " + name.getMethodName() + " / " + parameter);
    }

    @Parameters
    static public List<String[]> parametersChild() {
	log.info("parametersChild");
	List<String[]> result = new ArrayList<String[]>();
	result.add(new String[] { "one" });
	result.add(new String[] { "two" });
	return result;
    }
}
