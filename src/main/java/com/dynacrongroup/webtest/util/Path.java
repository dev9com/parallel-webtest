package com.dynacrongroup.webtest.util;

import com.dynacrongroup.webtest.SystemName;
import com.dynacrongroup.webtest.WebDriverFactory;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * This utility class provides support for isolating your web tests from a
 * particular server instance. By using this class in combination with Maven
 * profiles, you can support a wide variety of scenarios (for example, writing
 * tests that access your local system, then flip a switch and target a shared
 * development environment).
 */
public class Path {
    /**
     * These system properties can be used to override defaults
     */
    public static final String WEBDRIVER_PROTOCOL = "WEBDRIVER_PROTOCOL";
    public static final String WEBDRIVER_SERVER = "WEBDRIVER_SERVER";
    public static final String WEBDRIVER_PORT = "WEBDRIVER_PORT";
    public static final String WEBDRIVER_CONTEXT = "WEBDRIVER_CONTEXT";

    private final static Logger LOG = LoggerFactory.getLogger(Path.class);

    private static Set<String> sauceConnectWarnings = new HashSet<String>();
    /**
     * Used to track the various servers that the test suite[s] expect to talk
     * to. This is used to make sure that the same warning is not sent over and
     * over.
     */

    private String protocol = "http";
    private String server = null;
    private String context = "";
    private int port = 8080;

    public Path() {
        init();
    }

    public Path(String server) {
        init();
        this.server = server;
    }

    public Path(int port) {
        init();
        this.port = port;
    }

    public Path(String server, int port) {
        init();
        this.server = server;
        this.port = port;
    }

    public Path(String protocol, String server, int port) {
        init();
        this.protocol = protocol;
        this.server = server;
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getContext() {
        return context;
    }

    /**
     * Alias for translate()
     */
    public String _(String path) {
        return translate(path);
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String translate(String path) {

        try {
            return new URL(protocol, server, port, context + path)
                    .toExternalForm();
        } catch (MalformedURLException e) {
            LOG.error(e.getMessage());
            return null;
        }
    }

    /**
     * Used to determine whether the current path is local.  Can be useful for adding branching
     * logic to code based on the currently configured server.
     *
     * @return True if the host matches the system name or localhost.
     */
    public Boolean isLocal() {
        Boolean local = null;

        if (server != null) {
            try {
                InetAddress address = InetAddress.getByName(server);
                LOG.trace("Address is {}", address.getAddress());
                local = Arrays.equals(address.getAddress(), new byte[]{127, 0, 0, 1});
            } catch (UnknownHostException e) {
                LOG.error(e.getMessage());
            }
        }

        return local;
    }


    private void init() {
        if (server == null) {

            protocol = ConfigurationValue.getConfigurationValue(
                    WEBDRIVER_PROTOCOL, "http");

            String driver = ConfigurationValue.getConfigurationValue(
                    WebDriverFactory.WEBDRIVER_DRIVER, null);
            if (driver != null
                    && HtmlUnitDriver.class.toString().contains(driver)) {
                port = 8080;
            }

            if (protocol.equalsIgnoreCase("https")) {
                port = 443;
            }
            server = SystemName.getSystemName();

            server = ConfigurationValue.getConfigurationValue(WEBDRIVER_SERVER,
                    server);

            port = Integer.parseInt(ConfigurationValue.getConfigurationValue(
                    WEBDRIVER_PORT, port + ""));

            context = ConfigurationValue.getConfigurationValue(
                    WEBDRIVER_CONTEXT, "");
        }
    }

    /**
     * @return the path object converted to string, with no page appended to the url.
     */
    @Override
    public String toString() {
        return translate("");
    }
}