package com.ztemt.test.auto.item;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ztemt.test.auto.R;

public class NetworkTest extends RebootTest {

    private static final String LOG_TAG = "NetworkTest";

    private TelephonyManager mTelephonyManager;

    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {

        @Override
        public void onServiceStateChanged(ServiceState serviceState) {
            int state = serviceState.getState();

            if (state == ServiceState.STATE_IN_SERVICE) {
                setSuccess();
                resume();
            } else {
                Log.d(LOG_TAG, "serviceState = " + state);
                setFailure();
                resume();
            }
        }
    };

    public NetworkTest(Context context) {
        super(context);
        mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    @Override
    public void onRun() {
        sleep(30000);
        registerListener();
        pause();
        unregisterListener();
        reboot();
    }

    @Override
    public String getTitle() {
        return mContext.getString(R.string.network_test);
    }

    private void registerListener() {
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_SERVICE_STATE);
    }

    private void unregisterListener() {
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
    }
}
