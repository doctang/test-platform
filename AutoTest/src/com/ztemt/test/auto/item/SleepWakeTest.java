package com.ztemt.test.auto.item;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.os.SystemClock;

import com.ztemt.test.auto.R;

public class SleepWakeTest extends BaseTest {

    private static final String LOG_TAG = "SleepWakeTest";
    private static final String ACTION_SLEEP_TIMEOUT = "com.ztemt.test.auto.action.SLEEP_TIMEOUT";

    @SuppressWarnings("deprecation")
    private KeyguardManager.KeyguardLock mKeyguardLock;
    private PowerManager.WakeLock mWakeLock;
    private PowerManager mPowerManager;

    public SleepWakeTest(Context context) {
        super(context);
        mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            acquireWakeLock(5000);
            enableKeyguard(false);
            if (mPowerManager.isScreenOn()) {
                setSuccess();
            } else {
                setFailure();
            }
            sleep(5000);
            releaseWakeLock();
            resume();
        }
    };

    @Override
    public void onRun() {
        registerReceiver();
        goToSleep();
        wakeUp(5000);
        pause();
        unregisterReceiver();
    }

    @Override
    public String getTitle() {
        return mContext.getString(R.string.sleep_wake_test);
    }

    @SuppressWarnings("deprecation")
    private void enableKeyguard(boolean enabled) {
        if (mKeyguardLock == null) {
            KeyguardManager km = (KeyguardManager) mContext.getSystemService(
                    Context.KEYGUARD_SERVICE);
            mKeyguardLock = km.newKeyguardLock(LOG_TAG);
        }
        if (enabled) {
            mKeyguardLock.reenableKeyguard();
        } else {
            mKeyguardLock.disableKeyguard();
        }
    }

    @SuppressWarnings("deprecation")
    private void acquireWakeLock(long milliseconds) {
        if (mWakeLock == null) {
            mWakeLock = mPowerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP
                    | PowerManager.ON_AFTER_RELEASE, LOG_TAG);
            mWakeLock.acquire(milliseconds);
        }
    }

    private void releaseWakeLock() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    private void goToSleep() {
        enableKeyguard(true);
        mPowerManager.goToSleep(SystemClock.uptimeMillis());
    }

    private void wakeUp(long milliseconds) {
        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,
                new Intent(ACTION_SLEEP_TIMEOUT), PendingIntent
                .FLAG_CANCEL_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + milliseconds,
                pendingIntent);
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(ACTION_SLEEP_TIMEOUT);
        mContext.registerReceiver(mReceiver, filter);
    }

    private void unregisterReceiver() {
        mContext.unregisterReceiver(mReceiver);
    }
}
