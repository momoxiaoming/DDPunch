package com.andr.tool.thread;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2016/3/21.
 */
public class ThreadManager {
    public static String DEFALUT_THREADPOOL_NAME = "defalut_thread_name";

    /**
     * 用于长时间的线程池
     */
    private static ThreadPoolProxy mLongPool = null;
    private static Object mLongLock = new Object();

    /**
     * 短时间任务的线程池
     */
    private static ThreadPoolProxy mshortPool = null;
    private static Object mShortLock = new Object();
    /**
     * 下载上传任务的线程池
     */
    private static ThreadPoolProxy mUPLoadPool = null;
    private static Object mUpLoadLock = new Object();


    private static HashMap<String, ThreadPoolProxy> mMap = new HashMap<String, ThreadManager.ThreadPoolProxy>();
    private static Object mSingleLock = new Object();

    /**
     * 获取下载任务的线程池对象
     *
     * @return
     */
    public static ThreadPoolProxy getmUPLoadPool() {
        synchronized (mUpLoadLock) {
            if (mUPLoadPool == null) {
                mUPLoadPool = new ThreadPoolProxy(3, 3, 5L);
            }
            return mUPLoadPool;
        }

    }

    /**
     * 获取一个执行短耗时任务的线程池,避免应为和长时间任务在同一队列二得不到执行
     *
     * @return
     */
    public static ThreadPoolProxy getMshortPool() {
        synchronized (mShortLock) {
            if (mshortPool == null) {
                mshortPool = new ThreadPoolProxy(2, 2, 5L);
            }
            return mshortPool;
        }

    }

    /**
     * 获取一个长时间任务的线程池
     * @return
     */
    public static ThreadPoolProxy getmLoogPool() {
        synchronized (mLongLock) {
            if (mLongPool == null) {
                mLongPool = new ThreadPoolProxy(5, 5, 5L);
            }
            return mLongPool;
        }

    }

    /**
     * 获取一个单线程池,所有任务都会按照假如的顺序执行,避免同步开销的问题
     *
     * @return
     */
    public static ThreadPoolProxy getSinglePool() {

        return getOnePool(DEFALUT_THREADPOOL_NAME);
    }



    private static ThreadPoolProxy getOnePool(String name) {
        synchronized (mSingleLock) {
            ThreadPoolProxy mSinglePool = mMap.get(name);
            if (mSinglePool == null) {
                mSinglePool = new ThreadPoolProxy(1, 1, 5L);
                mMap.put(name, mSinglePool);
            }

            return mSinglePool;
        }
    }


    /**
     * 线程池代理类
     */
    public static class ThreadPoolProxy {
        private ThreadPoolExecutor mpool;  //线程池执行者

        private int mCoreThreadSize;   //最大线程数量

        private int mMaxThreadSize; //最大线程数量

        private long mkeepAlieTime; //线程活跃的时间

        /**
         * @param mCoreThreadSize //最大线程数量
         * @param mMaxThreadSize  //最大线程数量
         * @param mkeepAlieTime   //线程活跃的时间
         */
        public ThreadPoolProxy(int mCoreThreadSize, int mMaxThreadSize, long mkeepAlieTime) {
            this.mCoreThreadSize = mCoreThreadSize;
            this.mMaxThreadSize = mMaxThreadSize;
            this.mkeepAlieTime = mkeepAlieTime;
        }


        /**
         * 执行某个线程
         *
         * @param runnable
         */
        public synchronized void execute(Runnable runnable) {
            if (runnable == null) {
                return;
            }
            if (mpool == null || mpool.isShutdown()) {
                //参数说明
                //当线程池中的线程小于mCorePoolSize，直接创建新的线程加入线程池执行任务
                //当线程池中的线程数目等于mCorePoolSize，将会把任务放入任务队列BlockingQueue中
                //当BlockingQueue中的任务放满了，将会创建新的线程去执行，
                //但是当总线程数大于mMaximumPoolSize时，将会抛出异常，交给RejectedExecutionHandler处理
                //mKeepAliveTime是线程执行完任务后，且队列中没有可以执行的任务，存活的时间，后面的参数是时间单位
                //ThreadFactory是每次创建新的线程工厂
                mpool = new ThreadPoolExecutor(mCoreThreadSize, mMaxThreadSize, mkeepAlieTime, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
            }
            mpool.execute(runnable);
        }

        /**
         * 取消线程池中某个还未执行的任务
         */
        public synchronized void cancel(Runnable runnable) {

            //如果线程池不为空且没有被释放或者正在终止却未终止完成
            if (mpool != null && (!mpool.isShutdown() || mpool.isTerminating())) {

                mpool.getQueue().remove(runnable);
            }

        }

        /**
         * 检查线程池中是否存在某个任务
         *
         * @param runnable
         * @return
         */
        public synchronized boolean contains(Runnable runnable) {
            if (mpool != null && (!mpool.isShutdown() || mpool.isTerminating())) {

                return mpool.getQueue().contains(runnable);
            }
            return false;
        }

        /**
         * 强制关闭线程池,未执行的任务会立即终止
         *
         * @param runnable
         */
        public void stop(Runnable runnable) {
            if (mpool != null && (!mpool.isShutdown() || mpool.isTerminating())) {

                mpool.shutdownNow();
            }


        }

        /**
         * 平缓关闭线程池,线程池中的任务会全部执行完毕才会关闭
         */
        public void shutDown() {
            if (mpool != null && (!mpool.isShutdown() || mpool.isTerminating())) {
                mpool.shutdown();
            }

        }

    }
}
