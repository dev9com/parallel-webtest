package com.dev9.webtest;

import org.junit.*;
import org.junit.rules.TestName;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JUnitOrderTest {

    private static final Logger log = LoggerFactory.getLogger(JUnitOrderTest.class);

    private static int count = 0;

    /**
     * Shows how rules are intermingled with the process.
     */
    @Rule
    public TestWatcher testWatcher = new LogWatcher();


    class LogWatcher extends TestWatcher {

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
    }

    ;

    @Rule
    public TestName name = new TestName();

    public JUnitOrderTest() {
        log.info("constructor");

    }

    @BeforeClass
    public static void beforeClass() {
        log.info("beforeClass");
    }

    @Before
    public void before() {
        count++;
        log.info("annotation: before method: " + count);
    }

    @After
    public void after() {
        log.info("annotation: after method: " + count);
    }

    @AfterClass
    static public void afterClass() {
        log.info("afterClass: test methods executed: " + count);
    }

    @Test
    public void test1() {
        log.info("test1");
    }

    @Test
    public void test2() {
        log.info("test2");
    }

    @Ignore
    public void notAtest() {
    }

}
