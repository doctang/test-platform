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
import android.os.IBinder;

public class PackageService extends Service {

    public static final String EXTRA_COMMAND = "command";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String command = intent.getStringExtra(EXTRA_COMMAND);
            if ("getLauncherList".equals(command)) {
                getLauncherList();
            }
        }
        return START_NOT_STICKY;
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
        file.setReadable(true, false);
        write(jobj.toString(), file);
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
