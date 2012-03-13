package com.dynacrongroup.webtest.rule;

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

    class Parent {
        @Test
        public void innerTest() {
             //Test should never be executed
        }

        @Test
        public void anotherInnerTest() {
             //Test should never be executed
        }
    }

    class Sub1 extends Parent {
        //Do not add test methods here.
    }

    class Sub2 extends Sub1 {
        //Do not add test methods here.
    }

    class Sub3 extends Parent {
        @Test
        public void innerTest2 () {
             //Test should never be executed
        }
    }

    @Test
    public void verifyParentclassCount() {
        assertThat("Subclass with no test methods should have the same number of methods as superclass",
                ClassFinishRule.countTestMethods(Parent.class),
                equalTo(2));
    }

    @Test
    public void verifySubclassCount() {
        assertThat("Subclass with no test methods should have the same number of methods as superclass",
                ClassFinishRule.countTestMethods(Sub1.class),
                equalTo(ClassFinishRule.countTestMethods(Parent.class)));
    }

    @Test
    public void verifySubSubclassCount() {
        assertThat("SubSubclass with no test methods should have the same number of methods as superclass",
                ClassFinishRule.countTestMethods(Sub2.class),
                equalTo(ClassFinishRule.countTestMethods(Parent.class)));
    }

    @Test
    public void verifySubclassWithNewMethodCount() {
        assertThat("Subclass with one test method should have one more test method than superclass",
                ClassFinishRule.countTestMethods(Sub3.class),
                equalTo(ClassFinishRule.countTestMethods(Parent.class) + 1));
    }

}
