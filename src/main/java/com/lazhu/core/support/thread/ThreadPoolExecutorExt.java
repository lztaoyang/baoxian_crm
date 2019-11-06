package com.lazhu.core.support.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.ClassUtils;
/**
 * ThreadPoolExecutor扩展
 * 1、记录完整的线程堆栈，使异常发生时可以输出完整信息，包括线程提交的位置等信息
 * 2、在线程运行前给线程重新命名，使通过线程名即可知道某线程当前正在执行的任务
 * @author naxj
 *
 */
public class ThreadPoolExecutorExt extends ThreadPoolExecutor{
    public ThreadPoolExecutorExt(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		// TODO Auto-generated constructor stub
	}
    
	public ThreadPoolExecutorExt(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
		// TODO Auto-generated constructor stub
	}

	public ThreadPoolExecutorExt(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
		// TODO Auto-generated constructor stub
	}

	public ThreadPoolExecutorExt(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 线程执行前，给线程改一个有意义的线程名
	 */
	protected void beforeExecute(Thread t, Runnable r) { 
		String clzName = ClassUtils.getShortClassName(r.getClass());
		String threadName = "threadPool_" + clzName+"_"+System.currentTimeMillis();
		t.setName(threadName);
	}
    protected void afterExecute(Runnable r, Throwable t) { 
    	String threadName = "threadPool_free_"+System.currentTimeMillis();
    	Thread.currentThread().setName(threadName);
    }
//    @Override
//    public void execute(Runnable task){
//    	super.execute(wrap(task,clientTrace(),Thread.currentThread().getName()));
//    }
//    public Future<?> submit(Runnable task){
//    	return super.submit(wrap(task,clientTrace(),Thread.currentThread().getName()));
//    }
//    private Exception clientTrace(){
//    	return new Exception ("Thread Client stack trace");
//    }
//    
//    private Runnable wrap(final Runnable task,final Exception clientStack,String clientThreadName){
//    	return new Runnable(){
//    		public Class<?> getTaskClass(){
//    			return task.getClass();
//    		}
//    		public void run(){
//    			try{
//    				task.run();
//    			}catch (Exception e){
//    				e.addSuppressed(clientStack);
////    				clientStack.addSuppressed(e);
////    				clientStack.printStackTrace();
//
//    				if (task instanceof RunnableExt){
//    					((RunnableExt) task).onException(e);
//    				}
//    				throw e;
//    			}
//    		}
//    	};
//    }
}
