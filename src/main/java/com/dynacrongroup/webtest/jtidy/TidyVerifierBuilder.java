package com.dynacrongroup.webtest.jtidy;

import com.google.common.io.NullOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.tidy.Tidy;
import org.w3c.tidy.TidyMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Used to construct a TidyVerifier by building an instance of Tidy and an instance of a TidyMessageListener.
 * A property file can be used to configure most of the build process; if configuration is presented here instead,
 * it will override the property file.
 *
 * This class is currently experimental.
 */
public class TidyVerifierBuilder {

    /**
     * By default, placed /src/test/resources/jtidy.properties
     */
    public static final String DEFAULT_PROP_FILE = "/jtidy.properties";

    private static final Logger LOG = LoggerFactory.getLogger(TidyVerifierBuilder.class);

    private TidyMessage.Level threshold = null;
    private String propertyFileName = this.DEFAULT_PROP_FILE;
    private AbstractTMListener listener = new MapTMListener(new TreeMap<TidyMessage.Level, List<TidyMessage>>());
    private Tidy tidy = new Tidy();
    private Properties properties = null;
    private List<Integer> ignoredCodes = null;
    private Boolean displayErrorCodes = null;

    /**
     * Sets the minimum TidyMessage.Level that should generate an exception.
     * @param threshold
     * @return
     */
    public TidyVerifierBuilder setThreshold( TidyMessage.Level threshold) {
        this.threshold = threshold;
        return this;
    }

    /**
     * Sets the name of the property file (located in /src/test/resources/) that
     * should be used to configure JTidy.  Used to override the default value:
     * "jtidy.properties".
     * @param propertyFileName
     * @return
     */
    public TidyVerifierBuilder setPropertyFileName(String propertyFileName) {
        this.propertyFileName = propertyFileName;
        return this;
    }

    /**
     * Sets the MessageListener to be used by JTidy to track errors
     * @param messageListener
     * @return
     */
    public TidyVerifierBuilder setMessageListener(AbstractTMListener messageListener) {
        this.listener = messageListener;
        return this;
    }

    /**
     * Sets the list of codes that are to be ignored.
     * @param codes
     * @return
     */
    public TidyVerifierBuilder ignoreCodes(List<Integer> codes) {
        this.ignoredCodes = codes;
        return this;
    }

    /**
     * Sets whether codes should be displayed by the listener.
     * @param display False if codes should not be displayed in output
     * @return
     */
    public TidyVerifierBuilder displayCodes(Boolean display) {
        this.displayErrorCodes = display;
        return this;
    }

    /**
     * Builds the TidyVerifier, which wraps calls to verify that a given piece of html
     * is valid through JTidy.
     * @return
     */
    public TidyVerifier build() {
        //Construct properties from file, if any, to be used in constructing the TidyVerifier
        this.properties = getProperties();
        return new TidyVerifier(this);
    }

    /**
     * Called by TidyVerifier during construction; configures and returns the MessageListener.  This
     * has the final configuration of the listener; properties are set and then overridden if
     * specified during the build process.
     * @return
     */
    protected AbstractTMListener getListener() {
        if (properties != null) {
            listener.setProperties(properties);
        }
        if (threshold != null) {
            this.listener.setThreshold(threshold);
        }
        if (ignoredCodes != null) {
            this.listener.setIgnoredCodes(ignoredCodes);
        }
        if (displayErrorCodes != null) {
            this.listener.setDisplayErrorCodes(displayErrorCodes);
        }
        return this.listener;
    }

    /**
     * Called by TidyVerifier during construction; configures and returns a Tidy instance
     * @return
     */
    protected Tidy getTidy() {
        if (properties != null) {
            this.tidy.setConfigurationFromProps(properties);
        }
        this.tidy.setErrout(new PrintWriter(new NullOutputStream()));
        this.tidy.setMessageListener(listener);
        return this.tidy;
    }

    private Properties getProperties() {
        Properties newProperties = new Properties();
        try {
            InputStream stream = this.getClass().getResourceAsStream(propertyFileName);
            if (stream != null) {
                newProperties.load(stream);
            }
            else {
                LOG.info("Property file [{}] not found.  Using default settings.", propertyFileName);
            }
        } catch (IOException e) {
            LOG.warn("Unable to load property file [{}], using default settings.", propertyFileName);
        }
        return newProperties;
    }
}
