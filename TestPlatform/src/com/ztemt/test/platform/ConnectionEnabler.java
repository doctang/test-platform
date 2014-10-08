package com.ztemt.test.platform;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.ztemt.test.platform.ConnectionService.LocalBinder;

public class ConnectionEnabler implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "ConnectionEnabler";

    private Activity mActivity;
    private ProgressDialog mProgressDialog;
    private Switch mSwitch;
    private boolean mValidListener;

    private ConnectionService mService;
    private boolean mBound = false;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ConnectionService.ACTION_STATE_CHANGED.equals(action)) {
                switch (intent.getIntExtra(ConnectionService.EXTRA_STATE, 0)) {
                case ConnectionService.STATE_CONNECT_CLOSED:
                    setChecked(false);
                    dismissProgressDialog();
                    break;
                case ConnectionService.STATE_CONNECT_OPENED:
                    setChecked(true);
                    dismissProgressDialog();
                    break;
                case ConnectionService.STATE_CONNECTING:
                    showProgressDialog(R.string.connecting);
                    break;
                case ConnectionService.STATE_CONNECT_FAILED:
                    setChecked(false);
                    dismissProgressDialog();
                    break;
                }
            }
        }
    };

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocalBinder binder = (LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            setChecked(isConnected());
        }
    };

    public ConnectionEnabler(Activity activity, Switch switch_) {
        mActivity = activity;
        mSwitch = switch_;
        mValidListener = false;
        activity.startService(new Intent(activity, ConnectionService.class));
    }

    public void start() {
        // Bind to MessageService
        Intent intent = new Intent(mActivity, ConnectionService.class);
        mActivity.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void resume() {
        mSwitch.setEnabled(!ActivityManager.isUserAMonkey());
        mSwitch.setOnCheckedChangeListener(this);
        mValidListener = true;
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mReceiver,
                new IntentFilter(ConnectionService.ACTION_STATE_CHANGED));
    }

    public void pause() {
        mSwitch.setEnabled(!ActivityManager.isUserAMonkey());
        mSwitch.setOnCheckedChangeListener(null);
        mValidListener = false;
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mReceiver);
    }

    public void stop() {
        // Unbind from the service
        if (mBound) {
            mActivity.unbindService(mServiceConnection);
            mBound = false;
        }
    }

    public void setSwitch(Switch switch_) {
        if (mSwitch == switch_) return;
        mSwitch.setOnCheckedChangeListener(null);
        mSwitch = switch_;
        mSwitch.setOnCheckedChangeListener(mValidListener ? this : null);
    }

    public boolean isConnected() {
        if (mBound) {
            return mService.isConnected();
        } else {
            return false;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            showProgressDialog(R.string.registering);
            if (mBound) mService.connect(true);
        } else if (isConnected()) {
            showProgressDialog(R.string.disconnecting);
            if (mBound) mService.disconnect(true);
        } else {
            Log.d(TAG, "isChecked: " + isChecked + ", isConnected: " + isConnected());
        }
    }

    private void setChecked(boolean isChecked) {
        if (isChecked != mSwitch.isChecked()) {
            // set listener to null, so onCheckedChanged won't be called
            // if the checked status on Switch isn't changed by user click
            if (mValidListener) {
                mSwitch.setOnCheckedChangeListener(null);
            }
            mSwitch.setChecked(isChecked);
            if (mValidListener) {
                mSwitch.setOnCheckedChangeListener(this);
            }
        }
    }

    public void showProgressDialog(final int resId, Object... args) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.setMessage(mActivity.getString(resId, args));
        } else {
            mProgressDialog = new android.app.ProgressDialog(mActivity);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMessage(mActivity.getString(resId, args));
            mProgressDialog.show();
        }
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}
