package com.eriklievaart.antastic.ant;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AntScheduler {

	private static final ExecutorService SEQUENTIAL = Executors.newSingleThreadExecutor();

	public static void schedule(Runnable runnable) {
		SEQUENTIAL.execute(runnable);
	}

	public static void shutdownOnComplete() {
		SEQUENTIAL.shutdown();
	}
}
