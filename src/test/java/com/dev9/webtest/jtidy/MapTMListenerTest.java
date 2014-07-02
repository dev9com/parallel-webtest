package com.dev9.webtest.jtidy;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.tidy.Tidy;
import org.w3c.tidy.TidyMessage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.TreeMap;

import static org.hamcrest.CoreMatchers.startsWith;

public class MapTMListenerTest  {

    private static final Logger LOG = LoggerFactory.getLogger(MapTMListenerTest.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void verifyMapListenerCollectsErrors() throws Throwable {

        thrown.handleAssertionErrors();
        thrown.expect(AssertionError.class);
        thrown.expectMessage(startsWith("JTidy has reported messages with level [INFO] or greater"));
        String testHtml = "<!DOCTYPE html>" +
                "<head>" +
                "</head>" +
                "<body></body>" +
                "</html>";

        Tidy tidy = new Tidy();
        MapTMListener listener = new MapTMListener(new TreeMap<TidyMessage.Level, List<TidyMessage>>(), TidyMessage.Level.INFO);
        tidy.setMessageListener(listener );
        tidy.setQuiet(true);
        tidy.setTrimEmptyElements(false);
        tidy.parse(new ByteArrayInputStream(testHtml.getBytes()), new ByteArrayOutputStream());
        listener.verify();
    }


}
