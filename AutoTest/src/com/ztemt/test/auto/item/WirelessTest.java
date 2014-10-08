package com.ztemt.test.auto.item;

import java.lang.reflect.Method;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.ztemt.test.auto.R;

public class WirelessTest extends BaseTest {

    private static final String LOG_TAG = "WirelessTest";

    private ConnectivityManager mCM;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                        WifiManager.WIFI_STATE_UNKNOWN);
                if (state == WifiManager.WIFI_STATE_ENABLED) {
                    setSuccess();
                    resume();
                }
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                if (state == BluetoothAdapter.STATE_ON) {
                    setSuccess();
                    resume();
                }
            }
        }
    };

    public WirelessTest(Context context) {
        super(context);
        mCM = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
    }

    @Override
    public void onRun() {
        switch ((int) (Math.random() * 10) % 3) {
        case 0:
            Log.d(LOG_TAG, "testWifi");
            testWifi();
            break;
        case 1:
            Log.d(LOG_TAG, "testBluetooth");
            testBluetooth();
            break;
        case 2:
            Log.d(LOG_TAG, "testMobileData");
            testMobileData();
            break;
        }
    }

    @Override
    public String getTitle() {
        return mContext.getString(R.string.wireless_test);
    }

    private void testWifi() {
        WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        if (wm.isWifiEnabled()) {
            wm.setWifiEnabled(false);
            sleep(5000);
        }

        sleep(2000);
        mContext.registerReceiver(mReceiver, new IntentFilter(
                WifiManager.WIFI_STATE_CHANGED_ACTION));
        setTimeout(30000);
        wm.setWifiEnabled(true);

        // Pause the thread, resume after Wi-Fi turn on
        pause();

        mContext.unregisterReceiver(mReceiver);
        wm.setWifiEnabled(false);
    }

    private void testBluetooth() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            Log.e(LOG_TAG, "BluetoothAdapter is null");
            setFailure();
        } else {
            if (adapter.isEnabled()) {
                adapter.disable();
            }

            sleep(2000);
            mContext.registerReceiver(mReceiver, new IntentFilter(
                    BluetoothAdapter.ACTION_STATE_CHANGED));
            setTimeout(10000);
            adapter.enable();

            // Pause the thread, resume after bluetooth turn on
            pause();

            mContext.unregisterReceiver(mReceiver);
            adapter.disable();
        }
    }

    private void testMobileData() {
        if (getMobileDataEnabled()) {
            setMobileDataEnabled(false);
            sleep(1000);
        }

        if (getMobileDataEnabled()) {
            Log.e(LOG_TAG, "Disable mobile data fail");
            setFailure();
        } else {
            setMobileDataEnabled(true);
            sleep(1000);

            if (getMobileDataEnabled()) {
                setSuccess();
            } else {
                Log.e(LOG_TAG, "Enable mobile data fail");
                setFailure();
            }
        }
    }

    private boolean getMobileDataEnabled() {
        try {
            Method m = ConnectivityManager.class.getMethod("getMobileDataEnabled");
            return (Boolean) m.invoke(mCM);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            return false;
        }
    }

    private void setMobileDataEnabled(boolean enabled) {
        try {
            Method m = ConnectivityManager.class.getMethod(
                    "setMobileDataEnabled", Boolean.TYPE);
            m.invoke(mCM, enabled);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }
}
