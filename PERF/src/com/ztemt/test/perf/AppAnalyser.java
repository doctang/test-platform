package com.ztemt.test.perf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Environment;
import android.text.TextUtils;

/**
 * 获取应用启动时间
 * @author 0016001973
 *
 */
public class AppAnalyser extends PerfTest {

    public void test() throws IOException, JSONException {
        // 打印表头
        System.err.println(String.format("%-12s%-12s%-12s%-12s%-12s%-12s%-12s%-12s%-12s%-12s%-12s",
                "App", "size(byte)", "1st(ms)", "2nd(ms)", "3rd(ms)", "4th(ms)",
                "5th(ms)", "6th(ms)", "min(ms)", "max(ms)", "avg.(ms)"));

        // 遍历系统应用
        Process p = Runtime.getRuntime().exec("pm list package -s");
        InputStreamReader in = new InputStreamReader(p.getInputStream());
        BufferedReader br = new BufferedReader(in);
        String line = null;
        while ((line = br.readLine()) != null) {
            String packageName = line.substring(8);

            // 获取启动器列表
            Runtime.getRuntime().exec("am startservice -n com.ztemt.test.common/.PackageService --es package "
                    + packageName);
            sleep(1500);

            // 读取启动器列表
            File file = new File(Environment.getExternalStorageDirectory(),
                    "launcher.info");
            JSONArray array = new JSONArray(readLine(file));

            // 打印表内容
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                long[] time = getLaunchTime(packageName, obj.optString("activity"));
                int padding = 12 - Utils.getHanziCount(obj.optString("title"));
                System.err.println(String.format("%-" + padding
                        + "s%-12s%-12s%-12s%-12s%-12s%-12s%-12s%-12s%-12s%-12s",
                        obj.optString("title"), getPackageSize(packageName),
                        time[0], time[1], time[2], time[3], time[4], time[5],
                        time[6], time[7], time[8]));
            }
        }
    }

    private int startActivity(String packageName, String activityName, boolean clearTask) {
        String prog = String.format(
                "am start --user 0 -W %s -a android.intent.action.MAIN "
                + "-c android.intent.category.LAUNCHER -n %s/%s",
                clearTask ? "--activity-clear-task" : "", packageName,
                activityName);
        try {
            Process p = Runtime.getRuntime().exec(prog);
            InputStreamReader in = new InputStreamReader(p.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("ThisTime:")) {
                    return Integer.parseInt(line.substring(10));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void forceStop(String packageName) {
        String prog = String.format("am force-stop %s", packageName);
        try {
            Runtime.getRuntime().exec(prog);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long getPackageSize(String packageName) {
        String prog = String.format("pm path %s", packageName);
        try {
            Process p = Runtime.getRuntime().exec(prog);
            InputStreamReader in = new InputStreamReader(p.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String line = br.readLine();
            if (!TextUtils.isEmpty(line)) {
                String path = line.split(":")[1];
                File file = new File(path);
                return file.length();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private long[] getLaunchTime(String packageName, String activityName) {
        forceStop(packageName);

        getUiDevice().pressHome();
        int time1 = startActivity(packageName, activityName, false);

        getUiDevice().pressHome();
        int time2 = startActivity(packageName, activityName, true);

        getUiDevice().pressHome();
        int time3 = startActivity(packageName, activityName, true);

        getUiDevice().pressHome();
        int time4 = startActivity(packageName, activityName, true);

        getUiDevice().pressHome();
        int time5 = startActivity(packageName, activityName, true);

        getUiDevice().pressHome();
        int time6 = startActivity(packageName, activityName, true);

        forceStop(packageName);

        return new long[] { time1, time2, time3, time4, time5, time6,
                Math.min(Math.min(Math.min(time2, time3), Math.min(time4, time5)), time6),
                Math.max(Math.max(Math.max(time2, time3), Math.max(time4, time5)), time6),
                (time2 + time3 + time4 + time5 + time6) / 5 };
    }

    private static String readLine(InputStream is) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = "";

        try {
            line = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    private static String readLine(File file) {
        String line = "";

        try {
            line = readLine(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return line;
    }
}
