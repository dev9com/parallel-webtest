package com.dynacrongroup.webtest.util;

import com.gargoylesoftware.htmlunit.HttpMethod;
import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * Based on Sauce Labs code, on github: https://github.com/saucelabs/saucerest-java.
 * <p/>
 * Used to set the pass/fail status on tests.
 */
public class SauceREST {
    protected String username;
    protected String accessKey;

    public static final String RESTURL = "http://saucelabs.com/rest";

    private static final Logger LOG = LoggerFactory.getLogger(SauceREST.class);

    public SauceREST(String username, String accessKey) {
        this.username = username;
        this.accessKey = accessKey;
    }

    public JSONObject jobPassed(String jobId) {
        Map<String, Object> updates = new HashMap<String, Object>();
        updates.put("passed", true);
        return updateJobInfo(jobId, updates);
    }

    public JSONObject jobFailed(String jobId) {
        Map<String, Object> updates = new HashMap<String, Object>();
        updates.put("passed", false);
        return updateJobInfo(jobId, updates);
    }

    public JSONObject updateJobInfo(String jobId, Map<String, Object> updates) {
        String jsonText = JSONValue.toJSONString(updates);
        return jobRestRequest(jobId, HttpMethod.PUT.name() , null, jsonText);
    }



    public JSONObject requestStatus(String jobId) {
        return jobRestRequest(jobId, HttpMethod.GET.name() , null, null);
    }

    /**
     * Stop a currently running job.  Should not be necessary to use in WebDriverBase
     * @param jobId
     * @return
     */
    public JSONObject stopJob(String jobId) {
        return jobRestRequest(jobId, HttpMethod.PUT.name(), "/stop", null);
    }

    /**
     * General function for connection to sauce labs rest interface for a given job.
     *
     * @param jobId     id for job in Sauce
     * @param method    Http method ("GET", "PUT")
     * @param suffix    optional suffix for request after id (like "/stop")
     * @param json      optional json parameters
     * @return
     */
    private JSONObject jobRestRequest(String jobId, String method, String suffix, String json ) {

        JSONObject result = null;

        if (suffix == null) {
            suffix = "";
        }

        try {
            URL restEndpoint = new URL(RESTURL + "/v1/" + username + "/jobs/" + jobId + suffix);
            String auth = username + ":" + accessKey;
            auth = "Basic " + new String(Base64.encodeBase64(auth.getBytes()));

            HttpURLConnection postBack = (HttpURLConnection) restEndpoint.openConnection();
            postBack.setDoOutput(true);
            postBack.setRequestMethod(method);
            postBack.setRequestProperty("Authorization", auth);

            if (json != null) {
                OutputStream stream = postBack.getOutputStream();
                stream.write(json.getBytes());
                stream.close();
            }
            else {
                postBack.connect();
            }

            result = (JSONObject)JSONValue.parse(new BufferedReader(new InputStreamReader(postBack.getInputStream())));
            postBack.disconnect();

            LOG.debug("Raw result: {}", result.toString());
        } catch (IOException e) {
            LOG.error("Exception while trying to get Sauce job info: {}", e );
        }

        return result;
    }
}