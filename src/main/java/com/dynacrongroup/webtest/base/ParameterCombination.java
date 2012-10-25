package com.dynacrongroup.webtest.base;

import com.dynacrongroup.webtest.browser.WebDriverConfig;
import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * User: yurodivuie
 * Date: 10/22/12
 * Time: 12:34 PM
 */
public class ParameterCombination {

    private static final Logger LOG = LoggerFactory.getLogger(ParameterCombination.class);
    private static final String DEFAULT_LANGUAGE = "en";

    @NotNull
    private WebDriverConfig webDriverConfig = new WebDriverConfig();

    @NotNull
    private String language = DEFAULT_LANGUAGE;

    public Map<String, Object> globalCustomCapabilities = new HashMap<String, Object>();


    public WebDriverConfig getWebDriverConfig() {
        return webDriverConfig;
    }

    public void setWebDriverConfig(WebDriverConfig webDriverConfig) {
        this.webDriverConfig = webDriverConfig;
        webDriverConfig.customCapabilities.putAll(getGlobalCustomCapabilities());
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return Joiner.on("|").join(webDriverConfig.toString(), language.toString());
    }

    public Map<String, Object> getGlobalCustomCapabilities() {
        return globalCustomCapabilities;
    }

    public void setGlobalCustomCapabilities(Map<String, Object> globalCustomCapabilities) {
        this.globalCustomCapabilities = globalCustomCapabilities;
        webDriverConfig.customCapabilities.putAll(globalCustomCapabilities);        //TODO: merge more sensibly.
    }
}
