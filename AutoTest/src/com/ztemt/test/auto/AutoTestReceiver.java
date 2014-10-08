package com.ztemt.test.auto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ztemt.test.auto.util.PreferenceUtils;

public class AutoTestReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            PreferenceUtils pref = new PreferenceUtils(context);
            if (pref.getCurrent() > -1) {
                Intent i = new Intent(context, AutoTestActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        }
    }
}
