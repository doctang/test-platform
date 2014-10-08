package com.ztemt.test.auto;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

import com.ztemt.test.auto.item.BaseTest;
import com.ztemt.test.auto.util.PreferenceUtils;
import com.ztemt.test.platform.PlatformService;

public class AutoTestActivity extends ListActivity implements DialogInterface.OnClickListener,
        OnSharedPreferenceChangeListener {

    private static final String LOG_TAG = "AutoTest";

    private AutoTestAdapter mAdapter;
    private PreferenceUtils mPrefUtils;
    private Handler mHandler = new Handler();
    private View mPrefView;

    private PlatformService mPlatformService;
    private boolean mBound = false;
    private static BaseTest sTest;

    private Runnable mStartRunnable = new Runnable() {

        @Override
        public void run() {
            getListView().setFocusable(false);
            getListView().smoothScrollToPosition(mPrefUtils.getCurrent());
        }
    };

    private Runnable mStopRunnable = new Runnable() {

        @Override
        public void run() {
            Log.d(LOG_TAG, "Notify platform to stop");
            if (mBound) {
                try {
                    mPlatformService.notifyStop(mAdapter.report());
                } catch (RemoteException e) {
                    Log.e(LOG_TAG, "notifyStop", e);
                }
            }
            getListView().setFocusable(true);
        }
    };

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPlatformService = null;
            mBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPlatformService = PlatformService.Stub.asInterface(service);
            mBound = true;
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (sTest != null) {
                sTest.alert();
                sTest.setFailure();
                sTest.resume();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.test_list);

        mPrefUtils = new PreferenceUtils(this);
        mPrefUtils.setOnPreferenceChangeListener(this);

        mAdapter = new AutoTestAdapter(this);
        setListAdapter(mAdapter);
        getListView().setSelected(true);

        bindService(new Intent(PlatformService.class.getName()),
                mServiceConnection, BIND_AUTO_CREATE);

        handleIntent(getIntent().getExtras());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.manual_finish:
            mHandler.post(mStopRunnable);
            mPrefUtils.setCurrent(-1);
            return true;
        case R.id.manual_cancel:
            mAdapter.disableAll();
            mPrefUtils.setCurrent(-1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent.getExtras());
    }

    @Override
    public void onBackPressed() {
        // Nothing to do!
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mPrefUtils.setOnPreferenceChangeListener(null);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        int position = Integer.parseInt(String.valueOf(mPrefView.getTag()));
        mAdapter.getItem(position).onPreferenceClick(mPrefView);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (l.isFocusable()) {
            showPreferenceDialog(position);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
        updateInfo();
    }

    private void updateInfo() {
        mAdapter.notifyDataSetChanged();
    }

    private void showPreferenceDialog(int position) {
        BaseTest test = mAdapter.getItem(position);
        mPrefView = test.createPreferenceView();
        mPrefView.setTag(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(test.getTitle());
        builder.setView(mPrefView);
        builder.setPositiveButton(android.R.string.ok, this);
        builder.show();
    }

    private void registerReceiver() {
        registerReceiver(mReceiver, new IntentFilter(BaseTest.ACTION_TIMEOUT));
    }

    private void unregisterReceiver() {
        try {
            unregisterReceiver(mReceiver);
        } catch (IllegalArgumentException e) {
            Log.e(LOG_TAG, "Timeout receiver is not register");
        }
    }

    private void handleIntent(Bundle bundle) {
        Log.d(LOG_TAG, "handleIntent current=" + mPrefUtils.getCurrent() + ", count="
                + mAdapter.getCount() + ", bundle=" + (bundle != null && !bundle.isEmpty())
                + ", test=" + (sTest == null));
        if (sTest == null && (bundle != null && !bundle.isEmpty() || mPrefUtils.getCurrent() > -1)) {
            if (bundle != null && "auto".equals(bundle.getString("mode"))) {
                mPrefUtils.setCurrent(0);
                mAdapter.clearTimes();
            }

            if (mPrefUtils.getCurrent() > -1 && mPrefUtils.getCurrent() < mAdapter.getCount()) {
                mAdapter.setExtras(bundle);
                new TestThread().start();
            }
        } else {
            Log.d(LOG_TAG, "handleIntent already exist");
        }
    }

    private class TestThread extends Thread {

        @Override
        public void run() {
            int current;
            while ((current = mPrefUtils.getCurrent()) < mAdapter.getCount()) {
                sTest = mAdapter.getItem(current);
                while (sTest != null && sTest.isEnabled()
                        && sTest.getTestTimes() < sTest.getTotalTimes()) {
                    mHandler.post(mStartRunnable);
                    Log.d(LOG_TAG, String.format("%s[%d/%d]", sTest.getClass()
                            .getSimpleName(), sTest.getTestTimes() + 1, sTest
                            .getTotalTimes()));
                    registerReceiver();
                    sTest.onRun();
                    sTest.cancelTimeout();
                    unregisterReceiver();
                }
                mPrefUtils.setCurrent(++current);
            }

            mHandler.postDelayed(mStopRunnable, 3000);
            mPrefUtils.setCurrent(-1);
            sTest = null;
        }
    }
}
