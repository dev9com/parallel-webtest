# What is parallel-webtest ?

parallel-webtest is a Java library Selenium.
It makes running parallel tests with multiple browsers through Sauce Labs easy and fast.

# Getting Started

The easiest way to get started writing tests is to add parallel-webtest as a dependency, and create a new JUnit test
using package com.dynacrongroup.webtest.webdriverbase.WebDriverUtilitiesTest as your pattern.  Check back soon for more
detailed examples.

In addition, you'll want to add the following environment variables:

* saucelabs.user: your sauce labs user name.
* saucelabs.key: your sauce labs key.

Tests run by default in Firefox, locally.

# Changes from v1.X

v2 of parallel-webtest brings lots of changes!

* Configuration now uses TypeSafeConfiguration.  See application.conf under src/test/resources for an example.
* Configuration values now follow TypeSafeConfiguration syntax/standards (for example, SAUCELABS_USER is now
    set as "saucelabs.user".
* Parameters for the test now use a single ParameterCombination object, which can be extended to provide different
parameter combinations for different testing needs.
