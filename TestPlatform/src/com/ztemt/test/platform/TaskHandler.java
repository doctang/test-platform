package com.ztemt.test.platform;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.ztemt.test.platform.model.Task;

public abstract class TaskHandler {

    public static final String ACTION_STATUS_CHANGED = "com.ztemt.test.platform.action.STATUS_CHANGED";
    public static final String ACTION_NOTIFY_DISMISS = "com.ztemt.test.platform.action.NOTIFY_DISMISS";
    public static final String ACTION_GOON = "com.ztemt.test.platform.action.GOON";
    public static final String ACTION_SKIP = "com.ztemt.test.platform.action.SKIP";
    public static final String KEY_STATUS = "persist.sys.testplat.status";

    private Task mTask;

    private String mAction;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            mAction = intent.getAction();
        }
    };

    protected Context mContext;

    public TaskHandler(Context context, Task task) {
        mContext = context;
        mTask = task;
    }

    public void prepare() {
        // Nothing to do!
    }

    public void execute() {
        onBegin();
        onRun();
        onEnd();
    }

    protected void updateStatusText(String text) {
        SystemProperties.set(KEY_STATUS, text);
        Intent intent = new Intent(ACTION_STATUS_CHANGED);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    protected void setStatus(int status) {
        int index = TaskList.get().indexOf(mTask);
        if (index > -1) {
            Task task = TaskList.get().get(index);
            task.setStatus(status);
            TaskList.sync();
            updateStatus(task);
        }
    }

    protected void pause() {
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected void pause(long millis) {
        synchronized (this) {
            try {
                wait(millis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected void resume() {
        synchronized (this) {
            notify();
        }
    }

    protected void onBegin() {
        // Nothing to do!
    }

    protected abstract void onRun();

    protected void onEnd() {
        updateStatusText(mContext.getString(R.string.ready));
    }

    protected boolean waitNotificationCanceled(String title, int timeout) {
        IntentFilter filter = new IntentFilter(ACTION_GOON);
        filter.addAction(ACTION_SKIP);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mReceiver,
                filter);

        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra(TaskDetailFragment.TASK, mTask);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent p = PendingIntent.getActivity(mContext, 1, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder b = new NotificationCompat.Builder(mContext);
        b.setSmallIcon(R.drawable.ic_launcher);
        b.setContentTitle(title);
        b.setAutoCancel(false);
        b.setOngoing(true);
        b.setOnlyAlertOnce(true);
        b.setDefaults(Notification.DEFAULT_ALL);
        b.setContentIntent(p);

        NotificationManager nm = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        for (int i = timeout; i > 0 && mAction == null; i--) {
            b.setContentText(mContext.getString(R.string.timeout_skip, i));
            nm.notify(R.drawable.ic_launcher, b.build());
            SystemClock.sleep(1000);
        }
        nm.cancel(R.drawable.ic_launcher);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(
                new Intent(ACTION_NOTIFY_DISMISS).putExtra("action",
                        TextUtils.isEmpty(mAction) ? ACTION_GOON : mAction));

        // unregister receiver and return if skipped
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mReceiver);
        return ACTION_SKIP.equals(mAction);
    }

    private void updateStatus(Task task) {
        Intent intent = new Intent(mContext, TaskStatusService.class);
        intent.putExtra(TaskStatusService.EXTRA_TASK, task);
        mContext.startService(intent);
    }
}
