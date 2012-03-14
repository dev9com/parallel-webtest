package com.dynacrongroup.webtest.rule;

import org.junit.runner.Description;
import org.openqa.selenium.WebDriver;

/**
 * Abstract class for managing the driver life cycle.  Supplies default implementation of getJobUrl
 * and getJobId, which can be overridden if the specific implementation supports these methods.
 * <p/>
 * User: yurodivuie
 * Date: 3/11/12
 * Time: 9:56 AM
 */
abstract class AbstractWebDriverManager extends ClassFinishRule implements WebDriverManager {

    WebDriver driver;

    public AbstractWebDriverManager(WebDriver driver) {
        this.driver = driver;
        reportStartUp();
    }

    @Override
    protected void classFinished(Description description) {
        driver.quit();
        driver = null;
        reportShutDown();
    }

    /**
     * Provides WebDriver to tests using this rule to manage driver LifeCycle.
     * @return
     */
    @Override
    public final WebDriver getDriver() {
        return driver;
    }

    /**
     * Returns jobUrl for remote jobs; null otherwise.
     * @return
     */
    @Override
    public String getJobURL() {
        return null;
    }

    /**
     * Returns the jobId if one exists for this job.
     */
    @Override
    public String getJobId() {
        return null;
    }

    abstract  void reportStartUp();

    abstract void reportShutDown();

}
