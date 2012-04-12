package com.dynacrongroup.webtest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: yurodivuie
 * Date: 4/8/12
 * Time: 2:05 PM
 */

@RunWith(ParallelRunner.class)
public class ParameterizedTest {

    private static final Logger LOG = LoggerFactory.getLogger(ParameterizedTest.class);

    private String parameter;

    public ParameterizedTest(String parameter) {
        this.parameter = parameter;
    }

    @Test
    public void soEasy() {
        LOG.info("In class: " + parameter);
    }

    @DescriptivelyParameterized.Parameters
    public static List<String[]> parameters() throws IOException {
        String[] param1 = new String[] {"1"};
        String[] param2 = new String[] {"2"};
        List<String[]> array = new ArrayList<String[]>();
        array.add(param1);
        array.add(param2);
        return array;
    }

}
