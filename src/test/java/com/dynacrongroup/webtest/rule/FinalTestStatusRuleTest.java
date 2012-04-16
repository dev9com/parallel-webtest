package com.dynacrongroup.webtest.rule;

import com.dynacrongroup.webtest.sauce.SauceREST;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * User: yurodivuie
 * Date: 3/13/12
 * Time: 9:37 AM
 */
public class FinalTestStatusRuleTest {

    private static final Logger LOG = LoggerFactory.getLogger(FinalTestStatusRuleTest.class);

    String testJobId = "1234";
    SauceLabsFinalStatusReporter rule;

    @Before
    public void prepareRuleWithMock() {
        rule = new SauceLabsFinalStatusReporter(LOG, testJobId, "fakeUser", "fakeKey");
        rule.sauceREST = mock(SauceREST.class);
    }

    @Test
    public void testPassed() {
        rule.classFinished(mock(Description.class));
        verify(rule.sauceREST).jobPassed(testJobId);
    }

    @Test
    public void testFailed() {
        rule.failed(new Throwable(), mock(Description.class));
        rule.classFinished(mock(Description.class));
        verify(rule.sauceREST).jobFailed(testJobId);
    }

}
