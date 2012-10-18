package com.dynacrongroup.webtest.base;

import org.junit.runners.model.RunnerScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Performs the parallelization of the the jobs (i.e. running several browser
 * targets in parallel. Leverages the JUnit Parameterized functionality.
 * <p/>
 * Based on code provided on the SauceLabs blog, which in turn was inspired by
 * http ://hwellmann.blogspot.com/2009/12/running-parameterized-junit-tests-in
 * .html
 */
public class ParallelRunner extends DescriptivelyParameterized {

    private static class ThreadPoolScheduler implements RunnerScheduler {

        private final ExecutorService executor;

        public ThreadPoolScheduler() {
            executor = Executors.newCachedThreadPool();
        }


        @Override
        public void finished() {
            executor.shutdown();
            try {
                executor.awaitTermination(10, TimeUnit.MINUTES);
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
        }

        @Override
        public void schedule(Runnable childStatement) {
            executor.submit(childStatement);
        }
    }

    /**
     * Creates ParallelRunner.  Shouldn't be called by classes directly; instead should be used
     * with JUnit annotations: @RunWith(ParallelRunner.class)
     * @param klass
     * @throws Throwable
     */
    public ParallelRunner(@SuppressWarnings("rawtypes") Class klass)
            throws Throwable {
        super(klass);
        setScheduler(new ThreadPoolScheduler());
    }
}
