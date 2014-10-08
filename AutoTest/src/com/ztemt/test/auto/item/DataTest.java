package com.ztemt.test.auto.item;

import java.io.IOException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.telephony.MSimTelephonyManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ztemt.test.auto.R;

public class DataTest extends BaseTest {

    private static final String LOG_TAG = "DataTest";

    private MSimTelephonyManager mMSTM;
    private TelephonyManager mTM;

    private ConnectivityManager mCM;
    private WifiManager mWM;

    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {

        @Override
        public void onDataConnectionStateChanged(int state, int networkType) {
            if (state == TelephonyManager.DATA_CONNECTED) {
                mTM.listen(mPhoneStateListener, LISTEN_NONE);
                setSuccess();
                resume();
            }
        }
    };

    private PhoneStateListener mPhoneStateListener1 = new PhoneStateListener(0) {

        @Override
        public void onDataConnectionStateChanged(int state, int networkType) {
            if (state == TelephonyManager.DATA_CONNECTED) {
                mMSTM.listen(mPhoneStateListener1, LISTEN_NONE);
                setSuccess();
                resume();
            }
        }
    };

    private PhoneStateListener mPhoneStateListener2 = new PhoneStateListener(1) {

        @Override
        public void onDataConnectionStateChanged(int state, int networkType) {
            if (state == TelephonyManager.DATA_CONNECTED) {
                mMSTM.listen(mPhoneStateListener2, LISTEN_NONE);
                setSuccess();
                resume();
            }
        }
    };

    public DataTest(Context context) {
        super(context);
        mMSTM = (MSimTelephonyManager) mContext.getSystemService("phone_msim");
        mTM = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        mCM = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        mWM = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    public void onRun() {
        if (mWM.isWifiEnabled()) {
            mWM.setWifiEnabled(false);
        }

        if (mMSTM.isMultiSimEnabled()) {
            startActiveData();
            setTimeout(120000);
            mMSTM.listen(mPhoneStateListener1, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
            mMSTM.listen(mPhoneStateListener2, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
            pause();
            mMSTM.listen(mPhoneStateListener1, PhoneStateListener.LISTEN_NONE);
            mMSTM.listen(mPhoneStateListener2, PhoneStateListener.LISTEN_NONE);
        } else {
            if (mCM.getMobileDataEnabled()) {
                mCM.setMobileDataEnabled(false);
                sleep(3000);
            }
            mCM.setMobileDataEnabled(true);
            setTimeout(120000);
            mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
            pause();
            mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    @Override
    public String getTitle() {
        return mContext.getString(R.string.data_test);
    }

    @Override
    public void setFailure() {
        super.setFailure();
        Log.e(LOG_TAG, "data state isn't connected");
        sleep(10000);
    }

    private void startActiveData() {
        try {
            Runtime.getRuntime().exec("am instrument -w -e class com.android.settings.test.SettingsTestCase com.android.settings.test/android.test.InstrumentationTestRunner").waitFor();
        } catch (InterruptedException e) {
            Log.e(LOG_TAG, "InterruptedException", e);
        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException", e);
        }
    }
}
