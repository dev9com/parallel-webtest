package com.dynacrongroup.webtest.util;

import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * User: yurodivuie
 * Date: 2/29/12
 * Time: 11:06 AM
 */
public class SauceRESTRequestBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(SauceRESTRequest.class);

    private String suffix = "";
    private String method = "GET";  //default value;
    private String version = SauceRESTRequest.DEFAULT_VERSION;
    private Map<String, Object> jsonMap = new HashMap<String, Object>();

    public SauceRESTRequestBuilder addJSON(String key, Object value) {
        jsonMap.put(key, value);
        return this;
    }

    public SauceRESTRequestBuilder setHTTPMethod(String method) {
        this.method = method;
        return this;
    }


    public SauceRESTRequestBuilder addUsersToPath() {
        this.suffix += "/users";
        return this;
    }

    public SauceRESTRequestBuilder addUserIdToPath(String user) {
        this.suffix += "/";
        this.suffix += user;
        return this;
    }

    public SauceRESTRequestBuilder addJobsToPath() {
        this.suffix += "/jobs";
        return this;
    }

    public SauceRESTRequestBuilder addJobIdToPath(String jobId) {
        this.suffix += "/";
        this.suffix += jobId;
        return this;
    }

    public SauceRESTRequestBuilder addGenericSuffix(String suffix) {
        this.suffix += suffix;
        return this;
    }

    public SauceRESTRequestBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    public SauceRESTRequest build() {

        SauceRESTRequest request = null;
        String json = null;

        if (!jsonMap.isEmpty()) {
            json = JSONValue.toJSONString(jsonMap);
        }

        String stringUrl = String.format("%s/%s", SauceRESTRequest.REST_URL, version);

        if (suffix != null) {
            stringUrl += suffix;
        }

        LOG.trace("Constructed url is {}", stringUrl);
        URL url = null;

        try {
            url = new URL(stringUrl);
            request = new SauceRESTRequest(url, method, json);
        } catch (MalformedURLException e) {
            LOG.error("Unable to create sauce rest url {}", e);
        }

        return request;
    }

}
