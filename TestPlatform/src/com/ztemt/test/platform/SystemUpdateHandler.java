package com.ztemt.test.platform;

import java.io.File;
import java.io.IOException;

import android.app.ActivityManager;
import android.content.Context;
import android.os.RecoverySystem;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Log;

import com.ztemt.test.platform.model.SystemUpdate;
import com.ztemt.test.platform.model.Task;
import com.ztemt.test.platform.util.DeviceUtils;
import com.ztemt.test.platform.util.HttpDownloader;
import com.ztemt.test.platform.util.HttpDownloader.HttpDownloadListener;

public class SystemUpdateHandler extends TaskHandler implements HttpDownloadListener {

    private static final String TAG = "SystemUpdateHandler";
    private static final String KEY_NEVER_UPDATE = "persist.sys.testplat.noupdate";

    private SystemUpdate mSystemUpdate;

    private HttpDownloader mDownloader;

    public SystemUpdateHandler(Context context, SystemUpdate systemUpdate) {
        super(context, systemUpdate);

        mSystemUpdate = systemUpdate;

        mDownloader = new HttpDownloader(context);
        mDownloader.setDownloadListener(this);
    }

    @Override
    public void onDownloadComplete(File file) {
        resume();

        if (file != null) {
            installPackage(file);
            pause();
        } else {
            setStatus(Task.STATUS_FAIL);
        }
    }

    public static boolean isNeverUpdate() {
        return SystemProperties.getBoolean(KEY_NEVER_UPDATE, false);
    }

    public static void setNeverUpdate(boolean neverUpdate) {
        SystemProperties.set(KEY_NEVER_UPDATE, String.valueOf(neverUpdate));
    }

    @Override
    protected void onBegin() {
        super.onBegin();
        mDownloader.register();
    }

    @Override
    protected void onRun() {
        String title = mContext.getString(R.string.system_update);
        updateStatusText(title);

        if (isSystemUpdated()) {
            setStatus(Task.STATUS_OK);
        } else if (isNeverUpdate() || waitNotificationCanceled(title, 180)) {
            setStatus(Task.STATUS_CANCEL);
        } else {
            setStatus(Task.STATUS_WAIT);
            while (ActivityManager.isUserAMonkey()) {
                SystemClock.sleep(5000);
            }

            setStatus(Task.STATUS_GOING);
            mDownloader.download(mSystemUpdate.getUpdateUrl(), null);
            pause();
        }
    }

    @Override
    protected void onEnd() {
        super.onEnd();
        mDownloader.unregister();
    }

    private boolean isSystemUpdated() {
        String buildDate = DeviceUtils.getBuildDate();
        return buildDate.compareTo(mSystemUpdate.getBuildDate()) >= 0;
    }

    private void installPackage(final File file) {
        try {
            RecoverySystem.installPackage(mContext, file);
        } catch (IOException e) {
            Log.e(TAG, "Error install package", e);
        }
    }
}
