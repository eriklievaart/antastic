package com.eriklievaart.antastic.ant;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AntScheduler {

	private static final Executor SEQUENTIAL = Executors.newSingleThreadExecutor();

	public static void schedule(Runnable runnable) {
		SEQUENTIAL.execute(runnable);
	}
}
