package com.ztemt.test.auto.item;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.MSimTelephonyManager;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ztemt.test.auto.R;

public class NetworkTest3 extends BaseTest {

    private static final String LOG_TAG = "NetworkTest3";

    private MSimTelephonyManager mMSTM;
    private TelephonyManager mTM;

    private boolean mStateInService1;
    private boolean mStateInService2;

    private long mMaxTime;
    private long mPreTime;

    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {

        @Override
        public void onServiceStateChanged(ServiceState serviceState) {
            int state = serviceState.getState();

            if (state == ServiceState.STATE_IN_SERVICE) {
                mTM.listen(this, LISTEN_NONE);
                setSuccess();
                resume();
            }
        }
    };

    private PhoneStateListener mPhoneStateListener1 = new PhoneStateListener(0) {

        @Override
        public void onServiceStateChanged(ServiceState serviceState) {
            int state = serviceState.getState();

            if (state == ServiceState.STATE_IN_SERVICE) {
                mMSTM.listen(this, LISTEN_NONE);
                mStateInService1 = true;
                multiSuccessResume();
            }
        }
    };

    private PhoneStateListener mPhoneStateListener2 = new PhoneStateListener(1) {

        @Override
        public void onServiceStateChanged(ServiceState serviceState) {
            int state = serviceState.getState();

            if (state == ServiceState.STATE_IN_SERVICE) {
                mMSTM.listen(this, LISTEN_NONE);
                mStateInService2 = true;
                multiSuccessResume();
            }
        }
    };

    public NetworkTest3(Context context) {
        super(context);
        mMSTM = (MSimTelephonyManager) mContext.getSystemService("phone_msim");
        mTM = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    @Override
    public void onRun() {
        if (!isAirplaneModeOn()) {
            setAirplaneModeOn(true);
        }
        sleep(30000);
        setAirplaneModeOn(false);
        setTimeout(600000);
        mPreTime = SystemClock.elapsedRealtime();
        if (mMSTM.isMultiSimEnabled()) {
            mStateInService1 = false;
            mStateInService2 = false;
            mMSTM.listen(mPhoneStateListener1, PhoneStateListener.LISTEN_SERVICE_STATE);
            mMSTM.listen(mPhoneStateListener2, PhoneStateListener.LISTEN_SERVICE_STATE);
            pause();
            mMSTM.listen(mPhoneStateListener1, PhoneStateListener.LISTEN_NONE);
            mMSTM.listen(mPhoneStateListener2, PhoneStateListener.LISTEN_NONE);
        } else {
            mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_SERVICE_STATE);
            pause();
            mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        if (getTestTimes() >= getTotalTimes()) {
            Log.d(LOG_TAG, "The longest time of search network is " + mMaxTime + "ms");
        }
    }

    @Override
    public String getTitle() {
        return mContext.getString(R.string.network_test3);
    }

    @Override
    public void setSuccess() {
        super.setSuccess();
        long cost = SystemClock.elapsedRealtime() - mPreTime;
        Log.d(LOG_TAG, "search network spent " + cost + "ms");
        mMaxTime = Math.max(cost, mMaxTime);
    }

    @Override
    public void setFailure() {
        super.setFailure();
        Log.e(LOG_TAG, "network state isn't in service");
        sleep(10000);
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setAirplaneModeOn(boolean enabling) {
        int mode = enabling ? 1 : 0;
        if (Build.VERSION.SDK_INT < 17) {
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, mode);
        } else {
            Settings.Global.putInt(mContext.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, mode);
        }
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", enabling);
        mContext.sendBroadcast(intent);
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private boolean isAirplaneModeOn() {
        int mode;
        if (Build.VERSION.SDK_INT < 17) {
            mode = Settings.System.getInt(mContext.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0);
        } else {
            mode = Settings.Global.getInt(mContext.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0);
        }
        return mode == 1;
    }

    private void multiSuccessResume() {
        if (mStateInService1 && mStateInService2) {
            setSuccess();
            resume();
        }
    }
}
