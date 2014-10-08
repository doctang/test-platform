package com.ztemt.test.perf;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import android.text.TextUtils;

/**
 * 获取应用启动时间
 * @author 0016001973
 *
 */
public class AppAnalyser extends PerfTest {

    public void test() {
        // 打印表头
        System.err.println(String.format("%-5s%-12s%-12s%-12s%-12s%-12s%-12s%-12s%-12s%-12s%-12s%-12s",
                "No.", "App", "size(byte)", "1st(ms)", "2nd(ms)", "3rd(ms)", "4th(ms)",
                "5th(ms)", "6th(ms)", "min(ms)", "max(ms)", "avg.(ms)"));

        // 打印表内容
        for (int i = 0; i < AppData.DATA.length; i++) {
            if (isPackageExists(AppData.DATA[i][1])) {
                long[] time = getLaunchTime(AppData.DATA[i][1], AppData.DATA[i][2]);
                int padding = 12 - Utils.getHanziCount(AppData.DATA[i][0]);
                System.err.println(String.format("%-5s%-" + padding
                        + "s%-12s%-12s%-12s%-12s%-12s%-12s%-12s%-12s%-12s%-12s",
                        i + 1, AppData.DATA[i][0], getPackageSize(AppData.DATA[i][1]),
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

    private boolean isPackageExists(String packageName) {
        String prog = String.format("pm list package %s", packageName);
        try {
            Process p = Runtime.getRuntime().exec(prog);
            InputStreamReader in = new InputStreamReader(p.getInputStream());
            BufferedReader br = new BufferedReader(in);
            return !TextUtils.isEmpty(br.readLine());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
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
}
