package com.dynacrongroup.webtest;

import org.junit.runners.model.RunnerScheduler;
import org.junit.runners.Parameterized;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Performs the parallelization of the the jobs (i.e. running several browser
 * targets in parallel. Leverages the JUnit Parameterized functionality.
 * 
 * Based on code provided on the SauceLabs blog, which in turn was inspired by
 * http ://hwellmann.blogspot.com/2009/12/running-parameterized-junit-tests-in
 * .html
 */
public class ParallelRunner extends Parameterized {

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

    public ParallelRunner(@SuppressWarnings("rawtypes") Class klass)
	    throws Throwable {
	super(klass);
	setScheduler(new ThreadPoolScheduler());
    }
}
