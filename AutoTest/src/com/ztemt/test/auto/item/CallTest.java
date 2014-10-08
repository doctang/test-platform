package com.ztemt.test.auto.item;

import java.lang.reflect.Method;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.internal.telephony.ITelephony;
import com.ztemt.test.auto.AutoTestActivity;
import com.ztemt.test.auto.R;
import com.ztemt.test.auto.util.PreferenceUtils;

public class CallTest extends BaseTest {

    private static final String LOG_TAG = "CallTest";
    private static final String ACTION_SLEEP_TIMEOUT = "com.ztemt.test.auto.action.SLEEP_TIMEOUT";

    private EditText mOutgoingEdit;
    private EditText mIntervalEdit;

    @SuppressWarnings("deprecation")
    private KeyguardManager.KeyguardLock mKeyguardLock;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    private TelephonyManager mTelephonyManager;
    private ITelephony mTelephony;
    private PreferenceUtils mPrefUtils;

    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                Log.d(LOG_TAG, "IDLE");
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                Log.d(LOG_TAG, "OFFHOOK");
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                Log.d(LOG_TAG, "RINGING incomingNumber : " + incomingNumber);
                break;
            }
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_NEW_OUTGOING_CALL.equals(action)) {
                setSuccess();
                resume();
            } else if (ACTION_SLEEP_TIMEOUT.equals(action)) {
                acquireWakeLock(5000);
                enableKeyguard(false);
                sleep(2000);
                releaseWakeLock();
            } else {
                mTelephonyManager.listen(mPhoneStateListener,
                        PhoneStateListener.LISTEN_CALL_STATE);
            }
        }
    };

    public CallTest(Context context) {
        super(context);
        mPrefUtils = new PreferenceUtils(context);
        mTelephony = getTelephony();
        mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    }

    @Override
    public void onRun() {
        if (mTelephonyManager.getSimState() != TelephonyManager.SIM_STATE_READY) {
            sleep(2000);
            setFailure();
        } else {
            registerReceiver();
            setTimeout(350000);
            dial();
            pause();
            sleep(10000);
            endCall();
            goToSleep();
            sleep(getInterval());
            unregisterReceiver();
            wakeUp(0);
            backToTest();
        }
    }

    @Override
    public String getTitle() {
        return mContext.getString(R.string.call_test);
    }

    @Override
    public View createPreferenceView() {
        View view = super.createPreferenceView();
        mOutgoingEdit = addPreferenceEdit(view, R.string.outgoing_number_label, getOutgoingNumber());
        mIntervalEdit = addPreferenceEdit(view, R.string.test_interval_label, String.valueOf(getInterval()));
        return view;
    }

    @Override
    public void onPreferenceClick(View view) {
        super.onPreferenceClick(view);
        try {
            int interval = Integer.parseInt(mIntervalEdit.getText().toString());
            setInterval(interval);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        setOutgoingNumber(mOutgoingEdit.getText().toString());
    }

    @Override
    public void setExtras(Bundle bundle) {
        super.setExtras(bundle);
        if (bundle != null && bundle.containsKey("outgoing")) {
            setOutgoingNumber(bundle.getString("outgoing", ""));
        }
    }

    private ITelephony getTelephony() {
        IBinder binder = null;
        try {
            Class<?> c = Class.forName("android.os.ServiceManager");
            Method m = c.getMethod("getService", String.class);
            binder = (IBinder) m.invoke(c, Context.TELEPHONY_SERVICE);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            return null;
        }
        return ITelephony.Stub.asInterface(binder);
    }

    private void dial() {
        String phoneNumber = getOutgoingNumber();
        String operator = mTelephonyManager.getSimOperator();
        if (TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(operator)) {
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

        Intent call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        mContext.startActivity(call);
    }

    private void endCall() {
        try {
            mTelephony.endCall();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private String getOutgoingNumber() {
        return mPrefUtils.getString("outgoing", "");
    }

    private void setOutgoingNumber(String outgoingNumber) {
        mPrefUtils.putString("outgoing", outgoingNumber);
    }

    private int getInterval() {
        return mPrefUtils.getInt("interval", 30000);
    }

    private void setInterval(int interval) {
        mPrefUtils.putInt("interval", interval);
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        filter.addAction(ACTION_SLEEP_TIMEOUT);
        mContext.registerReceiver(mReceiver, filter);
    }

    private void unregisterReceiver() {
        try {
            mContext.unregisterReceiver(mReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void goToSleep() {
        enableKeyguard(true);
        mPowerManager.goToSleep(SystemClock.uptimeMillis());
    }

    private void wakeUp(long milliseconds) {
        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,
                new Intent(ACTION_SLEEP_TIMEOUT),
                PendingIntent.FLAG_CANCEL_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + milliseconds,
                pendingIntent);
    }

    @SuppressWarnings("deprecation")
    private void enableKeyguard(boolean enabled) {
        if (mKeyguardLock == null) {
            KeyguardManager km = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
            mKeyguardLock = km.newKeyguardLock(LOG_TAG);
        }
        if (enabled) {
            mKeyguardLock.reenableKeyguard();
        } else {
            mKeyguardLock.disableKeyguard();
        }
    }

    @SuppressWarnings("deprecation")
    private void acquireWakeLock(long milliseconds) {
        if (mWakeLock == null) {
            mWakeLock = mPowerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP
                    | PowerManager.ON_AFTER_RELEASE, LOG_TAG);
            mWakeLock.acquire(milliseconds);
        }
    }

    private void releaseWakeLock() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    private void backToTest() {
        Intent intent = new Intent(mContext, AutoTestActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
        //Instrumentation inst = new Instrumentation();
        //inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
        //inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
    }
}
