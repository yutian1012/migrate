package org.ipph.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;

@Component
public class ThreadPool {
	private ExecutorService threadPool = Executors.newFixedThreadPool(10, new NamedThreadFactory("migration"));
	/**
	 * 向线程池中放入执行线程
	 * @param task
	 */
	public void addTask(Runnable task){
		threadPool.execute(task);
	}
}
