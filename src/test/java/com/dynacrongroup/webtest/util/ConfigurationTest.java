package com.dynacrongroup.webtest.util;

import com.typesafe.config.Config;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.*;

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

}
