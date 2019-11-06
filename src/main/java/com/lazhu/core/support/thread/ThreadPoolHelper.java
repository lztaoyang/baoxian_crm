package com.lazhu.core.support.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.apache.commons.lang.ClassUtils;

import com.lazhu.core.config.SpringContext;
/**
 * 线程池辅助类
 * @author naxj
 *
 */
public class ThreadPoolHelper {
	//线程池
	private static ThreadPoolTaskExecutorExt taskExecutor;

	// 获取连接
	private static ThreadPoolTaskExecutorExt getTaskExecutor() {
		if (taskExecutor == null) {
			synchronized (ThreadPoolHelper.class) {
				if (taskExecutor == null) {
					taskExecutor = SpringContext.getBean(ThreadPoolTaskExecutorExt.class);
				}
			}
		}
		return taskExecutor;
	}

	/**
	 * 向线程池中添加一个任务
	 * @param task
	 */
	public static void taskExecute(Runnable task){
		//如果可以，那么记录当前堆栈信息
		if (task instanceof AbstractRunnable){
			((AbstractRunnable) task).setClientStack(clientStack());
		}
		getTaskExecutor().execute(task);
	}
	/**
	 * 向线程池中添加一个任务
	 * @param <T>
	 * @param task
	 * @return Future
	 */
	public static <T> Future<T> taskSubmit(Callable<T> task){
		return getTaskExecutor().submit(task);
	}
	/**
	 * 根据类名返回执行线程
	 * @param cls 实现Runnable的类名
	 */
	public static List<Thread> getThreadStatusByClsName(Class<?> cls){
		List<Thread> threadList = new ArrayList<Thread>();
		String clzName = ClassUtils.getShortClassName(cls);
		String threadName = "threadPool_" + clzName+"_";
		ThreadGroup group = getTaskExecutor().getThreadGroup();
		Thread[] threads = new Thread[(int)(group.activeCount() * 1.2)];
        int count = group.enumerate(threads, true);
        for(int i = 0; i < count; i++) {
        	if (threads[i].getName().startsWith(threadName)){
        		threadList.add(threads[i]);
        	}
        }
        if (threadList.isEmpty()){
        	return null;
        }else{
        	return threadList;
        }
	}
	
	/**
	 * 获取当前堆栈信息
	 * @return
	 */
	private static Throwable clientStack() {
		return new Exception("client stack");
	}
}
