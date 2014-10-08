package com.ztemt.test.auto.item;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.ztemt.test.auto.R;

public class SmsTest extends BaseTest {

    private static final String LOG_TAG = "SmsTest";
    private static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";

    private TelephonyManager mTelephonyManager;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setSuccess();
            resume();
        }
    };

    public SmsTest(Context context) {
        super(context);
        mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    @Override
    public void onRun() {
        if (mTelephonyManager.getSimState() != TelephonyManager.SIM_STATE_READY) {
            Log.e(LOG_TAG, "SIM not ready.");
            sleep(2000);
            setFailure();
        } else {
            registerReceiver();
            setTimeout(60000);
            sendMessage();
            pause();
            unregisterReceiver();
        }
    }

    @Override
    public String getTitle() {
        return mContext.getString(R.string.sms_test);
    }

    private void sendMessage() {
        String operator = mTelephonyManager.getSimOperator();
        String phoneNumber = "10086";
        String textBody = "101";

        if (!TextUtils.isEmpty(operator)) {
            if (operator.equals("46000") || operator.equals("46002")) {
                // China Mobile
                phoneNumber = "10086";
            } else if (operator.equals("46001")) {
                // China Unicom
                phoneNumber = "10010";
            } else if (operator.equals("46003")) {
                // China Telecom
                phoneNumber = "10000";
            }
        }

        if (!TextUtils.isEmpty(phoneNumber)) {
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0,
                    new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
            SmsManager.getDefault().sendTextMessage(phoneNumber, null, textBody,
                    pendingIntent, null);
        }
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(SMS_RECEIVED_ACTION);
        mContext.registerReceiver(mReceiver, filter);
    }

    private void unregisterReceiver() {
        try {
            mContext.unregisterReceiver(mReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
