package com.ztemt.test.auto.item;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public abstract class SensorTest extends BaseTest implements
        SensorEventListener {

    private static final String LOG_TAG = "SensorTest";

    private int mSensorType;

    private boolean mSensorChanged;

    public SensorTest(Context context, int sensorType) {
        super(context);
        mSensorType = sensorType;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!mSensorChanged) {
            Log.d(LOG_TAG, "onSensorChanged");
            mSensorChanged = true;
            setSuccess();
            resume();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(LOG_TAG, "onAccuracyChanged(" + accuracy + ")");
    }

    @Override
    public void onRun() {
        SensorManager sm = (SensorManager) mContext
                .getSystemService(Context.SENSOR_SERVICE);
        if (sm.getDefaultSensor(mSensorType) == null) {
            Log.e(LOG_TAG, "No sensor(" + mSensorType + ")");
            setFailure();
        } else {
            Sensor sensor = sm.getDefaultSensor(mSensorType);
            mSensorChanged = false;
            sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
            setTimeout(5000);
            pause();
            sm.unregisterListener(this);
        }
    }
}
