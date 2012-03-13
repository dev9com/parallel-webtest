package com.dynacrongroup.webtest.rule;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: yurodivuie
 * Date: 3/13/12
 * Time: 9:22 AM
 */
public class ClassFinishedRuleTest {

    class TestableClassFinishRule extends ClassFinishRule {

        Boolean classIsFinished = false;

        @Override
        protected void classFinished(Description description) {
            classIsFinished = true;
        }
    }

    TestableClassFinishRule rule;
    Description mockDescription;

    @Before
    public void prepareRuleWithMocks() {
        rule = new TestableClassFinishRule();
        mockDescription = mock(Description.class);
        when(mockDescription.getTestClass()).thenReturn((Class)ClassFinishedRuleTest.class);
    }

    @Test
    public void testClassWithNoTestMethods() {
        assertFalse(rule.classIsFinished);

        rule.methodsRemaining = 0;
        rule.finished(mockDescription);

        assertTrue(rule.classIsFinished);
    }

    @Test
    public void testClassWithTenMethods() {
        final Integer testMethods = 10;
        rule.methodsRemaining = testMethods;

        for (int i = 1; i <= testMethods; i++) {
            assertFalse(rule.classIsFinished);
            rule.finished(mockDescription);
        }

        assertTrue(rule.classIsFinished);
    }

}
