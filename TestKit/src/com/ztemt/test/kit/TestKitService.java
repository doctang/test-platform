package com.ztemt.test.kit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.text.TextUtils;

public class TestKitService extends Service {

    private static final String EXTRA_COMMAND = "command";

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String packageName = "";

            if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {
                packageName = intent.getData().getSchemeSpecificPart();
            }

            if (!TextUtils.isEmpty(packageName)) {
                File file = getFileStreamPath("package");
                write(packageName, file);

                getFilesDir().setReadable(true, false);
                file.setReadable(true, false);
            }
        }
    };

    @SuppressWarnings("deprecation")
    private KeyguardManager.KeyguardLock mKeyguardLock;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addDataScheme("package");

        registerReceiver(mReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String command = intent.getStringExtra(EXTRA_COMMAND);
            if ("getLauncherList".equals(command)) {
                getLauncherList();
            } else if ("disableKeyguard".equals(command)) {
                enableKeyguard(false);
            } else if ("enableKeyguard".equals(command)) {
                enableKeyguard(true);
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mReceiver);
    }

    private void getLauncherList() {
        Intent query = new Intent(Intent.ACTION_MAIN).addCategory(
                Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> activities = getPackageManager()
                .queryIntentActivities(query, 0);
        JSONObject jobj = new JSONObject();

        for (ResolveInfo info : activities) {
            String packageName = info.activityInfo.packageName;
            JSONObject obj = new JSONObject();
            try {
                obj.put("title", info.loadLabel(getPackageManager()).toString());
                obj.put("activity", info.activityInfo.name);
                if (jobj.has(packageName)) {
                    jobj.getJSONArray(packageName).put(obj);
                } else {
                    jobj.put(packageName, new JSONArray().put(obj));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Save json object to external storage
        File file = getFileStreamPath("launcher");
        write(jobj.toString(), file);

        getFilesDir().setReadable(true, false);
        file.setReadable(true, false);
    }

    private static void write(String line, File t) {
        BufferedWriter bw = null;
        try {
            FileWriter fw = new FileWriter(t, false);
            bw = new BufferedWriter(fw);
            bw.write(line);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
}
