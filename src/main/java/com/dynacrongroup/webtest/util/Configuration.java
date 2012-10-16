package com.dynacrongroup.webtest.util;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * User: yurodivuie
 * Date: 10/16/12
 * Time: 12:40 PM
 */
public class Configuration {

    private static Config conf = null;

    public static Config getConfig() {
        if (conf == null) {
            createConf();
        }
        return conf;
    }

    private static void createConf() {
        conf = ConfigFactory.load();
    }

}
