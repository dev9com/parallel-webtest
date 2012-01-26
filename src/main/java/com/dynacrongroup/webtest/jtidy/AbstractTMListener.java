package com.dynacrongroup.webtest.jtidy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.tidy.TidyMessage;
import org.w3c.tidy.TidyMessageListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public abstract class AbstractTMListener implements TidyMessageListener {
    
    public static final String IGNORED_CODES_PROP = "ignored-codes";
    public static final String DISPLAY_CODES_PROP = "display-error-codes";
    public static final String THRESHOLD_LEVEL_PROP = "threshold-level";

    public static final TidyMessage.Level DEFAULT_THRESHOLD = TidyMessage.Level.WARNING;
    public static final Boolean DEFAULT_ERROR_CODE_DISPLAY = Boolean.TRUE;
    
    private static final Logger LOG = LoggerFactory.getLogger(AbstractTMListener.class);

    protected List<Integer> ignoredCodes = new ArrayList<Integer>();
    protected TidyMessage.Level threshold;
    protected Boolean displayErrorCodes = this.DEFAULT_ERROR_CODE_DISPLAY;

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
        if (tidyMessage.getLevel().compareTo(threshold) >= 0
                && !ignoredCodes.contains(Integer.valueOf(tidyMessage.getErrorCode()))) {
            reportMessage(tidyMessage);
        }
    }
    
    public void setProperties(Properties properties) {
        setIgnoredCodes(getIgnoredCodesFromProps(properties));
        setDisplayErrorCodes(getErrorCodeDisplayFromProps(properties));
        setThreshold(getThresholdLevelFromProps(properties));
    }
    
    public void setIgnoredCodes(List<Integer> codes) {
        this.ignoredCodes = codes;
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

    private List<Integer> getIgnoredCodesFromProps(Properties properties) {
        String[] codeStrs = properties.getProperty(IGNORED_CODES_PROP).split(",");
        List<Integer> codes = new ArrayList<Integer>();
        if (codeStrs != null) {
            for (String code : codeStrs) {
                codes.add(Integer.valueOf(code.trim()));
            }
        }
        return (codes.isEmpty()) ? ignoredCodes : codes;
    }

    private Boolean getErrorCodeDisplayFromProps(Properties properties) {
        
        String property = properties.getProperty(this.DISPLAY_CODES_PROP);
        return (property == null) ? displayErrorCodes : Boolean.valueOf(property);
    }

    private TidyMessage.Level getThresholdLevelFromProps(Properties properties) {
        String property = properties.getProperty(this.THRESHOLD_LEVEL_PROP);
        return (property == null) ? threshold : TidyMessage.Level.fromCode(Integer.valueOf(property));
    }
}
