package com.eriklievaart.antastic.ant;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class AntScheduler {

	/** return a non-zero status code if the application has been run headless and a job failed. */
	static final AtomicBoolean DIRTY = new AtomicBoolean();
	private static final ExecutorService SEQUENTIAL = Executors.newSingleThreadExecutor();

	public static void schedule(Runnable runnable) {
		SEQUENTIAL.execute(runnable);
	}

	public static void awaitTermination() throws InterruptedException {
		SEQUENTIAL.shutdown();
		SEQUENTIAL.awaitTermination(1, TimeUnit.HOURS);
	}
}
