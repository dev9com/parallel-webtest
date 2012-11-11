package com.dynacrongroup.webtest.browser;

import org.apache.commons.lang.StringUtils;

import java.util.Locale;

/**
 * User: yurodivuie
 * Date: 11/4/12
 * Time: 11:51 AM
 */
public class BrowserLocale {

    private static final String defaultLanguage = "en";
    private static final String defaultCountry = "us";

    private String language = defaultLanguage;
    private String country = defaultCountry;

    public BrowserLocale() {
    }

    public BrowserLocale(String rawBrowserLocale) {
        String[] localeParts = rawBrowserLocale.split("-");
        if (localeParts.length != 2) {
            language = localeParts[0];
            country = "";
        }
        else {
            language = localeParts[0];
            country = localeParts[1];
        }
    }

    public BrowserLocale(Locale locale) {
        language = locale.getLanguage();
        country = locale.getCountry();
    }

    public String getLanguage() {
        return language;
    }

    public String getCountry() {
        return country;
    }

    public Locale getLocale() {
        return new Locale(language, country);
    }

    @Override
    public String toString() {
        if (StringUtils.isEmpty(country)) {
            return language;
        }
        else {
            return String.format("%s-%s", language, country);
        }
    }
}
