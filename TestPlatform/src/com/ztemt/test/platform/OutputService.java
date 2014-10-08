package com.ztemt.test.platform;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ztemt.test.platform.util.DeviceUtils;
import com.ztemt.test.platform.util.FileUtils;
import com.ztemt.test.platform.util.HttpPost;
import com.ztemt.test.platform.util.HttpPost.ProgressListener;

public class OutputService extends IntentService implements ProgressListener {

    private static final String TAG = "OutputService";

    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNM;

    public OutputService() {
        super("output");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(
                CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI
                && info.getState() == NetworkInfo.State.CONNECTED) {
            mBuilder = createProgressNotificationBuilder();
            mNM.notify(R.string.upload, mBuilder.build());
            for (File file : FileUtils.listFile(OutputManager.OUTPUT)) {
                if (upload(file)) {
                    file.delete();
                } else {
                    Log.w(TAG, "upload " + file.getName() + " failed");
                }
            }
            mNM.cancel(R.string.upload);
        } else {
            Log.d(TAG, "wifi is not active");
        }

        stopSelf();
    }

    @Override
    public void onProgressUpdate(int progress) {
        mBuilder.setProgress(100, progress, false);
        mNM.notify(R.string.upload, mBuilder.build());
    }

    private boolean upload(File file) {
        String task = file.getName().split("-")[0];
        Map<String, String> params = new HashMap<String, String>();
        params.put("address", DeviceUtils.getWifiMacAddress(this));
        params.put("model", Build.MODEL);
        params.put("display", Build.DISPLAY);
        params.put("buildDate", DeviceUtils.getBuildDate2());
        params.put("task", task);

        String result = "failure";

        try {
            result = HttpPost.post(getString(R.string.upload_url), params, file, this);
        } catch (IOException e) {
            Log.e(TAG, "upload", e);
        }

        return "success".equals(result);
    }

    private NotificationCompat.Builder createProgressNotificationBuilder() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle(getString(R.string.file_upload_title));
        builder.setContentText(getString(R.string.file_upload_text));
        builder.setAutoCancel(false);
        builder.setOnlyAlertOnce(true);
        builder.setOngoing(true);
        builder.setDefaults(Notification.DEFAULT_SOUND);
        return builder;
    }
}
