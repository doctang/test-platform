package com.ztemt.test.auto.item;

import android.content.Context;
import android.hardware.Sensor;

import com.ztemt.test.auto.R;

public class HumiditySensorTest extends SensorTest {

    public HumiditySensorTest(Context context) {
        super(context, Sensor.TYPE_RELATIVE_HUMIDITY);
    }

    @Override
    public String getTitle() {
        return mContext.getString(R.string.humidity_sensor_test);
    }
}
