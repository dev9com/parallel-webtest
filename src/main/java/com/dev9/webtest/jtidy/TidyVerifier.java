package com.dev9.webtest.jtidy;

import com.google.common.io.NullOutputStream;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.tidy.Tidy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * TidyVerifier is used to verify that a string or stream of html is valid.  It is built using the
 * TidyVerifierBuilder, and can be configured from a property file (/jtidy.properties by default).
 *
 * Usage:
 *
 * ...
 * String html = <em>some html</em>;
 * TidyVerifier tidyVerifier = new TidyVerifierBuilder().build();
 * tidyVerifier.verifyHtml(html);
 * ...
 *
 * or
 *
 * ...
 * String html = <em>some html</em>;
 * TidyVerifier tidyVerifier = new TidyVerifierBuilder().displayCodes(false).setThreshold(TidyMessage.Level.ERROR).build();
 * tidyVerifier.verifyHtml(html);
 * html = <em>some new html</em>;
 * tidyVerifier.verifyHtml(html);
 */
public class TidyVerifier {

    private static final Logger LOG = LoggerFactory.getLogger(TidyVerifier.class);

    private Tidy tidy;

    // Listener is stored separately so that results can be verified.
    private AbstractTMListener listener;

    private TidyVerifier() {
        throw new IllegalAccessError("TidyVerifier should be build using TidyVerifierBuilder");
    }

    /**
     * TidyVerifier should be constructed using TidyVerifierBuilder
     * @param builder
     */
    protected TidyVerifier(TidyVerifierBuilder builder) {
        LOG.trace("Creating TidyVerifier from builder.");
        this.listener = builder.getListener();
        this.tidy = builder.getTidy();
    }

    public void verifyHtml(WebDriver driver) {
        verifyHtml(driver.getPageSource());
    }

    public void verifyHtml(String html) {
        verifyHtml(new ByteArrayInputStream(html.getBytes()));
    }

    public void verifyHtml(InputStream stream) {
        LOG.trace("Verifying html with JTidy");
        tidy.parse(stream, new NullOutputStream());
        listener.verify();
    }
}
