Parallel Webtest
==================

**NOTE: This project has been *deprecated*. Use at your own risk!**

parallel-webtest is a Java library for Selenium written and maintained by [Dev9](http://www.dev9.com)
It makes running parallel tests with multiple browsers through [Sauce Labs](http://www.saucelabs.com) easy and fast.
While you're developing your tests, you can use a local browser, but switching to running multiple remote browsers is
as easy as using a single system parameter.

Getting Started
------------------

The easiest way to get started adding tests to an existing project is to add parallel-webtest as a dependency, and
create a new JUnit test using package com.dev9.webtest.webdriverbase.WebDriverUtilitiesTest as your pattern.

A more detailed example can be found in our [webtest quickstart](https://github.com/dev9com/webtest-quickstart).

Tests are run by default in Firefox, locally, as configured in src/main/resources/reference.conf.

Test Configuration
-------------------

Tests are configured using the [TypeSafe Config](https://github.com/typesafehub/config) library.  When you use
parallel-webtest as a dependency, src/main/resources/reference.conf is applied as the default configuration, which will
be overridden by your own configuration file.  This file should be located at src/test/resources/application.conf.

Configuring Test Parameters
-------------------

The application.conf file in src/test/resources is used to configure parameters that will be fed into each test.  By
default this includes the browser and the target language.

If only a single value is provided for each parameter, then each test will be run only once for that configuration.  The
parameter will be deserialized into a ParameterCombination object, which will be passed into the constructor for the
test class.  If an array of values is provided instead, then a number of parameter combinations will be created, and
each test will be executed once per parameter combination.  The parameter combination strategy determines how many
combinations will be made (if there is more than one parameter with multiple values).

For example, if you were testing locally against a single firefox instance:

````
parameters {
    webDriverConfig: {
        type:       local
        browser:    firefox
    }
}
````

However, if you wanted to run locally against both Chrome and Firefox:

````
parameters {
    webDriverConfig: [
        {
            type:       local
            browser:    firefox
        }
        {
            type:       local
            browser:    chrome
        }
    ]
}
````

Profiles are also supported by using the system property variable "webtest.profile".  To create a set of parameters
that can be triggered using a profile, nest them in under the name of the profile, like so:

````
local-chrome-only {
    parameters {
        webDriverConfig: {
            type:       local
            browser:    chrome
        }
    }
}
````

When profiles are used, they overlay over the default parameters object, so you only need to specify as much or as
little as you need to override.  The default ParameterCombination object only parameterizes browsers and languages, but
you can extend this object for your own tests with new parameters, as necessary.

Parameter Logging
-------------------

As each test method finishes, the com.dev9.webtest.rule.ParameterResultReport rule will log out the full name
of the test, including class, method, and parameters, pass/fail, and the url of the test in Sauce Labs (if applicable).
Using logback, one can configure this rule to print to a single file for tracking purposes.

Sauce Labs Integration
-------------------

Configuring your test to run in Sauce Labs is simple, but requires two things: parameter configuration of the remote
driver, and Sauce Labs credentials.

If you want to run your tests through Sauce Labs, you'll want to add the following environment variables:

* SAUCELABS_USER: your Sauce Labs user name.
* SAUCELABS_KEY: your Sauce Labs key.

Next, you'll need to create a profile that uses remote browsers in your application.conf.  For example:

````
single-remote {
    parameters {
        webDriverConfig {
            type:       remote
            browser:    firefox
            version:    15
            platform:   Windows 2012
        }
    }
}
````

Running many browsers in parallel on Sauce Labs can be a very efficient use of resources, since most of the processing
power and memory required for running a test is used by the browser.

Changes from v1.x
-------------------

v2 of parallel-webtest brings lots of changes!

* Configuration now uses TypeSafeConfiguration.  See application.conf under src/test/resources for an example.
* Configuration values now follow TypeSafeConfiguration syntax/standards (for example, SAUCELABS_USER is now
    set as "saucelabs.user".
* Parameters for the test now use a single ParameterCombination object, which can be extended to provide different
parameter combinations for different testing needs.
