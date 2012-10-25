package com.dynacrongroup.webtest.base;

import com.dynacrongroup.webtest.browser.WebDriverConfig;
import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;

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


    public WebDriverConfig getWebDriverConfig() {
        return webDriverConfig;
    }

    public void setWebDriverConfig(WebDriverConfig webDriverConfig) {
        this.webDriverConfig = webDriverConfig;
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


}
