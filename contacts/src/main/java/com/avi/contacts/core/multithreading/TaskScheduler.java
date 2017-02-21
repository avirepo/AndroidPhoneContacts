package com.avi.contacts.core.multithreading;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Class TaskScheduler created on 19/07/16 - 3:52 PM.
 * All copyrights reserved to the Zoomvy.
 * Class behaviour is to schedule a task and provide mechanism to
 * handle onStop, onFinish, onComplete for the runnable behaviour.
 * <p/>
 * The methods will define the behaviour if a task is schedule and user stop it will first call
 * onStop and the onFinish and if task is completed after provided delay it will first call
 * on Complete and then onFinish
 * <p/>
 */
public final class TaskScheduler {

    private long mStartTime;
    private long mDelay;
    private Timer mTaskExecutor;
    private final OnTaskStateListener mListener;

    public TaskScheduler(long delay, OnTaskStateListener listener) {
        mListener = listener;
        mStartTime = System.currentTimeMillis();
        mDelay = delay;
        Task task = new Task(mListener);
        mTaskExecutor = new Timer();
        mTaskExecutor.schedule(task, mDelay);
    }

    public static class OnTaskStateListener {
        /**
         * On Stop of task scheduler first method will be this one who get called
         */
        public void onStop() {
        }

        /**
         * On successfully time interval completion
         * method will be called
         */
        public void onComplete() {
        }

        /**
         * Method will be called when perform cancel action
         * on task scheduler and there is no more requirement for the result
         */
        public void onCancel() {

        }

        /**
         * Method will be always call in lifecycle end of
         * {@link TaskScheduler}
         */
        public void onFinish() {
        }
    }

    /**
     * To Stop the ongoing task delay
     * call this method
     */
    public final void stop() {
        if (System.currentTimeMillis() < (mStartTime + mDelay)) {
            mTaskExecutor.cancel();
            mListener.onStop();
            mListener.onFinish();
        }
        mTaskExecutor = null;
    }

    public final void cancel() {
        mTaskExecutor.cancel();
        mListener.onCancel();
    }

    private static class Task extends TimerTask {
        private final OnTaskStateListener mListener;

        private Task(@NonNull OnTaskStateListener listener) {
            mListener = listener;
        }

        @Override
        public void run() {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mListener.onComplete();
                    mListener.onFinish();
                }
            });

        }
    }


    /**
     * Need to be implement
     *
     * onTick method will call with tick interval as if the class object is construct using interval param then it
     // will call onTick after provided interval.
     * public void onTick(long interval) {
     *}
     */
}
