package com.dynacrongroup.webtest;

import org.junit.*;
import org.junit.rules.TestName;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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
public class SimpleTest {
    private static final Logger log = LoggerFactory.getLogger(SimpleTest.class);

    private static int count = 0;

    /**
     * Shows how rules are intermingled with the process.
     */
    @Rule
    public TestWatcher testWatcher = new LogWatcher();


    class LogWatcher extends TestWatcher{

        LogWatcher() {
            log.info("rule: constructor");
        }

        @Override
        public Statement apply(Statement base, Description description) {
            return super.apply(base, description);    //To change body of overridden methods use File | Settings | File Templates.
        }

        @Override
        protected void succeeded(Description description) {
            log.info("rule: succeeded");
        }

        @Override
        protected void failed(Throwable e, Description description) {
            log.info("rule: failed");
        }

        @Override
        protected void starting(Description description) {
            log.info("rule: starting");
        }

        @Override
        protected void finished(Description description) {
            log.info("rule: finished");
        }
    };

    @Rule
    public TestName name = new TestName();
    private final String parameter;

    public SimpleTest(String paramter) {
	this.parameter = paramter;
	log.info("constructor / " + parameter);

    }

    @BeforeClass
    public static void beforeClass() {
	log.info("beforeClass");
    }

    @Before
    public void before() {
	count++;
	log.info("before [" + count + "]");
    }

    @After
    public void after() {
	log.info("after");
	count--;
    }

    @AfterClass
    static public void afterClass() {
	log.info("afterClass (" + count + ")");
    }

    @Test
    public void checkSomething() {
	log.info("test / " + name.getMethodName() + " / " + parameter);
    }

    @Test
    public void secondCheckSomething() {
	log.info("test2 / " + parameter);
    }

    @Ignore
    public void notAtest() {
    }

    @Parameters
    static public List<String[]> parameters() {
	log.info("parameters");
	List<String[]> result = new ArrayList<String[]>();
	result.add(new String[] { "one" });
	result.add(new String[] { "two" });
	return result;
    }
}
