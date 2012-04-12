package com.dynacrongroup.webtest.jtidy;

import com.google.common.io.NullOutputStream;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.tidy.Tidy;
import org.w3c.tidy.TidyMessage;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Used to construct a TidyVerifier by building an instance of Tidy and an instance of a TidyMessageListener.
 * A property file can be used to configure most of the build process; if configuration is presented here instead,
 * it will override the property file.
 * <p/>
 * This class is currently experimental.
 */
public class TidyVerifierBuilder {

    /**
     * By default, placed /src/test/resources/jtidy.properties
     */
    public static final String DEFAULT_PROP_FILE = "/jtidy.properties";

    private static final Logger LOG = LoggerFactory.getLogger(TidyVerifierBuilder.class);

    private TidyMessage.Level threshold = null;
    private String propertyFileName = DEFAULT_PROP_FILE;
    private AbstractTMListener listener = new MapTMListener(new TreeMap<TidyMessage.Level, List<TidyMessage>>());
    private Tidy tidy = new Tidy();
    private PropertiesConfiguration properties = null;
    private List<Integer> ignoredCodes = null;
    private List<String> ignoredMessages = null;
    private Boolean displayErrorCodes = null;

    /**
     * Sets the minimum TidyMessage.Level that should generate an exception.
     *
     * @param threshold
     * @return
     */
    public TidyVerifierBuilder setThreshold(TidyMessage.Level threshold) {
        this.threshold = threshold;
        return this;
    }

    /**
     * Sets the name of the property file (located in /src/test/resources/) that
     * should be used to configure JTidy.  Used to override the default value:
     * "jtidy.properties".
     *
     * @param propertyFileName
     * @return
     */
    public TidyVerifierBuilder setPropertyFileName(String propertyFileName) {
        this.propertyFileName = propertyFileName;
        return this;
    }

    /**
     * Sets the MessageListener to be used by JTidy to track errors
     *
     * @param messageListener
     * @return
     */
    public TidyVerifierBuilder setMessageListener(AbstractTMListener messageListener) {
        this.listener = messageListener;
        return this;
    }

    /**
     * Sets the list of codes that are to be ignored.
     *
     * @param codes
     * @return
     */
    public TidyVerifierBuilder ignoreCodes(List<Integer> codes) {
        this.ignoredCodes = codes;
        return this;
    }

    /**
     * Sets the list of messages that are to be ignored.
     *
     * @param messages
     * @return
     */
    public TidyVerifierBuilder ignoredMessages(List<String> messages) {
        this.ignoredMessages = messages;
        return this;
    }

    /**
     * Sets whether codes should be displayed by the listener.
     *
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
     *
     * @return
     */
    public TidyVerifier build() {
        //Construct properties from file, if any, to be used in constructing the TidyVerifier
        getPropertiesConfiguration();
        return new TidyVerifier(this);
    }

    /**
     * Called by TidyVerifier during construction; configures and returns the MessageListener.  This
     * has the final configuration of the listener; properties are set and then overridden if
     * specified during the build process.
     *
     * @return
     */
    protected AbstractTMListener getListener() {
        if (properties != null) {
            listener.setProperties(properties);
        }
        if (threshold != null) {
            listener.setThreshold(threshold);
        }
        if (ignoredCodes != null) {
            listener.addIgnoredCodes(ignoredCodes);
        }
        if (ignoredMessages != null) {
            listener.addIgnoredMessages(ignoredMessages);
        }
        if (displayErrorCodes != null) {
            listener.setDisplayErrorCodes(displayErrorCodes);
        }
        return this.listener;
    }

    /**
     * Called by TidyVerifier during construction; configures and returns a Tidy instance
     *
     * @return
     */
    protected Tidy getTidy() {
        if (properties != null) {
            this.tidy.setConfigurationFromProps(getJTidyProperties());
        }
        this.tidy.setErrout(new PrintWriter(new NullOutputStream()));
        this.tidy.setMessageListener(listener);
        return this.tidy;
    }

    private void getPropertiesConfiguration() {
        PropertiesConfiguration newProperties = new PropertiesConfiguration();
        newProperties.setListDelimiter('\t');
        InputStream stream = null;
        try {
            stream = this.getClass().getResourceAsStream(propertyFileName);
            if (stream != null) {
                newProperties.load(stream);
            } else {
                LOG.info("Property file [{}] not found.  Using default settings.", propertyFileName);
            }
        } catch (ConfigurationException e) {
            LOG.warn("Unable to load property file [{}], using default settings.", propertyFileName);
        } finally {
            IOUtils.closeQuietly(stream);
        }
        properties = newProperties;
    }

    private Properties getJTidyProperties() {
        Properties jtidyProperties = new Properties();
        for ( Iterator iterator = properties.getKeys(); iterator.hasNext();) {
            String key = (String) iterator.next();
            if (notACustomKey(key)) {
                jtidyProperties.put(key, properties.getString(key));
            }
        }
        return jtidyProperties;
    }

    private boolean notACustomKey(String key) {
        return !(AbstractTMListener.DISPLAY_CODES_PROP.equals(key) ||
                AbstractTMListener.IGNORED_CODES_PROP.equals(key) ||
                AbstractTMListener.IGNORED_MESSAGES_PROP.equals(key) ||
                AbstractTMListener.THRESHOLD_LEVEL_PROP.equals(key));
    }
}
