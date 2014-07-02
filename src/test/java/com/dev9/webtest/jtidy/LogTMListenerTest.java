package com.dev9.webtest.jtidy;

import com.google.common.io.NullOutputStream;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.tidy.Tidy;
import org.w3c.tidy.TidyMessage;

import java.io.ByteArrayInputStream;

import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class LogTMListenerTest {
    
    private static final Logger LOG = LoggerFactory.getLogger(LogTMListenerTest.class);

    @Test
    public void verifyLoggerLogsErrors() {
        String testHtml = "<!DOCTYPE html>" +
                "<head>" +
                "</head>" +
                "<body></body>" +
                "</html>";
        
        Logger mockLogger = mock(Logger.class);
        Tidy tidy = new Tidy();
        tidy.setMessageListener(new LogTMListener(mockLogger, TidyMessage.Level.INFO));
        tidy.setQuiet(true);
        tidy.setTrimEmptyElements(false);
        tidy.parse(new ByteArrayInputStream(testHtml.getBytes()), new NullOutputStream());
        verify(mockLogger).info(contains("WARNING: inserting missing 'title'"));
    }


}
