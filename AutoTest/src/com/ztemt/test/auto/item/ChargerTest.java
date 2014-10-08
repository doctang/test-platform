package com.ztemt.test.auto.item;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import com.ztemt.test.auto.R;

public class ChargerTest extends BaseTest {

    private static final String LOG_TAG = "ChargerTest";

    public ChargerTest(Context context) {
        super(context);
    }

    @Override
    public void onRun() {
        sleep(2000);
        Intent intent = mContext.registerReceiver(null, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));
        if (intent != null) {
            int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
            Log.d(LOG_TAG, "Battery plugged type = " + plugged);
            switch (plugged) {
            case BatteryManager.BATTERY_PLUGGED_AC:
            case BatteryManager.BATTERY_PLUGGED_USB:
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                setSuccess();
                break;
            default:
                setFailure();
                break;
            }
        } else {
            setFailure();
        }
    }

    @Override
    public String getTitle() {
        return mContext.getString(R.string.charger_test);
    }
}
