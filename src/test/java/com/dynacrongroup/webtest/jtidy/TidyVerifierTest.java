package com.dynacrongroup.webtest.jtidy;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.tidy.TidyMessage;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.startsWith;

public class TidyVerifierTest {

    private static final Logger LOG = LoggerFactory.getLogger(TidyVerifierTest.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void verifyDefaultBuildWithNoErrors() throws Throwable {

        String testHtml = "<!DOCTYPE html>" +
                "<head>" +
                "<title>title</title>" +
                "</head>" +
                "<body></body>" +
                "</html>";

        TidyVerifier tidyVerifier = new TidyVerifierBuilder().build();
        tidyVerifier.verifyHtml(testHtml);
    }

    @Test
    public void verifyDefaultBuildWithWarning() throws Throwable {
        thrown.expect(AssertionError.class);
        thrown.expectMessage(startsWith("JTidy has reported messages with level [WARNING] or greater"));
        thrown.expectMessage(containsString("missing 'title' element"));

        String testHtml = "<!DOCTYPE html>" +
                "<head>" +
                "</head>" +
                "<body></body>" +
                "</html>";

        TidyVerifier tidyVerifier = new TidyVerifierBuilder().build();
        tidyVerifier.verifyHtml(testHtml);
    }

    @Test
    public void verifyBuildWithWarningBelowThreshold() throws Throwable {
        String testHtml = "<!DOCTYPE html>" +
                "<head>" +
                "</head>" +
                "<body></body>" +
                "</html>";

        TidyVerifier tidyVerifier = new TidyVerifierBuilder().setThreshold(TidyMessage.Level.ERROR).build();
        tidyVerifier.verifyHtml(testHtml);
    }


}
