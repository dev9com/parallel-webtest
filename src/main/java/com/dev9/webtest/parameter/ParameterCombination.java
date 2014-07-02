package com.dev9.webtest.parameter;

import com.dev9.webtest.browser.BrowserLocale;
import com.dev9.webtest.browser.WebDriverConfig;
import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.ConfigurationException;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

public class ParameterCombination {

    private static final Logger LOG = LoggerFactory.getLogger(ParameterCombination.class);

    @NotNull
    private WebDriverConfig webDriverConfig = new WebDriverConfig();

    @NotNull
    private BrowserLocale browserLocale = new BrowserLocale();

    public Map<String, Object> globalCustomCapabilities = new HashMap<String, Object>();


    public WebDriverConfig getWebDriverConfig() {
        return webDriverConfig;
    }

    public void setWebDriverConfig(WebDriverConfig webDriverConfig) {
        this.webDriverConfig = webDriverConfig;
        webDriverConfig.customCapabilities.putAll(getGlobalCustomCapabilities());
        webDriverConfig.setBrowserLocale(browserLocale);
        webDriverConfig.setEnableNativeEventsForFirefox();
//        try {
//            webDriverConfig.configureProxySettings(webDriverConfig);
//        } catch (ConfigurationException e) {
//            LOG.error("Problem configuring proxy: {}", Throwables.getStackTraceAsString(e));
//        }
    }

    public BrowserLocale getBrowserLocale() {
        return browserLocale;
    }

    public void setBrowserLocale(BrowserLocale browserLocale) {
        this.browserLocale = browserLocale;
        webDriverConfig.setBrowserLocale(browserLocale);
    }

    @Override
    public String toString() {
        return Joiner.on("|").join(webDriverConfig.toString(), browserLocale.toString());
    }

    public Map<String, Object> getGlobalCustomCapabilities() {
        return globalCustomCapabilities;
    }

    public void setGlobalCustomCapabilities(Map<String, Object> globalCustomCapabilities) {
        this.globalCustomCapabilities.putAll(globalCustomCapabilities);
        webDriverConfig.customCapabilities.putAll(globalCustomCapabilities);        //TODO: merge more sensibly.
    }
}
