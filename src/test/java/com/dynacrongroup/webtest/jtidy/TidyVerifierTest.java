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

/*    Tried using this to redirect output; didn't work.
        {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
    }
    */

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

    @Test
    public void verifyIgnoredMessageIsIgnored() throws Throwable {
        String testHtml = "<!DOCTYPE html>" +
                "<head>" +
                "<title>title</title>" +
                "</head>" +
                "<body>" +
                "<a href=/somewhere tabindex=-1>link</a>" +
                "</body>" +
                "</html>";

        TidyVerifier tidyVerifier = new TidyVerifierBuilder().setThreshold(TidyMessage.Level.WARNING).build();
        tidyVerifier.verifyHtml(testHtml);
    }

    @Test
    public void verifyNotIgnoredMessageIsNotIgnored() throws Throwable {
        String testHtml = "<!DOCTYPE html>" +
                "<head>" +
                "<title>title</title>" +
                "</head>" +
                "<body>" +
                "<a href=/somewhere tabindex=-2>link</a>" +
                "</body>" +
                "</html>";

        thrown.expect(AssertionError.class);
        thrown.expectMessage(startsWith("JTidy has reported messages with level [WARNING] or greater"));
        thrown.expectMessage(containsString("attribute \"tabindex\" has invalid value \"-2\""));

        TidyVerifier tidyVerifier = new TidyVerifierBuilder().setThreshold(TidyMessage.Level.WARNING).build();
        tidyVerifier.verifyHtml(testHtml);
    }


}
