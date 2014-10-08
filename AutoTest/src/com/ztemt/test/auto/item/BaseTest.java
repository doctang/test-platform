package com.ztemt.test.auto.item;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ztemt.test.auto.R;
import com.ztemt.test.auto.util.PreferenceUtils;

public abstract class BaseTest {

    private static final String LOG_TAG = "AutoTest";
    private static final String SUCCESS = "success";
    private static final String FAILURE = "failure";
    private static final String TIMES   = "times";
    private static final String ENABLED = "enabled";
    private static final String ORDINAL = "ordinal";

    public static final String ACTION_TIMEOUT = "com.ztemt.test.auto.action.TIMEOUT";

    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;
    private boolean mPause = false;

    private PreferenceUtils mPrefUtils;
    private String mSuccess;
    private String mFailure;
    private String mTimes;
    private String mEnabled;
    private String mOrdinal;

    protected Context mContext;

    public abstract void onRun();
    public abstract String getTitle();

    public BaseTest(Context context) {
        mContext = context;
        mPrefUtils = new PreferenceUtils(context);

        mSuccess = getPrefixName(SUCCESS);
        mFailure = getPrefixName(FAILURE);
        mTimes   = getPrefixName(TIMES);
        mEnabled = getPrefixName(ENABLED);
        mOrdinal = getPrefixName(ORDINAL);

        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void setTimeout(long milliseconds) {
        Intent intent = new Intent(ACTION_TIMEOUT);
        mPendingIntent = PendingIntent.getBroadcast(mContext, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        mAlarmManager.set(AlarmManager.RTC, System.currentTimeMillis()
                + milliseconds, mPendingIntent);
    }

    public void cancelTimeout() {
        mAlarmManager.cancel(mPendingIntent);
    }

    public int getTestTimes() {
        return getSuccessTimes() + getFailureTimes();
    }

    public void setSuccess() {
        setSuccessTimes(getSuccessTimes() + 1);
    }

    public void setFailure() {
        setFailureTimes(getFailureTimes() + 1);
    }

    public int getSuccessTimes() {
        return mPrefUtils.getInt(mSuccess, 0);
    }

    public void setSuccessTimes(int value) {
        mPrefUtils.putInt(mSuccess, value);
    }

    public int getFailureTimes() {
        return mPrefUtils.getInt(mFailure, 0);
    }

    public void setFailureTimes(int value) {
        mPrefUtils.putInt(mFailure, value);
    }

    public int getTotalTimes() {
        return mPrefUtils.getInt(mTimes, 1);
    }

    public void setTotalTimes(int value) {
        mPrefUtils.putInt(mTimes, value);
    }

    public boolean isEnabled() {
        return mPrefUtils.getBoolean(mEnabled, true);
    }

    public void setEnabled(boolean enabled) {
        mPrefUtils.putBoolean(mEnabled, enabled);
    }

    public int getOrdinal() {
        return mPrefUtils.getInt(mOrdinal, 1);
    }

    public void setOrdinal(int ordinal) {
        mPrefUtils.putInt(mOrdinal, ordinal);
    }

    public void setExtras(Bundle bundle) {
        if (bundle == null) return;

        if (bundle.containsKey(mTimes)) {
            setTotalTimes(bundle.getInt(mTimes, 10));
            setEnabled(bundle.getInt(mTimes, 10) > 0);
        } else if (bundle.containsKey(TIMES)) {
            setTotalTimes(bundle.getInt(TIMES, 10));
            setEnabled(bundle.getInt(TIMES, 10) > 0);
        }
        if (bundle.containsKey(mEnabled)) {
            setEnabled(bundle.getBoolean(mEnabled, true));
        } else if (bundle.containsKey(ENABLED)) {
            setEnabled(bundle.getBoolean(ENABLED, true));
        }
        if (bundle.containsKey(mOrdinal)) {
            setOrdinal(bundle.getInt(mOrdinal));
        }
    }

    public void pause() {
        Log.d(LOG_TAG, getClass().getSimpleName() + " paused");
        mPause = true;
        while (mPause) {
            sleep(1000);
        }
    }

    public void resume() {
        Log.d(LOG_TAG, getClass().getSimpleName() + " resumed");
        mPause = false;
    }

    public void sleep(long time) {
        Log.d(LOG_TAG, String.format("%s sleep %d ms", getClass().getSimpleName(), time));
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public View createPreferenceView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pref_edit, null);
        EditText testTime = (EditText) view.findViewById(R.id.test_times);
        testTime.setText(String.valueOf(getTotalTimes()));
        return view;
    }

    public EditText addPreferenceEdit(View view, int labelResId, String defValue) {
        TableRow tr = (TableRow) LayoutInflater.from(mContext).inflate(R.layout.pref_item, null);
        TextView tv = (TextView) tr.findViewById(R.id.item_label);
        tv.setText(labelResId);
        EditText et = (EditText) tr.findViewById(R.id.item_value);
        et.setText(defValue);
        TableLayout layout = (TableLayout) view.findViewById(R.id.table_layout);
        layout.addView(tr);
        return et;
    }

    public void onPreferenceClick(View view) {
        EditText testTime = (EditText) view.findViewById(R.id.test_times);
        try {
            int value = Integer.parseInt(testTime.getText().toString());
            setTotalTimes(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void alert() {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        final Ringtone ringtone = RingtoneManager.getRingtone(mContext, uri);
        ringtone.play();

        // Stop after 5 seconds
        new Thread() {
            public void run() {
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    ringtone.stop();
                }
            }
        }.start();
    }

    private String getPrefixName(String prefix) {
        return String.format("%s_%s", getClass().getSimpleName(), prefix);
    }
}
