package com.dynacrongroup.webtest.jtidy;

import com.dynacrongroup.webtest.ParallelRunner;
import com.dynacrongroup.webtest.WebDriverBase;
import com.dynacrongroup.webtest.util.Path;
import com.google.common.io.NullOutputStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.tidy.Configuration;
import org.w3c.tidy.Tidy;
import org.w3c.tidy.TidyMessage;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: yurodivuie
 * Date: 1/25/12
 * Time: 9:05 AM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(ParallelRunner.class)
public class LogTMListenerTest extends WebDriverBase {
    
    private static final Logger LOG = LoggerFactory.getLogger(LogTMListenerTest.class);

    Path p = new Path("www.dynacrongroup.com", 80);

    public LogTMListenerTest(String browser, String browserVersion) {
        super(browser, browserVersion);
    }

    @Before
    public void loadPage() {
        driver.get(p._("/webtest.html"));
    }

    /**
     * Currently a manual test... shouldn't throw an exception.
     */
    @Test
    public void verifyLoggerLogsErrors() {
        Tidy tidy = new Tidy();
        tidy.setMessageListener(new LogTMListener(LOG, TidyMessage.Level.WARNING));
        tidy.setQuiet(true);
        tidy.setTrimEmptyElements(false);
        tidy.parse(new ByteArrayInputStream(driver.getPageSource().getBytes()), new NullOutputStream());
    }
    
    @Test
    public void showConfigOptions() {
        Configuration configuration = new Tidy().getConfiguration();
        configuration.printConfigOptions(new PrintWriter(System.out), false);
    }


}
