package com.dynacrongroup.webtest.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;


/**
 * Based on Sauce Labs code, on github: https://github.com/saucelabs/saucerest-java.
 * <p/>
 * Used to set the pass/fail status on tests.
 */
public class SauceREST {

    protected String username;
    protected String accessKey;


    private static final Logger LOG = LoggerFactory.getLogger(SauceREST.class);

    public SauceREST(String username, String accessKey) {
        this.username = username;
        this.accessKey = accessKey;
    }

    public JSONObject getAccountDetails() {
        SauceRESTRequest request = new SauceRESTRequestBuilder()
                .setHTTPMethod("GET")
                .addUsersToPath()
                .addUserIdToPath(username)
                .build();

        return (JSONObject) sendRestRequest(request);
    }


    public JSONObject getUsageData() {
        SauceRESTRequest request = new SauceRESTRequestBuilder()
                .setHTTPMethod("GET")
                .addUsersToPath()
                .addUserIdToPath(username)
                .addGenericSuffix("/usage")
                .build();

        return (JSONObject) sendRestRequest(request);
    }

    public JSONArray getAllJobs() {
        SauceRESTRequest request = new SauceRESTRequestBuilder()
                .setHTTPMethod("GET")
                .addUserIdToPath(username)
                .addJobsToPath()
                .build();

        return (JSONArray) sendRestRequest(request);
    }

    public JSONObject getJobStatus(String jobId) {
        SauceRESTRequest request = new SauceRESTRequestBuilder()
                .setHTTPMethod("GET")
                .addUserIdToPath(username)
                .addJobsToPath()
                .addJobIdToPath(jobId)
                .build();

        return (JSONObject) sendRestRequest(request);
    }

    public JSONObject jobPassed(String jobId) {
        return updateJob(jobId, "passed", true);
    }

    public JSONObject jobFailed(String jobId) {
        return updateJob(jobId, "passed", false);
    }

    public JSONObject updateJob(String jobId, String jsonKey, Object jsonValue) {
        SauceRESTRequest request = new SauceRESTRequestBuilder()
                .addJSON(jsonKey, jsonValue)
                .setHTTPMethod("PUT")
                .addUserIdToPath(username)
                .addJobsToPath()
                .addJobIdToPath(jobId)
                .build();

        return (JSONObject) sendRestRequest(request);
    }

    /**
     * Stop a currently running job.  Should not be necessary to use in WebDriverBase
     *
     * @param jobId
     * @return
     */
    public JSONObject stopJob(String jobId) {
        SauceRESTRequest request = new SauceRESTRequestBuilder()
                .setHTTPMethod("PUT")
                .addUserIdToPath(username)
                .addJobsToPath()
                .addJobIdToPath(jobId)
                .addGenericSuffix("/stop")
                .build();

        return (JSONObject) sendRestRequest(request);
    }

    public boolean isTunnelPresent() {
        JSONArray tunnels = getAllTunnels();
        return (!tunnels.isEmpty());
    }

    public JSONArray getAllTunnels() {
        SauceRESTRequest request = new SauceRESTRequestBuilder()
                .setHTTPMethod("GET")
                .addUserIdToPath(username)
                .addGenericSuffix("/tunnels")
                .build();

        return (JSONArray) sendRestRequest(request);
    }

    public JSONObject getTunnelStatus(String tunnelId) {
        SauceRESTRequest request = new SauceRESTRequestBuilder()
                .setHTTPMethod("GET")
                .addUserIdToPath(username)
                .addGenericSuffix("/tunnels/")
                .addGenericSuffix(tunnelId)
                .build();

        return (JSONObject) sendRestRequest(request);
    }

    public JSONObject deleteTunnel(String tunnelId) {
        SauceRESTRequest request = new SauceRESTRequestBuilder()
                .setHTTPMethod("DELETE")
                .addUserIdToPath(username)
                .addGenericSuffix("/tunnels/")
                .addGenericSuffix(tunnelId)
                .build();

        return (JSONObject) sendRestRequest(request);
    }

    public JSONObject getSauceStatus() {
        SauceRESTRequest request = new SauceRESTRequestBuilder()
                .setHTTPMethod("GET")
                .addGenericSuffix("/info/status")
                .build();

        return (JSONObject) sendRestRequest(request);
    }

    public JSONArray getSauceBrowsers() {
        SauceRESTRequest request = new SauceRESTRequestBuilder()
                .setHTTPMethod("GET")
                .addGenericSuffix("/info/browsers")
                .build();

        return (JSONArray) sendRestRequest(request);
    }


    /**
     * @deprecated  Use sendRestRequest
     * @param request   request created by SauceRESTRequestBuilder
     * @return  Json from Sauce Labs
     */
    public Object sendHttpUrlConnectionRestRequest(SauceRESTRequest request) {

        Object result = null;

        try {
            String auth = username + ":" + accessKey;
            auth = "Basic " + new String(Base64.encodeBase64(auth.getBytes()));

            HttpURLConnection postBack = (HttpURLConnection) request.getRequestUrl().openConnection();
            postBack.setDoInput(true);
            postBack.setUseCaches(false);
            postBack.setRequestMethod(request.getMethod());
            postBack.setRequestProperty("Authorization", auth);

            if (request.getJsonParameters() != null) {
                postBack.setDoOutput(true);
                postBack.setRequestProperty("Content-Type", "application/json");
                OutputStream stream = postBack.getOutputStream();
                stream.write(request.getJsonParameters().getBytes());
                stream.close();
            } else {
                postBack.setRequestProperty("content-length", "0");
            }

            Integer responseCode = postBack.getResponseCode();
            if (responseCode == 200) {
                result = JSONValue.parse(new BufferedReader(new InputStreamReader(postBack.getInputStream())));
            } else {
                LOG.error("Received response code {}: {}", responseCode, postBack.getResponseMessage());
            }
            postBack.disconnect();

            LOG.trace("Raw result: {}", result.toString());
        } catch (IOException e) {
            LOG.error("Exception while trying to execute rest request: {}\n{}",
                    new Object[]{e.getMessage(), e.getStackTrace()});
        }

        return result;
    }


    public Object sendRestRequest(SauceRESTRequest request) {

        Object result = null;

        try {
            HttpClient client = new HttpClient();

            Credentials defaultcreds = new UsernamePasswordCredentials(username, accessKey);
            client.getState().setCredentials(AuthScope.ANY, defaultcreds);

            HttpMethod method = null;

            if (request.getMethod().equalsIgnoreCase("get")) {
                method = new GetMethod(request.requestUrl.toExternalForm());
            } else if (request.getMethod().equalsIgnoreCase("delete")) {
                method = new DeleteMethod(request.requestUrl.toExternalForm());
            } else {

                EntityEnclosingMethod eMethod = null;

                if (request.getMethod().equalsIgnoreCase("post")) {
                    eMethod = new PostMethod(request.requestUrl.toExternalForm());
                } else if (request.getMethod().equalsIgnoreCase("put")) {
                    eMethod = new PutMethod(request.requestUrl.toExternalForm());
                }

                if (request.getJsonParameters() != null) {
                    eMethod.setRequestEntity(new StringRequestEntity(request.getJsonParameters(), "application/json", "utf-8"));
                }

                method = eMethod;
            }



            Integer responseCode = client.executeMethod(method);
            if (responseCode == 200) {
                result = JSONValue.parse(method.getResponseBodyAsString());
            } else {
                LOG.error("Received response code {}: {}", responseCode, method.getResponseBodyAsString());
            }

            LOG.trace("Raw result: {}", result.toString());
        } catch (IOException e) {
            LOG.error("Exception while trying to execute rest request: {}\n{}",
                    new Object[]{e.getMessage(), e.getStackTrace()});
        }

        return result;
    }

}