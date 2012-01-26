package com.dynacrongroup.webtest.jtidy;

import com.dynacrongroup.webtest.ParallelRunner;
import com.dynacrongroup.webtest.WebDriverBase;
import com.dynacrongroup.webtest.util.Path;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.tidy.Tidy;
import org.w3c.tidy.TidyMessage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

/**
 * Created by IntelliJ IDEA.
 * User: yurodivuie
 * Date: 1/25/12
 * Time: 9:05 AM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(ParallelRunner.class)
public class CollectorTMListenerTest extends WebDriverBase {

    private static final Logger LOG = LoggerFactory.getLogger(CollectorTMListenerTest.class);

    Path p = new Path("www.dynacrongroup.com", 80);

    public CollectorTMListenerTest(String browser, String browserVersion) {
        super(browser, browserVersion);
    }

    @Before
    public void loadPage() {
        driver.get(p._("/webtest.html"));
    }

    @Test
    public void verifyJobUrlContainsSessionID() {
        
        ErrorCollector mockCollector = mock(ErrorCollector.class);
        
        Tidy tidy = new Tidy();
        tidy.setMessageListener(new CollectorTMListener(mockCollector, TidyMessage.Level.WARNING));
        tidy.setQuiet(true);
        tidy.setTrimEmptyElements(false);
        tidy.parse(new ByteArrayInputStream(driver.getPageSource().getBytes()), new ByteArrayOutputStream());

        verify(mockCollector, atLeastOnce()).addError(isA(AssertionError.class));
    }

}
