package com.dynacrongroup.webtest;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;

/**
 * User: yurodivuie
 * Date: 3/15/12
 * Time: 3:39 PM
 */
public class WebDriverParameterFactoryTest {

    @Test
    public void testByClass() {
        String testDriver = "org.openqa.selenium.firefox.FirefoxDriver";
        String testSauce = "iexplore:8";
        WebDriverParameterFactory parameterFactory = new WebDriverParameterFactory();

        parameterFactory.classDriver = testDriver;
        parameterFactory.singleSauce = testSauce;
        parameterFactory.createDriverTargets();

        assertThat(parameterFactory.getDriverTargets(), hasItem(new String[]{WebDriverParameterFactory.BY_CLASS, testDriver}));
    }

    @Test
    public void testSingleSauce() {
        String testDriver = null;
        String testSauce = "iexplore:8";
        WebDriverParameterFactory parameterFactory = new WebDriverParameterFactory();

        parameterFactory.classDriver = testDriver;
        parameterFactory.singleSauce = testSauce;
        parameterFactory.createDriverTargets();

        assertThat(parameterFactory.getDriverTargets(), hasItem(testSauce.split(":")));
    }

    @Test
    public void testDefault() {
        String testDriver = null;
        String testSauce = null;
        WebDriverParameterFactory parameterFactory = new WebDriverParameterFactory();

        parameterFactory.classDriver = testDriver;
        parameterFactory.singleSauce = testSauce;
        parameterFactory.createDriverTargets();

        String firstDefaultTarget = WebDriverParameterFactory.NO_DEFAULT_SPECIFIED_TARGETS.split(",")[0];
        assertThat(parameterFactory.getDriverTargets(), hasItem(firstDefaultTarget.split(":")));
    }
}
