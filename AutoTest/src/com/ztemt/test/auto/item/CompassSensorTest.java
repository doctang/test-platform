package com.ztemt.test.auto.item;

import android.content.Context;
import android.hardware.Sensor;

import com.ztemt.test.auto.R;

public class CompassSensorTest extends SensorTest {

    public CompassSensorTest(Context context) {
        super(context, Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public String getTitle() {
        return mContext.getString(R.string.compass_sensor_test);
    }
}
