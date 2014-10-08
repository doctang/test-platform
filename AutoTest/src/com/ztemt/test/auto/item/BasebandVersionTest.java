package com.ztemt.test.auto.item;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.ztemt.test.auto.R;

public class BasebandVersionTest extends RebootTest {

    private static final String LOG_TAG = "BasebandVersionTest";

    public BasebandVersionTest(Context context) {
        super(context);
    }

    @Override
    public void onRun() {
        String basebandVersion = Build.getRadioVersion();
        if (TextUtils.isEmpty(basebandVersion)) {
            setFailure();
        } else {
            Log.d(LOG_TAG, "basebandVersion = " + basebandVersion);
            setSuccess();
        }

        reboot();
        pause();
    }

    @Override
    public String getTitle() {
        return mContext.getString(R.string.baseband_version_test);
    }
}
