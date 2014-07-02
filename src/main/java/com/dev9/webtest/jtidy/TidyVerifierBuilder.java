package com.dev9.webtest.jtidy;

import com.dev9.webtest.util.Configuration;
import com.google.common.io.NullOutputStream;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.tidy.Tidy;
import org.w3c.tidy.TidyMessage;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.*;

/**
 * Used to construct a TidyVerifier by building an instance of Tidy and an instance of a TidyMessageListener.
 * A property file can be used to configure most of the build process; if configuration is presented here instead,
 * it will override the property file.
 * <p/>
 * This class is currently experimental.
 */
public class TidyVerifierBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(TidyVerifierBuilder.class);

    private TidyMessage.Level threshold = null;
    private AbstractTMListener listener = new MapTMListener(new TreeMap<TidyMessage.Level, List<TidyMessage>>());
    private Tidy tidy = new Tidy();
    private Config config = null;
    private List<Integer> ignoredCodes = null;
    private List<String> ignoredMessages = null;
    private Boolean displayErrorCodes = null;

    public TidyVerifierBuilder() {
        config = Configuration.getConfig().getConfig("jtidy");
    }

    public TidyVerifierBuilder(Class testClass) {
        config = Configuration.getConfigForClass(testClass).getConfig("jtidy");
    }

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
        if (config != null) {
            listener.setConfig(config);
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
        if (config != null) {
            this.tidy.setConfigurationFromProps(getJTidyProperties());
        }
        this.tidy.setErrout(new PrintWriter(new NullOutputStream()));
        this.tidy.setMessageListener(listener);
        return this.tidy;
    }

    private Properties getJTidyProperties() {
        Properties jtidyProperties = new Properties();
        for (Map.Entry<String, ConfigValue> entry : config.entrySet()) {
            if (notACustomKey(entry.getKey()) &&  entry.getValue().valueType().equals(ConfigValueType.STRING)) {
                jtidyProperties.put(entry.getKey(), config.getString(entry.getKey()));
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
