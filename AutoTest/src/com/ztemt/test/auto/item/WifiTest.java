package com.ztemt.test.auto.item;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;

import com.ztemt.test.auto.R;

public class WifiTest extends BaseTest {

    //private static final String LOG_TAG = "WifiTest";

    private WifiManager mWifiManager;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                        WifiManager.WIFI_STATE_UNKNOWN);
                if (state == WifiManager.WIFI_STATE_ENABLED) {
                    setSuccess();
                    resume();
                }
            }
        }
    };

    public WifiTest(Context context) {
        super(context);
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    public void onRun() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
            sleep(5000);
        }

        sleep(2000);
        mContext.registerReceiver(mReceiver, new IntentFilter(
                WifiManager.WIFI_STATE_CHANGED_ACTION));
        setTimeout(30000);
        mWifiManager.setWifiEnabled(true);

        // Pause the thread, resume after Wi-Fi turn on
        pause();

        mContext.unregisterReceiver(mReceiver);
        mWifiManager.setWifiEnabled(false);
        sleep(10000);
    }

    @Override
    public String getTitle() {
        return mContext.getString(R.string.wifi_test);
    }
}
