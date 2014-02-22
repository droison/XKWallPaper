package com.xkwallpaper.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 线程池框架使用类
 * 
 * @author chaisong
 * 
 */
public abstract class ThreadExecutor {

	/**
	 * 线程池框架执行
	 */
	private static ExecutorService executor = null;

	/**
	 *线程数目
	 */
	private static int nThreads = 20;

	/**
	 * 初始化
	 */
	static {
		executor = Executors.newFixedThreadPool(nThreads);
	}
	
	private static Future transPending;

	/**
	 * 执行类
	 * 
	 * @param t
	 *            线程
	 */
	public static void execute(Runnable t) {
	//	transPending = executor.submit(t);
		executor.execute(t);
	}
}
