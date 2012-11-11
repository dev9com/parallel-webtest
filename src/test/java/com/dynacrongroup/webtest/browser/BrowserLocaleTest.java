package com.dynacrongroup.webtest.browser;

import org.junit.Test;

import java.util.Locale;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * User: yurodivuie
 * Date: 11/11/12
 * Time: 12:03 PM
 */
public class BrowserLocaleTest {

    @Test
    public void testGettingLocale() {
        BrowserLocale browserLocale = new BrowserLocale("en-US");
        assertThat(browserLocale.getLocale()).isEqualTo(Locale.US);
    }

    @Test
    public void gettingLocaleWithoutCountryTest() {
        BrowserLocale browserLocale = new BrowserLocale("en");
        assertThat(browserLocale.getLocale()).isEqualTo(Locale.ENGLISH);
    }

    @Test
    public void buildingFromLocaleWithoutCountry() {
        BrowserLocale browserLocale = new BrowserLocale(Locale.ENGLISH);
        assertThat(browserLocale.toString()).isEqualToIgnoringCase(Locale.ENGLISH.getLanguage());
    }

    @Test
    public void buildingFromLocaleWithCountry() {
        BrowserLocale browserLocale = new BrowserLocale(Locale.CANADA_FRENCH);
        assertThat(browserLocale.toString()).containsIgnoringCase(Locale.CANADA_FRENCH.getLanguage());
        assertThat(browserLocale.toString()).containsIgnoringCase(Locale.CANADA_FRENCH.getCountry());
    }
}
