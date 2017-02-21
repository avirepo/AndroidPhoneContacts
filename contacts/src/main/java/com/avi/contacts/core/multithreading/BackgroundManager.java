package com.avi.contacts.core.multithreading;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.avi.contacts.core.OnDataListener;
import com.avi.contacts.core.logs.LoggerFactory;
import com.avi.contacts.core.logs.Logger;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Class BackgroundManager created on 16/05/16 - 4:22 PM.
 * All copyrights reserved to the Zoomvy.
 * Class behaviour is to provide a common interface to process task on background
 * <p/>
 * Their are two type of background thread queue is available Single thread and Multi-thread.
 * Multi-thread queue can execute maximum 4 thread parallely
 */
@SuppressWarnings("unused")
public class BackgroundManager {
    private static final Logger LOGGER = LoggerFactory.createLogger(BackgroundManager.class);
    private static final int EXECUTOR_THREAD_COUNT = 2;

    private static volatile BackgroundManager sInstance;
    private ExecutorService mSingleExecutor;
    private ExecutorService mExecutor;
    private ScheduledExecutorService mScheduledExecutorService;
    private ExecutorService mTaskScheduler;
    private Handler mHandler = new Handler(Looper.getMainLooper());


    public static BackgroundManager getInstance() {
        if (null == sInstance) {
            synchronized (BackgroundManager.class) {
                sInstance = new BackgroundManager();
            }
        }
        return sInstance;
    }


    private BackgroundManager() {
        mSingleExecutor = Executors.newSingleThreadExecutor();
        mExecutor = Executors.newFixedThreadPool(EXECUTOR_THREAD_COUNT);
        mTaskScheduler = Executors.newCachedThreadPool();
        mScheduledExecutorService = Executors.newScheduledThreadPool(1);
    }


    /**
     * This method runs a instance of Runnable in single threaded queue in background.
     */
    public synchronized void runInBackground(Runnable r) {
        mExecutor.execute(r);
    }

    /**
     * This method runs a instance of Runnable in single threaded queue in background.
     */
    public synchronized void runInBackground(final Runnable r, long delay) {
        mScheduledExecutorService.schedule(r, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * Executes the code in background thread of Single threaded queue.
     * @param r runnable to be run in background thread.
     */
    public synchronized void runInSingleThreadedQueue(Runnable r) {
        mSingleExecutor.submit(r);
    }


    /**
     * This method runs a instance of Callable in single threaded queue in background.
     */
    public synchronized <T> Future<T> runInSingleThreadedQueue(Callable<T> callable) {
        return mSingleExecutor.submit(callable);
    }

    /**
     * Post any data for using the provided listener on the main thread
     * @param runnable runnable witch need to be fired on main thread
     * @param delay milliseconds which need to be delayed
     */
    public synchronized void postDelayed(Runnable runnable, long delay) {
        mHandler.postDelayed(runnable, delay);
    }


    /**
     * This method runs a instance of Runnable in main thread.
     */
    public synchronized void runOnMainThread(Runnable r) {
        mHandler.post(r);
    }

    /**
     * Method will use to schedule a task for provided interval and
     * on completion of interval callback will be fired with the provided listener and data
     * @param listener Listener which fire on completion of interval
     * @param data Data for he register listener
     * @param delay waiting time
     * @param <T> Type of data
     * @return Future for cancellation of task
     */
    public synchronized <T> Future schedule(@NonNull final OnDataListener<T> listener, final T data, final long delay) {
        LOGGER.info("Submitted a task with delay %s", delay);
        return mTaskScheduler.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay);
                    listener.onResult(data);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * Method will use to schedule a task for provided interval and on completion
     * of interval callback will be fired with the provided listener and data on main thread
     * @param listener Listener which fire on completion of interval
     * @param data Data for he register listener
     * @param delay waiting time
     * @param <T> Type of data
     * @return Future for cancellation of task
     */
    public synchronized <T> Future scheduleOnMainThread(@NonNull final OnDataListener<T> listener, final T data, final long delay) {
        LOGGER.info("Submitted a task with delay %s", delay);
        return mTaskScheduler.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay);
                    postOnMainThread(listener, data);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Post any data for using the provided listener on the main thread
     * @param listener listener which need to be fired
     * @param data Data which need to be pass
     * @param <T> Type of data
     */
    public synchronized <T> void postOnMainThread(final OnDataListener<T> listener, final T data) {
        if (null != listener) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onResult(data);
                }
            });
        }
    }

    public synchronized Handler getHandler() {
        return mHandler;
    }

    public synchronized void terminateAll() {
        try {
            if (null != mSingleExecutor && (!mSingleExecutor.isShutdown() || !mSingleExecutor.isTerminated())) {
                mSingleExecutor.shutdownNow();
            }

            if (null != mExecutor && (!mExecutor.isShutdown() || !mExecutor.isTerminated())) {
                mExecutor.shutdownNow();
            }
            if (null != mTaskScheduler && (!mTaskScheduler.isShutdown() || !mTaskScheduler.isTerminated())) {
                mTaskScheduler.shutdownNow();
            }
            if (null != mHandler) {
                mHandler.removeCallbacksAndMessages(null);
            }
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage());
        }

    }
}
