package com.ztemt.test.auto.item;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;

import com.ztemt.test.auto.R;

public class AirplaneModeTest extends BaseTest {

    private TelephonyManager mTelephonyManager;

    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {

        @Override
        public void onServiceStateChanged(ServiceState serviceState) {
            int state = serviceState.getState();

            if (state == ServiceState.STATE_POWER_OFF) {
                setSuccess();
                resume();
            }
        }
    };

    public AirplaneModeTest(Context context) {
        super(context);
        mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    @Override
    public void onRun() {
        if (isAirplaneModeOn()) {
            setAirplaneModeOn(false);
        }

        sleep(10000);
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_SERVICE_STATE);
        setTimeout(100000);
        setAirplaneModeOn(true);

        // Pause the thread, resume after airplane mode turn on
        pause();

        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        setAirplaneModeOn(false);
    }

    @Override
    public String getTitle() {
        return mContext.getString(R.string.airplane_mode_test);
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
}
