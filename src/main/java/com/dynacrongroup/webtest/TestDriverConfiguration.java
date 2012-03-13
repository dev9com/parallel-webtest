package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.TargetWebBrowser;
import org.json.simple.JSONValue;

import java.util.Map;

/**
 * User: yurodivuie
 * Date: 3/12/12
 * Time: 10:04 AM
 */
public class TestDriverConfiguration {

    private Class testClass;
    private TargetWebBrowser targetWebBrowser;
    private Map<String, Object> customCapabilities;

    public TestDriverConfiguration(Class testClass, TargetWebBrowser targetWebBrowser, Map<String, Object> customCapabilities) {
        this.testClass = testClass;
        this.targetWebBrowser = targetWebBrowser;
        this.customCapabilities = customCapabilities;
    }

    public Class getTestClass() {
        return testClass;
    }

    public void setTestClass(Class testClass) {
        this.testClass = testClass;
    }

    public TargetWebBrowser getTargetWebBrowser() {
        return targetWebBrowser;
    }

    public void setTargetWebBrowser(TargetWebBrowser targetWebBrowser) {
        this.targetWebBrowser = targetWebBrowser;
    }

    public Map<String, Object> getCustomCapabilities() {
        return customCapabilities;
    }

    public void setCustomCapabilities(Map<String, Object> customCapabilities) {
        this.customCapabilities = customCapabilities;
    }

    public String toString() {
        return String.format("%s - %s - %s",
                testClass.getSimpleName(),
                targetWebBrowser.humanReadable(),
                JSONValue.toJSONString(customCapabilities).toString());
    }
}
