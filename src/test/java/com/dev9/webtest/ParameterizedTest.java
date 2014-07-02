package com.dev9.webtest;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: yurodivuie
 * Date: 4/8/12
 * Time: 2:05 PM
 */

//@RunWith(ParallelRunner.class)
    @Ignore
public class ParameterizedTest {

    private static final Logger LOG = LoggerFactory.getLogger(ParameterizedTest.class);

    private String parameter;

/*    public ParameterizedTest(String parameter) {
        this.parameter = parameter;
    }*/

    @Test
    public void soEasy() {
        LOG.info("In class: " + parameter);
    }

    //TODO: rewrite.  Our parameterized class is no longer string based.
/*    @ParameterCombinationRunner.Parameters
    public static List<String[]> parameters(Class klass) throws IOException {
        String[] param1 = new String[] {"1"};
        String[] param2 = new String[] {"2"};
        List<String[]> array = new ArrayList<String[]>();
        array.add(param1);
        array.add(param2);
        return array;
    }*/

}
