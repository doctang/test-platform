package com.ztemt.test.auto.item;

import java.lang.reflect.Method;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.os.storage.IMountService;
import android.util.Log;

import com.ztemt.test.auto.R;

public class SDCardTest extends BaseTest {

    private static final String LOG_TAG = "SDCardTest";

    private IMountService mMountService;

    private String mMountPoint = Environment.getExternalStorageDirectory().getPath();

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                setSuccess();
                resume();
            } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                setSuccess();
                resume();
            }
        }
    };

    public SDCardTest(Context context) {
        super(context);
        mContext = context;
        mMountService = getMountService();
    }

    @Override
    public void onRun() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            registerReceiver();
            setTimeout(10000);
            unmount();
            pause();
            unregisterReceiver();
        } else if (state.equals(Environment.MEDIA_UNMOUNTED)) {
            registerReceiver();
            setTimeout(10000);
            mount();
            pause();
            unregisterReceiver();
        } else {
            Log.d(LOG_TAG, "SD card state is " + state);
            setFailure();
        }
    }

    @Override
    public String getTitle() {
        return mContext.getString(R.string.sdcard_test);
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addDataScheme("file");
        mContext.registerReceiver(mReceiver, filter);
    }

    private void unregisterReceiver() {
        mContext.unregisterReceiver(mReceiver);
    }

    private boolean mount() {
        try {
            mMountService.mountVolume(mMountPoint);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            return false;
        }
        return true;
    }

    private boolean unmount() {
        try {
            mMountService.unmountVolume(mMountPoint, true, true);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            return false;
        }
        return true;
    }

    private IMountService getMountService() {
        IBinder binder = null;
        try {
            Class<?> c = Class.forName("android.os.ServiceManager");
            Method m = c.getMethod("getService", String.class);
            binder = (IBinder) m.invoke(c, "mount");
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            return null;
        }
        return IMountService.Stub.asInterface(binder);
    }
}
