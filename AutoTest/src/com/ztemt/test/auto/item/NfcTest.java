package com.ztemt.test.auto.item;

import java.lang.reflect.Method;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.util.Log;

import com.ztemt.test.auto.R;

public class NfcTest extends BaseTest {

    private static final String LOG_TAG = "NfcTest";

    private NfcAdapter mNfcAdapter;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @TargetApi(18)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (NfcAdapter.ACTION_ADAPTER_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(NfcAdapter.EXTRA_ADAPTER_STATE,
                        NfcAdapter.STATE_OFF);
                if (state == NfcAdapter.STATE_ON) {
                    setSuccess();
                    resume();
                }
            }
        }
    };

    public NfcTest(Context context) {
        super(context);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(context);
    }

    @TargetApi(18)
    @Override
    public void onRun() {
        if (mNfcAdapter == null) {
            Log.e(LOG_TAG, "NfcAdapter is null");
            setFailure();
        } else {
            if (mNfcAdapter.isEnabled()) {
                disable();
            }

            sleep(2000);
            mContext.registerReceiver(mReceiver, new IntentFilter(
                    NfcAdapter.ACTION_ADAPTER_STATE_CHANGED));
            setTimeout(10000);
            enable();

            // Pause the thread, resume after nfc turn on
            pause();

            mContext.unregisterReceiver(mReceiver);
        }
    }

    @Override
    public String getTitle() {
        return mContext.getString(R.string.nfc_test);
    }

    private void enable() {
        try {
            Method m = NfcAdapter.class.getMethod("enable");
            m.invoke(mNfcAdapter);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    private void disable() {
        try {
            Method m = NfcAdapter.class.getMethod("disable");
            m.invoke(mNfcAdapter);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }
}
