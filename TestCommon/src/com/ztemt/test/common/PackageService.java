package com.ztemt.test.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Environment;
import android.os.IBinder;

public class PackageService extends Service {

    public static final String EXTRA_PACKAGE = "package";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            getLauncherList(intent.getStringExtra(EXTRA_PACKAGE));
        }
        return START_NOT_STICKY;
    }

    private void getLauncherList(String packageName) {
        Intent query = new Intent(Intent.ACTION_MAIN).addCategory(
                Intent.CATEGORY_LAUNCHER).setPackage(packageName);
        List<ResolveInfo> activities = getPackageManager().queryIntentActivities(
                query, 0);
        JSONArray array = new JSONArray();

        for (ResolveInfo info : activities) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("title", info.loadLabel(getPackageManager()).toString());
                obj.put("activity", info.activityInfo.name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(obj);
        }

        // Save json array to external storage
        File file = new File(Environment.getExternalStorageDirectory(),
                "launcher.info");
        write(array.toString(), file);
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
}
