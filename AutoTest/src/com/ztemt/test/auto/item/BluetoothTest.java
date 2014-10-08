package com.ztemt.test.auto.item;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.ztemt.test.auto.R;

public class BluetoothTest extends BaseTest {

    private static final String LOG_TAG = "BluetoothTest";

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                if (state == BluetoothAdapter.STATE_ON) {
                    setSuccess();
                    resume();
                }
            }
        }
    };

    public BluetoothTest(Context context) {
        super(context);
    }

    @Override
    public void onRun() {
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

    @Override
    public String getTitle() {
        return mContext.getString(R.string.bluetooth_test);
    }
}
