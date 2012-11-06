package com.dynacrongroup.webtest.jtidy;

import com.typesafe.config.Config;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.tidy.TidyMessage;
import org.w3c.tidy.TidyMessageListener;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTMListener implements TidyMessageListener {

    public static final String IGNORED_CODES_PROP = "ignored-codes";
    public static final String IGNORED_MESSAGES_PROP = "ignored-messages";
    public static final String DISPLAY_CODES_PROP = "display-error-codes";
    public static final String THRESHOLD_LEVEL_PROP = "threshold-level";

    public static final TidyMessage.Level DEFAULT_THRESHOLD = TidyMessage.Level.WARNING;
    public static final Boolean DEFAULT_ERROR_CODE_DISPLAY = Boolean.TRUE;

    List<Integer> ignoredCodes = new ArrayList<Integer>();
    List<String> ignoredMessages = new ArrayList<String>();

    TidyMessage.Level threshold;
    Boolean displayErrorCodes = this.DEFAULT_ERROR_CODE_DISPLAY;

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTMListener.class);

    AbstractTMListener(TidyMessage.Level level) {
        threshold = level;
    }

    /**
     * Default constructor assumes that default is the threshold level.
     */
    AbstractTMListener() {
        threshold = DEFAULT_THRESHOLD;
    }

    @Override
    public void messageReceived(TidyMessage tidyMessage) {
        if (aboveThreshold(tidyMessage)
                && notIgnoredByCode(tidyMessage)
                && notIgnoredByMessage(tidyMessage)) {
            reportMessage(tidyMessage);
        }
    }

    private boolean notIgnoredByMessage(TidyMessage tidyMessage) {
        Boolean valid = true;
        for(String ignoredMessage : ignoredMessages) {
            if (tidyMessage.getMessage().contains(ignoredMessage)) {
                valid = false;
                break;
            }
        }
        return valid;
    }

    private boolean notIgnoredByCode(TidyMessage tidyMessage) {
        return !ignoredCodes.contains(Integer.valueOf(tidyMessage.getErrorCode()));
    }

    private boolean aboveThreshold(TidyMessage tidyMessage) {
        return tidyMessage.getLevel().compareTo(threshold) >= 0;
    }

    public void setConfig(Config config) {
        addIgnoredCodes(getIgnoredCodesFromConfig(config));
        addIgnoredMessages(getIgnoredMessagesFromConfig(config));
        setDisplayErrorCodes(getErrorCodeDisplayFromProps(config));
        setThreshold(getThresholdLevelFromProps(config));
    }

    public void addIgnoredCodes(List<Integer> codes) {
        LOG.trace("Adding ignored codes: {}", ArrayUtils.toString(codes));
        this.ignoredCodes.addAll(codes);
    }

    public void addIgnoredMessages(List<String> ignoredMessages) {
        LOG.trace("Adding ignored messages: {}", ArrayUtils.toString(ignoredMessages));
        this.ignoredMessages.addAll(ignoredMessages);
    }

    public void setDisplayErrorCodes(Boolean display) {
        this.displayErrorCodes = display;
    }

    public void setThreshold(TidyMessage.Level level) {
        this.threshold = level;
    }

    public abstract void verify() throws AssertionError;

    protected abstract void reportMessage(TidyMessage tidyMessage);

    protected String formatTidyMessage(TidyMessage tidyMessage) {
        return String.format("line %s column %s - %s: %s %s",
                tidyMessage.getLine(),
                tidyMessage.getColumn(),
                tidyMessage.getLevel(),
                tidyMessage.getMessage(),
                formatErrorCode(tidyMessage));
    }

    protected String formatErrorCode(TidyMessage message) {
        String formattedString = "";
        if (displayErrorCodes) {
            formattedString = String.format("[EC%s]", message.getErrorCode());
        }
        return formattedString;
    }

    private List<Integer> getIgnoredCodesFromConfig(Config config) {
        List<Integer> codes = new ArrayList<Integer>();
        List<String> codeStrings = config.hasPath(IGNORED_CODES_PROP) ? config.getStringList(IGNORED_CODES_PROP) : new ArrayList<String>();
        for (String code : codeStrings) {
            codes.add(Integer.valueOf(code));
        }
        return codes;
    }

    private List<String> getIgnoredMessagesFromConfig(Config config) {
        return config.hasPath(IGNORED_MESSAGES_PROP) ? config.getStringList(IGNORED_MESSAGES_PROP) : new ArrayList<String>();
    }

    private Boolean getErrorCodeDisplayFromProps(Config config) {
        return config.hasPath(DISPLAY_CODES_PROP) ? config.getBoolean(DISPLAY_CODES_PROP) : displayErrorCodes;
    }

    private TidyMessage.Level getThresholdLevelFromProps(Config config) {
        return config.hasPath(THRESHOLD_LEVEL_PROP) ? TidyMessage.Level.fromCode(config.getInt(THRESHOLD_LEVEL_PROP)) : threshold;
    }
}
