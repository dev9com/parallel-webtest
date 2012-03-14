package com.dynacrongroup.webtest.rule;

import org.junit.runner.Description;
import org.openqa.selenium.WebDriver;

/**
 * Class manages the driver life cycle.
 * <p/>
 * User: yurodivuie
 * Date: 3/11/12
 * Time: 9:56 AM
 */
abstract class AbstractWebDriverProvider extends ClassFinishRule implements WebDriverProvider {

    WebDriver driver;

    public AbstractWebDriverProvider(WebDriver driver) {
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
