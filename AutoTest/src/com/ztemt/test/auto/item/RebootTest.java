package com.ztemt.test.auto.item;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import com.ztemt.test.auto.R;

public class RebootTest extends BaseTest {

    private static final String LOG_TAG = "RebootTest";

    public RebootTest(Context context) {
        super(context);
    }

    @Override
    public void onRun() {
        setSuccess();
        reboot();
        pause();
    }

    @Override
    public String getTitle() {
        return mContext.getString(R.string.reboot_test);
    }

    public void reboot() {
        Log.d(LOG_TAG, "Performing reboot...");
        PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        powerManager.reboot(null);
    }
}
