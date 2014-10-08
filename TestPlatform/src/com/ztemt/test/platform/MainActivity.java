package com.ztemt.test.platform;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemProperties;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.ztemt.test.platform.VersionUpdate.VersionUpdateListener;

public class MainActivity extends Activity implements VersionUpdateListener,
        ListView.OnItemClickListener {

    public static final String DRAWER_POSITION = "position";

    private Switch mConnectedSwitch;
    private ConnectionEnabler mConnectionEnabler;
    private VersionUpdate mVersionUpdate;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private TextView mStatusText;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mHeaderTitles;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(TaskHandler.ACTION_STATUS_CHANGED)) {
                mStatusText.setText(intent.getStringExtra(Intent.EXTRA_TEXT));
            } else if (action.equals(TaskHandler.ACTION_NOTIFY_DISMISS)) {
                removeDetailFragment();
            }
        }
    };

    private OnClickListener mClickListener = new OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                LocalBroadcastManager.getInstance(MainActivity.this)
                        .sendBroadcast(new Intent(TaskHandler.ACTION_GOON));
            } else {
                LocalBroadcastManager.getInstance(MainActivity.this)
                        .sendBroadcast(new Intent(TaskHandler.ACTION_SKIP));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = mDrawerTitle = getTitle();
        mHeaderTitles = getResources().getStringArray(R.array.drawer_titles);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mStatusText = (TextView) findViewById(R.id.status);

        // Set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        // Set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mHeaderTitles));
        mDrawerList.setOnItemClickListener(this);

        // Enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open,
                R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Set up the switch
        final int padding = getResources().getDimensionPixelSize(
                R.dimen.action_bar_switch_padding);
        mConnectedSwitch = new Switch(this);
        mConnectedSwitch.setTextOn(getString(R.string.connected));
        mConnectedSwitch.setTextOff(getString(R.string.disconnected));
        mConnectedSwitch.setPadding(0, 0, padding, 0);
        mConnectionEnabler = new ConnectionEnabler(this, mConnectedSwitch);

        // Set up the version update
        mVersionUpdate = new VersionUpdate(this);
        mVersionUpdate.setVersionUpdateListener(this);
        mVersionUpdate.queryVersion();

        // Register broadcast for task handler
        IntentFilter filter = new IntentFilter(TaskHandler.ACTION_STATUS_CHANGED);
        filter.addAction(TaskHandler.ACTION_NOTIFY_DISMISS);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);

        // Handle intent
        onNewIntent(getIntent());
    }

    @Override
    protected void onStart() {
        super.onStart();
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(mConnectedSwitch, new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_VERTICAL | Gravity.END));
        mConnectionEnabler.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mConnectionEnabler.resume();
        mStatusText.setText(SystemProperties.get(TaskHandler.KEY_STATUS,
                getString(R.string.ready)));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mConnectionEnabler.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getActionBar().setDisplayOptions(0, ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(null);
        mConnectionEnabler.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVersionUpdate.setVersionUpdateListener(null);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            // Toggle drawer open or close
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle other action bar items
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // Remove task detail fragment if exists
        removeDetailFragment();

        if (intent.hasExtra(TaskDetailFragment.TASK)) {
            TaskDetailFragment detail = new TaskDetailFragment();
            detail.setArguments(intent.getExtras());
            detail.setStyle(DialogFragment.STYLE_NO_FRAME, 0);
            detail.setPositiveListener(mClickListener);
            detail.setNegativeListener(mClickListener);
            detail.show(getFragmentManager(), "detail");
        } else if (intent.hasExtra(DRAWER_POSITION)) {
            int position = intent.getIntExtra(DRAWER_POSITION, 0);
            selectItem(position);
        }
    }

    @Override
    public void onVersionUpdate(String version) {
        if (version != null) {
            mVersionUpdate.startInstallVersion(version);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(position);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    private void removeDetailFragment() {
        FragmentManager fm = getFragmentManager();

        // Remove task detail fragment if exists
        Fragment fragment = fm.findFragmentByTag("detail");
        if (fragment != null) {
            fm.beginTransaction().remove(fragment).commit();
        }
    }

    private void selectItem(int position) {
        // Update the main content by replacing fragments
        Fragment fragment = null;
        if (position == 0) {
            fragment = new ConnectionPreference();
        } else if (position == 1) {
            fragment = new SettingsPreference();
        } else if (position == 2) {
            fragment = new TaskManager();
        } else if (position == 3) {
            fragment = new OutputManager();
        } else if (position == 4) {
            fragment = new AboutPreference();
        }
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame,
                fragment).commit();

        // Update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mHeaderTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }
}
