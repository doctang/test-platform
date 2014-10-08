package com.ztemt.test.auto.item;

import android.content.Context;
import android.os.Vibrator;

import com.ztemt.test.auto.R;

public class VibratorTest extends BaseTest {

    private Vibrator mVibrator;

    public VibratorTest(Context context) {
        super(context);
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public void onRun() {
        mVibrator.vibrate(new long[] { 0, 2000, 2000 }, 0);
        sleep(6000);
        mVibrator.cancel();
        sleep(1000);
        setSuccess();
    }

    @Override
    public String getTitle() {
        return mContext.getString(R.string.vibrator_test);
    }
}
