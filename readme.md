# What is parallel-webtest?

parallel-webtest is a Java library Selenium.
It makes running parallel tests with multiple browsers through Sauce Labs easy and fast.

# Getting Started

The easiest way to get started writing tests is to add parallel-webtest as a dependency, and create a new JUnit test
using package com.dynacrongroup.webtest.webdriverbase.WebDriverUtilitiesTest as your pattern.  Check back soon for more
detailed examples.

In addition, you'll want to add the following environment variables:

* SAUCELABS_USER: your sauce labs user name.
* SAUCELABS_KEY: your sauce labs key.

Tests run by default in Firefox, locally.

# Configuring Test Parameters

The application.conf file in src/test/resources is used to configure parameters that will be fed into each test.  By
default this includes the browser and the target language.

# Parameter Logging

As each test method finishes, the com.dynacrongroup.webtest.rule.ParameterResultReport rule will log out the full name
of the test, including class, method, and parameters, pass/fail, and the url of the test in Sauce Labs (if applicable).
Using logback, one can configure this rule to print to a single file for tracking purposes.

# Changes from v1.x

v2 of parallel-webtest brings lots of changes!

* Configuration now uses TypeSafeConfiguration.  See application.conf under src/test/resources for an example.
* Configuration values now follow TypeSafeConfiguration syntax/standards (for example, SAUCELABS_USER is now
    set as "saucelabs.user".
* Parameters for the test now use a single ParameterCombination object, which can be extended to provide different
parameter combinations for different testing needs.
