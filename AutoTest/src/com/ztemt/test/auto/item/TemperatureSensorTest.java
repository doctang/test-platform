package com.ztemt.test.auto.item;

import android.content.Context;
import android.hardware.Sensor;

import com.ztemt.test.auto.R;

public class TemperatureSensorTest extends SensorTest {

    public TemperatureSensorTest(Context context) {
        super(context, Sensor.TYPE_AMBIENT_TEMPERATURE);
    }

    @Override
    public String getTitle() {
        return mContext.getString(R.string.temperature_sensor_test);
    }
}
