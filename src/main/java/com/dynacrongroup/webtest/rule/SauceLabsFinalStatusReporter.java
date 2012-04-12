package com.dynacrongroup.webtest.rule;

import com.dynacrongroup.webtest.sauce.SauceREST;
import com.dynacrongroup.webtest.util.Path;
import com.google.common.annotations.VisibleForTesting;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOG = LoggerFactory.getLogger(SauceLabsFinalStatusReporter.class);
    private final String jobId;

    private Boolean allTestsPassed = true;

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
            checkTunnel();
            sauceREST.jobFailed(jobId);
        }
    }

    public Boolean getAllTestsPassed() {
        return allTestsPassed;
    }


    private void checkTunnel() {
        if ( new Path().isLocal() && !sauceREST.isTunnelPresent()) {
            LOG.warn("Tests appear to be running against local target, " +
                    "but sauce connect tunnel is not active for user.");
        }
    }
}
