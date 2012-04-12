package com.dynacrongroup.webtest.jtidy;

import org.apache.commons.configuration.PropertiesConfiguration;
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

    public void setProperties(PropertiesConfiguration properties) {
        setIgnoredCodes(getIgnoredCodesFromProps(properties));
        setIgnoredMessages(getIgnoredMessagesFromProps(properties));
        setDisplayErrorCodes(getErrorCodeDisplayFromProps(properties));
        setThreshold(getThresholdLevelFromProps(properties));
    }

    public void setIgnoredCodes(List<Integer> codes) {
        this.ignoredCodes = codes;
    }

    public void setIgnoredMessages(List<String> ignoredMessages) {
        this.ignoredMessages = ignoredMessages;
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

    private List<Integer> getIgnoredCodesFromProps(PropertiesConfiguration properties) {
        List<Integer> codes = new ArrayList<Integer>();

        if (properties.containsKey(IGNORED_CODES_PROP)) {
            String[] codeStrings = properties.getString(IGNORED_CODES_PROP).split(",");
            if (codeStrings != null) {
                for (String codeString : codeStrings) {
                    codes.add(Integer.valueOf(codeString.trim()));
                }
            }
        }

        return (codes.isEmpty()) ? ignoredCodes : codes;
    }

    private List<String> getIgnoredMessagesFromProps(PropertiesConfiguration properties) {
        List<String> messages = properties.getList(IGNORED_MESSAGES_PROP, new ArrayList<String>());
        return (messages.isEmpty()) ? ignoredMessages : messages;
    }

    private Boolean getErrorCodeDisplayFromProps(PropertiesConfiguration properties) {
        return properties.getBoolean(DISPLAY_CODES_PROP, displayErrorCodes);
    }

    private TidyMessage.Level getThresholdLevelFromProps(PropertiesConfiguration properties) {
        Integer codeProperty = properties.getInteger(THRESHOLD_LEVEL_PROP, null);
        return (codeProperty == null) ? threshold : TidyMessage.Level.fromCode(codeProperty);
    }
}
