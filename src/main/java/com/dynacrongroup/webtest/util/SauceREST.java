package com.dynacrongroup.webtest.util;

/**
 * Created by IntelliJ IDEA.
 * User: yurodivuie
 * Date: 2/8/12
 * Time: 1:55 PM
 * To change this template use File | Settings | File Templates.
 */


import org.json.simple.JSONValue;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * Code from Sauce Labs, on github: https://github.com/saucelabs/saucerest-java
 */
public class SauceREST {
    protected String username;
    protected String accessKey;

    public static final String RESTURL = "http://saucelabs.com/rest";

    public SauceREST(String username, String accessKey) {
        this.username = username;
        this.accessKey = accessKey;
    }

    public void jobPassed(String jobId) throws IOException {
        Map<String, Object> updates = new HashMap<String, Object>();
        updates.put("passed", true);
        updateJobInfo(jobId, updates);
    }

    public void jobFailed(String jobId) throws IOException {
        Map<String, Object> updates = new HashMap<String, Object>();
        updates.put("passed", false);
        updateJobInfo(jobId, updates);
    }

    public void updateJobInfo(String jobId, Map<String,Object> updates) throws IOException {
        URL restEndpoint = new URL(RESTURL+"/v1/"+username+"/jobs/"+jobId);
        String auth = username + ":" + accessKey;
        auth = "Basic " + new String(Base64.encodeBase64(auth.getBytes()));

        HttpURLConnection postBack = (HttpURLConnection) restEndpoint.openConnection();
        postBack.setDoOutput(true);
        postBack.setRequestMethod("PUT");
        postBack.setRequestProperty("Authorization", auth);
        String jsonText = JSONValue.toJSONString(updates);
        postBack.getOutputStream().write(jsonText.getBytes());
        postBack.getInputStream().close();
    }
}