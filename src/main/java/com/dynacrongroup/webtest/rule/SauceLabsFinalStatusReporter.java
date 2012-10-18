package com.dynacrongroup.webtest.rule;

import com.dynacrongroup.webtest.util.WebDriverUtilities;
import com.dynacrongroup.webtest.sauce.SauceREST;
import com.google.common.annotations.VisibleForTesting;
import org.json.simple.JSONObject;
import org.junit.runner.Description;
import org.slf4j.Logger;

import java.util.Date;

/**
 * Rule interacts with the SauceREST interface to report job pass/fail results.
 * <p/>
 * User: yurodivuie
 * Date: 3/11/12
 * Time: 9:47 AM
 */
public class SauceLabsFinalStatusReporter extends AbstractClassFinishRule {

    @VisibleForTesting
    SauceREST sauceREST = null;

    private Logger log;
    private final String jobId;

    private Boolean allTestsPassed = true;

    public SauceLabsFinalStatusReporter(Logger log, String jobId, String user, String key) {
        this.log = log;
        this.jobId = jobId;
        this.sauceREST = new SauceREST(user, key);
    }

    @Override
    protected void failed(Throwable e, Description description) {

        super.failed(e, description);
        allTestsPassed = false;
        logFailure(description);
    }

    @Override
    protected void classFinished(Description description) {
        sendFinalTestStatusForJobId(jobId);
    }

    private void logFailure(Description description) {
        try {
            JSONObject jobStatus = sauceREST.getJobStatus(jobId);
            Long startTimeInSeconds = (Long) jobStatus.get("start_time");
            Long currentTimeInSeconds = new Date().getTime() / 1000;
            Long elapsedTestTime =  currentTimeInSeconds - startTimeInSeconds;

            Object[] logArgs = {description.getMethodName(),
                    elapsedTestTime,
                    WebDriverUtilities.getJobUrlFromId(jobId)};

            log.error("Test {} failed roughly {} seconds into job execution.  View job on Sauce Labs at {}", logArgs);
        } catch (Exception exception) {
            log.error("Failed to report test failure time: {}", exception.getMessage());
        }

    }

    private void sendFinalTestStatusForJobId(String jobId) {
        if (getAllTestsPassed()) {
            sauceREST.jobPassed(jobId);
        } else {
            sauceREST.jobFailed(jobId);
        }
    }

    public Boolean getAllTestsPassed() {
        return allTestsPassed;
    }
}
