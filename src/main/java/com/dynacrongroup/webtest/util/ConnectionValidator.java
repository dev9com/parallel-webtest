package com.dynacrongroup.webtest.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/** Validate that a particular URL is accessible */
public final class ConnectionValidator {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionValidator.class);

    /** Makes checkstyle happy */
    private ConnectionValidator() {
        throw new UnsupportedOperationException("Class is utility class; should not be instantiated.");
    }

    /**
     * Silently verifies that the connection path is valid.
     *
     * @param path
     * @return
     */
    public static boolean verifyConnection(String path) {
        return verifyConnection(path, false);
    }

    /**
     * Simple utility method, attempts to connect to the specified path and read a line of text.
     */
    public static boolean verifyConnection(String path, boolean silent) {
        Boolean success = false;

        try {
            URL url = new URL(path);
            BufferedReader contents;

            InputStreamReader isr = new InputStreamReader(url.openStream());

            contents = new BufferedReader(isr);

            String readLine = contents.readLine();
            success =  !readLine.isEmpty();

        } catch (Exception ex) {
            if (!silent) {
                LOG.error("Unable to connect to " + path, ex);
            }
        }
        return success;
    }
}
