package com.dynacrongroup.webtest.util;

import com.dynacrongroup.webtest.WebDriverBase;
import com.typesafe.config.Config;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * User: yurodivuie
 * Date: 10/16/12
 * Time: 12:50 PM
 */
public class ConfigurationTest {

    @Test
    public void testReferenceOverride() {
        Config config = Configuration.getConfig();
        assertThat(config.getString("local.browser")).isEqualToIgnoringCase("chrome");
    }

    @Test
    public void testReferenceOnly() {
        Config config = Configuration.getConfig();
        assertThat(config.getString("saucelabs.server")).isEqualToIgnoringCase("ondemand.saucelabs.com/wd/hub");
    }

    @Test
    public void testClassConf() {
        Config config = Configuration.getConfigForClass(ConfigurationTest.class);
        assertThat(config.getBoolean("class")).isTrue();
    }

    @Test
    public void testMissingClassConf() {
        Config config = Configuration.getConfigForClass(WebDriverBase.class);
        assertThat(config.getString("local.browser")).isEqualToIgnoringCase("chrome");
    }

}
