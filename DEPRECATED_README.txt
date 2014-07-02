The full tutorial and documentation for parallel-webtest can be found in the
generated site:

http://repository-dev9.forge.cloudbees.com/release/sites/parallel-webtest/tutorial.html

Configuring parallel-webtest

 The parallel-webtest library can be configured using either system properties,
 environment variables, or default settings.  The variable names are listed
 below, with an explanation of the defaults used and how they interact.

* <<WEBTEST_HOSTNAME>>: no default

     This is the only required configuration setting.
     It is typically set as an environment variable referring to your full computer
     name, which can be found on a Windows 7 computer by right clicking on
     "Computer" under the start menu and selecting "properties".  On Macs running
     Lion, this can be found under "sharing" in "System Preferences" - typically
     "{somecomputername}.local".

* <<SAUCELABS_USER>>: defaults to null

     This configuration is usually set as an
     environment variable.  It is required for running tests in Sauce Labs.  It can
     be set to either a Sauce Labs account name or to a Subaccount name.

* <<SAUCELABS_KEY>>: defaults to null

     This configuration is usually set as an
     environment variable.  It is required for running tests in Sauce Labs.  It is
     set to the Sauce Labs key that corresponds to the above Sauce Labs user.

* <<DEFAULT_TARGETS>>: default "firefox:5,iexplore:7,iexplore:8,iexplore:9,chrome:*"

     This is the default list of browsers that tests will be run against in Sauce
     Labs.  If the WEBDRIVER_DRIVER and SINGLE_SAUCE configurations are set to null
     or are not set, then tests will, by default, run against this list of browsers.
     The list should be in the above format: {browser}:{version}, with each entry
     comma-delimited.

* <<WEBDRIVER_DRIVER>>: defaults to null

     This variable is typically set as a System
     Property Variable in either your surefire or failsafe configuration.  If it is
     set to a local driver class (like org.openqa.selenium.firefox.FirefoxDriver),
     then all tests will be run locally using that driver.  This will fail if the
     browser called by the driver is not installed locally.

* <<SINGLE_SAUCE>>: defaults to null

     This variable is also usually set as a System
     Property Variable in either surefire or failsafe, if used.  If it is set to a
     browser/version combination (like firefox:7, iexplore:8, or chrome:*), then all
     tests will be run against this browser remotely in Sauce Labs.

* <<SAUCELABS_SERVER>>: defaults to "ondemand.saucelabs.com/wd/hub"

     This is
     generally never changed, but could be used to direct tests to a non-standard
     location.

* <<WEBDRIVER_SERVER>>: defaults to WEBTEST_HOSTNAME

     Used by the "Path" utility
     object to provide the server name for tests to be directed to.  It can be set
     to override the default behavior to run against a non-local server (like a
     QA instance).

* <<WEBDRIVER_PROTOCOL>>: defaults to "http"

     Used by the "Path" utility object to
     set the protocol for constructed urls.

* <<WEBDRIVER_PORT>>: defaults to 8080 if the protocol is "http", or 443 if the
 protocol is "https"

     Used by the Path utility object to set the port number
     for constructed urls.

* <<WEBDRIVER_CONTEXT>>: defaults to null

     Used by the Path utility object to set
     the context for constructed urls.  If it is set, a leading slash should be
     included ("/webapp", for example).

 In summation, the path object will by default construct the url
 http://${WEBTEST_HOSTNAME}:8080 if no values are set for the four above
 configurable settings.