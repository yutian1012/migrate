package org.ipph.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
	private ExecutorService threadPool = Executors.newFixedThreadPool(10, new NamedThreadFactory("migration"));
	
	
}
