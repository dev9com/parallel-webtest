# What is parallel-webtest ?

parallel-webtest is a Java library Selenium.
It makes running parallel tests with multiple browsers through Sauce Labs easy and fast.

# Getting Started

The easiest way to get started writing tests is to add parallel-webtest as a dependency, and create a new JUnit test
using package com.dynacrongroup.webtest.webdriverbase.WebDriverUtilitiesTest as your pattern.  Check back soon for more
detailed examples.

In addition, you'll want to add the following environment variables:

* SAUCELABS_USER: your sauce labs user name.
* SAUCELABS_KEY: your key.

Tests run by default in Firefox, locally.

# Changes from v1.X

v2 of parallel-webtest brings lots of changes which break backwards compatibility.

* Configuration now uses TypeSafeConfiguration.  See application.conf for an example.
* Parameters for the test now use a single ParameterCombination object, which can be extended to provide different
parameter combinations for different testing needs.
