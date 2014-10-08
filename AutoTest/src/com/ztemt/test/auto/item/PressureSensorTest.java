package com.ztemt.test.auto.item;

import android.content.Context;
import android.hardware.Sensor;

import com.ztemt.test.auto.R;

public class PressureSensorTest extends SensorTest {

    public PressureSensorTest(Context context) {
        super(context, Sensor.TYPE_PRESSURE);
    }

    @Override
    public String getTitle() {
        return mContext.getString(R.string.pressure_sensor_test);
    }
}
