package com.lazhu.core.support.thread;
/**
 * 线程任务扩展类
 * 1、记录完整的线程调用堆栈，使异常发生时可以输出完整信息，包括线程提交的位置等信息
 * @author naxj
 *
 */
public abstract class AbstractRunnable implements Runnable {
	private Throwable clientStack;
	
	@Override
	public void run() {
		try {
			runFn();
		} catch (Exception e) {
			if (this.clientStack!=null){
				e.addSuppressed(this.clientStack);
			}
			onException(e);
			throw e;
		}
	}
	/**
	 * 当异常发生会调用此方法
	 * @param e
	 */
	protected void onException(Exception e) {
		e.printStackTrace();
	};
	/**
	 * 记录线程调用的完整堆栈，为定位BUG时使用
	 * @param clientStack
	 */
	final public void setClientStack(Throwable clientStack) {
		this.clientStack = clientStack;
	}
	/**
	 * 线程真正的执行方法，子类实现
	 */
	protected abstract void runFn();

}
