package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.WebDriverBase;
import com.dynacrongroup.webtest.test.SystemNameTest;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Used to test the count for the number of test methods remaining.
 *
 * User: yurodivuie
 * Date: 11/22/11
 * Time: 2:20 PM
 */
public class MethodCountTest {

    class Sub extends SystemNameTest {
        //Do not add test methods here.
    }

    @Test
    @Ignore("Issue 3 on github")
    public void verifySubclassCount() {
        assertThat("Subclass with no test methods should have the same number of methods as superclass",
                WebDriverBase.countTestMethods(Sub.class), equalTo(WebDriverBase.countTestMethods(SystemNameTest.class)));
    }


}
