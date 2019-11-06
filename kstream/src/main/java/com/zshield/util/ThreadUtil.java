package com.zshield.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadUtil extends Thread {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public ThreadUtil(final String name, Runnable runnable, boolean daemon, UncaughtExceptionHandler uncaughtExceptionHandler) {
        super(runnable, name);
        configureThread(name, daemon,uncaughtExceptionHandler);
    }

    public ThreadUtil(final String name, boolean daemon, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        super(name);
        configureThread(name, daemon,uncaughtExceptionHandler);
    }

    public static  Thread daemon(final String name, Runnable runnable) {
        return daemon(name, runnable, null);
    }

    public static Thread daemon(final String name, Runnable runnable, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        return new ThreadUtil(name, runnable , true, uncaughtExceptionHandler);
    }

    public static Thread nonDaemon(final String name, Runnable runnable, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        return new ThreadUtil(name, runnable,false,uncaughtExceptionHandler);
    }

    public static Thread nonDaemon(final String name,Runnable runnable) {
        return nonDaemon(name, runnable,null);
    }

    private void configureThread(String name, boolean daemon, UncaughtExceptionHandler uncaughtExceptionHandler) {
        //Marks this thread as either a {@linkplain #isDaemon daemon} thread or a user thread.
        // The Java Virtual Machine exits when the only threads running are all daemon threads.
        //将此线程标记为{@linkplain #isDaemon daemon}线程或用户线程。当惟一运行的线程都是守护进程线程时，Java虚拟机退出
        setDaemon(daemon);
        if (uncaughtExceptionHandler == null) {
            setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    logger.error("Uncaught exception in thread '{}' :",name, e);
                }
            });
        } else {
            //Set the handler invoked when this thread abruptly terminates due to an uncaught exception.
            //
            //设置此线程由于未捕获异常而突然终止时调用的处理程序。
            setUncaughtExceptionHandler(uncaughtExceptionHandler);
        }

    }

}
