package com.dynacrongroup.webtest.rule;

import com.dynacrongroup.webtest.sauce.SauceREST;
import com.google.common.annotations.VisibleForTesting;
import org.junit.runner.Description;

/**
 * Rule interacts with the SauceREST interface to report job pass/fail results.
 *
 * User: yurodivuie
 * Date: 3/11/12
 * Time: 9:47 AM
 */
public class SauceLabsFinalStatusReporter extends ClassFinishRule {

    @VisibleForTesting
    SauceREST sauceREST = null;

    private Boolean allTestsPassed = true;

    private final String jobId;

    public SauceLabsFinalStatusReporter(String jobId, String user, String key) {
        this.jobId = jobId;
        this.sauceREST = new SauceREST(user, key);
    }

    @Override
    protected void failed(Throwable e, Description description) {
        super.failed(e, description);
        allTestsPassed = false;
    }

    @Override
    protected void classFinished(Description description) {
        sendFinalTestStatusForJobId(jobId);
    }

    private void sendFinalTestStatusForJobId(String jobId) {
        if (getAllTestsPassed()) {
            sauceREST.jobPassed(jobId);
        }
        else {
            sauceREST.jobFailed(jobId);
        }
    }

    public Boolean getAllTestsPassed() {
        return allTestsPassed;
    }
}
