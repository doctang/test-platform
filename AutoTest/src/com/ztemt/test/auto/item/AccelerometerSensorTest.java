package com.ztemt.test.auto.item;

import android.content.Context;
import android.hardware.Sensor;

import com.ztemt.test.auto.R;

public class AccelerometerSensorTest extends SensorTest {

    public AccelerometerSensorTest(Context context) {
        super(context, Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public String getTitle() {
        return mContext.getString(R.string.accelerometer_sensor_test);
    }
}
