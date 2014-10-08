package com.ztemt.test.platform;

import android.app.IntentService;
import android.app.KeyguardManager;
import android.content.Intent;
import android.os.PowerManager;

import com.ztemt.test.platform.data.TextData;
import com.ztemt.test.platform.model.Message;
import com.ztemt.test.platform.model.SystemTest;
import com.ztemt.test.platform.model.SystemUpdate;
import com.ztemt.test.platform.model.Task;

public class TaskService extends IntentService {

    @SuppressWarnings("deprecation")
    private KeyguardManager.KeyguardLock mKeyguardLock;
    private PowerManager.WakeLock mWakeLock;

    public TaskService() {
        super("task");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        acquireWakeLock();
        enableKeyguard(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseWakeLock();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        TaskHandler handler;

        // Add new task to queue
        Message message = intent.getParcelableExtra(MessageService.EXTRA_MESG);
        for (TextData data : message.getArrayExtra("tasks")) {
            switch (data.getInt(Task.TYPE)) {
            case Task.TYPE_SYSTEM_UPDATE:
                SystemUpdate systemUpdate = new SystemUpdate(data);
                systemUpdate.setTimestamp(message.getTimestamp());
                if (!TaskList.get().contains(systemUpdate)) {
                    TaskList.get().add(systemUpdate);
                    TaskList.sync();
                }
                break;
            case Task.TYPE_SYSTEM_TEST:
                SystemTest systemTest = new SystemTest(data);
                systemTest.setTimestamp(message.getTimestamp());
                handler = new SystemTestHandler(this, systemTest);
                handler.prepare();
                if (!TaskList.get().contains(systemTest)) {
                    TaskList.get().add(systemTest);
                    TaskList.sync();
                }
                break;
            }
        }

        // Handle task from queue
        for (Task task : TaskList.get()) {
            int status = task.getStatus();
            if (status == Task.STATUS_NOT_START || status == Task.STATUS_WAIT
                    || status == Task.STATUS_GOING) {
                switch (task.getType()) {
                case Task.TYPE_SYSTEM_UPDATE:
                    handler = new SystemUpdateHandler(this, (SystemUpdate) task);
                    handler.execute();
                    break;
                case Task.TYPE_SYSTEM_TEST:
                    handler = new SystemTestHandler(this, (SystemTest) task);
                    handler.execute();
                    break;
                case Task.TYPE_SYSTEM_CLEAR:
                    break;
                }
            }
        }

        // Stop self
        stopSelf();
    }

    @SuppressWarnings("deprecation")
    private void enableKeyguard(boolean enabled) {
        if (mKeyguardLock == null) {
            KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            mKeyguardLock = km.newKeyguardLock("keyguard");
        }
        if (enabled) {
            mKeyguardLock.reenableKeyguard();
        } else {
            mKeyguardLock.disableKeyguard();
        }
    }

    @SuppressWarnings("deprecation")
    private void acquireWakeLock() {
        if (mWakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP
                    | PowerManager.ON_AFTER_RELEASE, "wakelock");
            mWakeLock.acquire();
        }
    }

    private void releaseWakeLock() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }
}
