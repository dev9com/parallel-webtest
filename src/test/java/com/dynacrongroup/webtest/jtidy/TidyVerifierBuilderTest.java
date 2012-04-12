package com.dynacrongroup.webtest.jtidy;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * User: yurodivuie
 * Date: 4/12/12
 * Time: 10:05 AM
 */
public class TidyVerifierBuilderTest {

    @Test
    public void getCodesFromPropFile() {
        TidyVerifierBuilder builder = new TidyVerifierBuilder();
        builder.build();

        List<Integer> ignoredCodes = builder.getListener().ignoredCodes;

        assertThat(ignoredCodes, hasItem(48));
        assertThat(ignoredCodes, hasItem(49));

    }

    @Test
    public void getMessagesFromPropFile() {
        TidyVerifierBuilder builder = new TidyVerifierBuilder();
        builder.build();

        List<String> ignoredMessages = builder.getListener().ignoredMessages;

        assertThat(ignoredMessages, hasItem("attribute \"tabindex\" has invalid value \"-1\""));
        assertThat(ignoredMessages, hasItem("This document has errors that must be fixed"));

    }

}
