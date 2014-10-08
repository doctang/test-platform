package com.ztemt.test.auto.item;

import java.lang.reflect.Method;

import com.ztemt.test.auto.R;

import android.content.Context;
import android.util.Log;

public class BreathinglightTest extends BaseTest {

    private static final String LOG_TAG = "BreathinglightTest";
    private static final String CLASS_NAME = "android.os.nubia.breathlight.BreathinglightManager";

    public BreathinglightTest(Context context) {
        super(context);
    }

    @Override
    public void onRun() {
        updateBreathinglightStatus(3);
        sleep(2000);
        setSuccess();
    }

    @Override
    public String getTitle() {
        return mContext.getString(R.string.breathinglight_test);
    }

    private void updateBreathinglightStatus(int status) {
        try {
            Class<?> c = Class.forName(CLASS_NAME);
            Method m = c.getMethod("updateBreathLightStatus", Integer.TYPE);
            m.invoke(c.newInstance(), status);
        } catch (Exception e) {
            Log.e(LOG_TAG, "updateBreathinglightStatus", e);
        }
    }
}
