package com.dynacrongroup.webtest.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Validate that a particular URL is accessible */
public class ConnectionValidator {

    private static final Logger log = LoggerFactory.getLogger(ConnectionValidator.class);

    /** Makes checkstyle happy */
    private ConnectionValidator() {
    }

    public static boolean verifyConnection(String path) {
        return verifyConnection(path, false);
    }

    /** Simple utility method, attempts to connect to the specified path and read a line of text.
     */
    public static boolean verifyConnection(String path, boolean silent) {
        URL url = null;

        try {
            url = new URL(path);
            BufferedReader contents;

            InputStreamReader isr = new InputStreamReader(url.openStream());

            contents = new BufferedReader(isr);

            String readLine = contents.readLine();
            return !readLine.isEmpty();

        } catch (Exception ex) {
            if (!silent) {
                log.error("Unable to connect to " + path, ex);
            }
        }
        return false;
    }
}
