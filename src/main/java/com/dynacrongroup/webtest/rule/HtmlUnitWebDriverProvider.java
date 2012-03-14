package com.dynacrongroup.webtest.rule;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class manages the driver life cycle.
 * <p/>
 * User: yurodivuie
 * Date: 3/11/12
 * Time: 9:56 AM
 */
public class HtmlUnitWebDriverProvider extends AbstractWebDriverProvider implements WebDriverProvider {

    private static final Logger LOG = LoggerFactory.getLogger(HtmlUnitWebDriverProvider.class);

    public HtmlUnitWebDriverProvider(WebDriver driver) {
        super(driver);
    }

    void reportStartUp() {
        LOG.info("HtmlUnitDriver ready");
    }

    void reportShutDown() {
        LOG.info("HtmlUnitDriver shut down.");
    }
}
