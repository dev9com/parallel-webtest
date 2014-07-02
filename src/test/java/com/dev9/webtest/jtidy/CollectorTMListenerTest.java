package com.dev9.webtest.jtidy;

import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.tidy.Tidy;
import org.w3c.tidy.TidyMessage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

public class CollectorTMListenerTest {

    private static final Logger LOG = LoggerFactory.getLogger(CollectorTMListenerTest.class);

    @Test
    public void verifyCollectorReceivesError() {
        
        ErrorCollector mockCollector = mock(ErrorCollector.class);
        String testHtml = "<!DOCTYPE html>" +
                "<head>" +
                "</head>" +
                "<body></body>" +
                "</html>";
        
        Tidy tidy = new Tidy();
        tidy.setMessageListener(new CollectorTMListener(mockCollector, TidyMessage.Level.WARNING));
        tidy.setQuiet(true);
        tidy.setTrimEmptyElements(false);
        tidy.parse(new ByteArrayInputStream(testHtml.getBytes()), new ByteArrayOutputStream());

        verify(mockCollector, atLeastOnce()).addError(isA(AssertionError.class));
    }

}
