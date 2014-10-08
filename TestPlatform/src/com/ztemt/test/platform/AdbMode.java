package com.ztemt.test.platform;

import android.os.SystemProperties;
import android.util.Log;

public class AdbMode {

    private static final String TAG = "AdbMode";

    public static final String MODE_TCP = "tcp";
    public static final String MODE_USB = "usb";

    public void setAdbMode(String mode) {
        if (!getAdbMode().equals(mode)) {
            SystemProperties.set("persist.service.adbd", mode);
        } else {
            Log.v(TAG, "Already a " + mode + " mode");
        }
    }

    public String getAdbMode() {
        return SystemProperties.get("persist.service.adbd", MODE_USB);
    }
}
