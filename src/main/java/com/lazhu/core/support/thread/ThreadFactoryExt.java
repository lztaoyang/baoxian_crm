package com.lazhu.core.support.thread;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.ClassUtils;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

public class ThreadFactoryExt extends CustomizableThreadFactory{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final AtomicInteger threadCount = new AtomicInteger(0);
	@Override
	public Thread newThread(Runnable runnable) {
		return createThread(runnable);
	}
	public Thread createThread(Runnable runnable) {
		Thread thread = new Thread(getThreadGroup(), runnable, nextThreadName(runnable));
		thread.setPriority(getThreadPriority());
		thread.setDaemon(isDaemon());
		return thread;
	}

	protected String nextThreadName(Runnable runnable) {
		String clzName = ClassUtils.getShortClassName(runnable.getClass());
		return getThreadNamePrefix() + clzName+"_"+this.threadCount.incrementAndGet();
	}
}
