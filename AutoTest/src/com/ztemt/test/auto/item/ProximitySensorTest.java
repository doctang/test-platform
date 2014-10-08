package com.ztemt.test.auto.item;

import android.content.Context;
import android.hardware.Sensor;

import com.ztemt.test.auto.R;

public class ProximitySensorTest extends SensorTest {

    public ProximitySensorTest(Context context) {
        super(context, Sensor.TYPE_PROXIMITY);
    }

    @Override
    public String getTitle() {
        return mContext.getString(R.string.proximity_sensor_test);
    }
}
