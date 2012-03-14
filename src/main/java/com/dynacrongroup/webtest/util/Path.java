package com.dynacrongroup.webtest.util;

import com.dynacrongroup.webtest.SystemName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;

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

    private static final Logger LOG = LoggerFactory.getLogger(Path.class);

    private String protocol = "http";
    private String server = null;
    private String context = "";
    private int port = 8080;

    /**
     * Create default path object.
     */
    public Path() {
        init();
    }

    /**
     * Create default path object with server overridden.
     * @param server
     */
    public Path(String server) {
        init();
        this.server = server;
    }

    /**
     * Create default path object with port overridden.
     * @param port
     */
    public Path(int port) {
        init();
        this.port = port;
    }

    /**
     * Create default path object with server and port overridden.
     * @param server
     * @param port
     */
    public Path(String server, int port) {
        init();
        this.server = server;
        this.port = port;
    }

    /**
     * Create path object overriding all defaults.
     * @param protocol
     * @param server
     * @param port
     */
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

    public void setContext(String context) {
        this.context = context;
    }

    /**
     * Alias for translate()
     */
    public String _(String path) {
        return translate(path);
    }

    /**
     * Translates path into a URL string.
     * @param path  Path to append to the context.
     * @return
     */
    public String translate(String path) {
        String fullUrl = null;

        try {
            fullUrl = new URL(protocol, server, port, context + path)
                    .toExternalForm();
        } catch (MalformedURLException e) {
            LOG.error(e.getMessage());
        }

        return fullUrl;
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

        protocol = ConfigurationValue.getConfigurationValue(
                WEBDRIVER_PROTOCOL, "http");

        if (protocol.equalsIgnoreCase("https")) {
            port = 443;
        }

        port = Integer.parseInt(ConfigurationValue.getConfigurationValue(
                WEBDRIVER_PORT, String.valueOf(port)));

        server = ConfigurationValue.getConfigurationValue(WEBDRIVER_SERVER,
                SystemName.getSystemName());

        context = ConfigurationValue.getConfigurationValue(
                WEBDRIVER_CONTEXT, "");
    }

    /**
     * @return the path object converted to string, with no page appended to the url.
     */
    @Override
    public String toString() {
        return translate("");
    }
}