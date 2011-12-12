package com.dynacrongroup.webtest.swarm;

import com.dynacrongroup.webtest.ParallelRunner;
import com.dynacrongroup.webtest.WebDriverBase;
import com.dynacrongroup.webtest.util.Path;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import org.openqa.selenium.By;

/**
 * Test used to verify that parallel browsers can be created without crashing.
 */
@RunWith(ParallelRunner.class)
public class Swarm5 extends WebDriverBase {



    Path p = new Path("www.dynacrongroup.com", 80);

    public Swarm5(String browser, String browserVersion) {
        super(browser, browserVersion);
    }

                   @Test
                   public void doSomething() {
                       driver.get( p._(""));
                       assertThat(driver.findElement(By.tagName("body")).getText(), containsString("Dynacron"));

                   }

                   @Test
    public void doAnything() {
        driver.get(p._(""));
        assertThat(driver.findElement(By.tagName("body")).getText(), containsString("Welcome"));
    }

}
