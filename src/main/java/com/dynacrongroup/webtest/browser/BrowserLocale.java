package com.dynacrongroup.webtest.browser;

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

    public BrowserLocale() {}

    public BrowserLocale(String rawBrowserLocale) {

        String[] localeParts = rawBrowserLocale.split("-");
        if (localeParts.length != 2) {
            throw new ExceptionInInitializerError(String.format("Locale \"%s\" is not in lang-country format (\"en-us\", for example)."));
        }

        language = localeParts[0];
        country = localeParts[1];
    }

    public String getLanguage() {
        return language;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public String toString() {
        return String.format("%s-%s", language, country);
    }
}
