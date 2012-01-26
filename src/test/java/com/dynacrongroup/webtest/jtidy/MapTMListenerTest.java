package com.dynacrongroup.webtest.jtidy;

import com.dynacrongroup.webtest.ParallelRunner;
import com.dynacrongroup.webtest.WebDriverBase;
import com.dynacrongroup.webtest.util.Path;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.tidy.Tidy;
import org.w3c.tidy.TidyMessage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.TreeMap;

import static org.hamcrest.CoreMatchers.startsWith;

/**
 * Created by IntelliJ IDEA.
 * User: yurodivuie
 * Date: 1/25/12
 * Time: 9:05 AM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(ParallelRunner.class)
public class MapTMListenerTest extends WebDriverBase {

    private static final Logger LOG = LoggerFactory.getLogger(MapTMListenerTest.class);

    Path p = new Path("www.dynacrongroup.com", 80);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public MapTMListenerTest(String browser, String browserVersion) {
        super(browser, browserVersion);
    }

    @Before
    public void loadPage() {
        driver.get(p._("/webtest.html"));
    }

    @Test
    public void verifyJobUrlContainsSessionID() throws Throwable {
        
        thrown.expect(AssertionError.class);
        thrown.expectMessage(startsWith("JTidy has reported messages with level [INFO] or greater"));
        
        Tidy tidy = new Tidy();
        MapTMListener listener = new MapTMListener(new TreeMap<TidyMessage.Level, List<TidyMessage>>(), TidyMessage.Level.INFO);
        tidy.setMessageListener(listener );
        tidy.setQuiet(true);
        tidy.setTrimEmptyElements(false);
        tidy.parse(new ByteArrayInputStream(driver.getPageSource().getBytes()), new ByteArrayOutputStream());
        listener.verify();
    }


}
