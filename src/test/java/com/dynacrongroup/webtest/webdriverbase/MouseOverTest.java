package com.dynacrongroup.webtest.webdriverbase;

import com.dynacrongroup.webtest.base.ParallelRunner;
import com.dynacrongroup.webtest.base.ParameterCombination;
import com.dynacrongroup.webtest.base.WebDriverBase;
import com.dynacrongroup.webtest.util.Path;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

/**
 * Test was added to verify that using native mouse events would not crash the
 * browser.  It's still present for reference.
 *
 * @author drakdr1
 */

@RunWith(ParallelRunner.class)
public class MouseOverTest extends WebDriverBase {

	private static final Logger log = LoggerFactory
			.getLogger(MouseOverTest.class);

    public MouseOverTest(ParameterCombination parameterCombination) {
        super(parameterCombination);
    }

	@Test
	public void nativeEventTest() throws Exception {

        assumeTrue(!getWebDriverConfig().isHtmlUnit());

        driver.get(new Path("www.google.com", 80)._(""));
		WebElement search = driver.findElement(By.name("q"));
		Actions builder = new Actions(driver);
		builder.moveToElement(search);
		builder.perform();
		search.sendKeys("Hello, WebDriver");
		search.submit();
		log.info(">" + driver.getTitle());

		assertNotNull(driver.getTitle());
	}
}

