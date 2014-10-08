package com.ztemt.test.platform.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.util.Log;

public class DeviceUtils {

    private static final String TAG = "DeviceUtils";
    private static final String PROC_VERSION = "/proc/version";

    public static String getBuildDate() {
        String buildDate = SystemProperties.get("ro.build.date");
        SimpleDateFormat f = new SimpleDateFormat("yyyy年 MM月 dd日 E HH:mm:ss", Locale.CHINA);
        Date date = new Date();
        try {
            date = f.parse(buildDate);
        } catch (java.text.ParseException e) {
            Log.e(TAG, "Error parse build date", e);
        }
        f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        return f.format(date);
    }

    public static String getBuildDate2() {
        return getBuildDate().replaceAll("[-: ]", "");
    }

    public static String getDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    public static String getWifiMacAddress(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wm.getConnectionInfo();
        String mac = info != null ? info.getMacAddress() : "";
        return mac;
    }

    public static String getWifiIpAddress(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wm.getConnectionInfo();
        int ip = info.getIpAddress();
        if (ip != 0) {
            return (ip & 0xff) + "." + (ip >> 8 & 0xff) + "." + (ip >> 16 &0xff) + "." + (ip >> 24 & 0xff);
        } else {
            return null;
        }
    }

    public static String getFormattedKernelVersion() {
        try {
            return formatKernelVersion(readLine(PROC_VERSION));
        } catch (IOException e) {
            return "Unavailable";
        }
    }

    private static String formatKernelVersion(String rawKernelVersion) {
        // Example (see tests for more):
        // Linux version 3.0.31-g6fb96c9 (android-build@xxx.xxx.xxx.xxx.com) \
        //     (gcc version 4.6.x-xxx 20120106 (prerelease) (GCC) ) #1 SMP PREEMPT \
        //     Thu Jun 28 11:02:39 PDT 2012

        final String PROC_VERSION_REGEX =
            "Linux version (\\S+) " + /* group 1: "3.0.31-g6fb96c9" */
            "\\((\\S+?)\\) " +        /* group 2: "x@y.com" (kernel builder) */
            "(?:\\(gcc.+? \\)) " +    /* ignore: GCC version information */
            "(#\\d+) " +              /* group 3: "#1" */
            "(?:.*?)?" +              /* ignore: optional SMP, PREEMPT, and any CONFIG_FLAGS */
            "((Sun|Mon|Tue|Wed|Thu|Fri|Sat).+)"; /* group 4: "Thu Jun 28 11:02:39 PDT 2012" */

        Matcher m = Pattern.compile(PROC_VERSION_REGEX).matcher(rawKernelVersion);
        if (!m.matches()) {
            Log.e(TAG, "Regex did not match on /proc/version: " + rawKernelVersion);
            return "Unavailable";
        } else if (m.groupCount() < 4) {
            Log.e(TAG, "Regex match on /proc/version only returned " + m.groupCount()
                    + " groups");
            return "Unavailable";
        }
        return m.group(1) + "\n" +                 // 3.0.31-g6fb96c9
            m.group(2) + " " + m.group(3) + "\n" + // x@y.com #1
            m.group(4);                            // Thu Jun 28 11:02:39 PDT 2012
    }

    private static String readLine(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName), 256);
        try {
            return reader.readLine();
        } finally {
            reader.close();
        }
    }
}
