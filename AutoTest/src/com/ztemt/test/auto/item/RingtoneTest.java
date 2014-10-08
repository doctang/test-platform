package com.ztemt.test.auto.item;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.ztemt.test.auto.R;

public class RingtoneTest extends BaseTest {

    private static final String LOG_TAG = "RingtoneTest";

    public RingtoneTest(Context context) {
        super(context);
    }

    @Override
    public void onRun() {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        Ringtone r = RingtoneManager.getRingtone(mContext, uri);
        try {
            r.play();
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            setFailure();
            return;
        }

        sleep(15000);

        // Stop ringtone play.
        if (r != null) {
            r.stop();
        }

        setSuccess();
    }

    @Override
    public String getTitle() {
        return mContext.getString(R.string.ringtone_test);
    }
}
