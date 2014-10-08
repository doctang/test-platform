package com.ztemt.test.platform;

import java.util.List;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.Status;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemProperties;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.ztemt.test.platform.Registration.RegisterInfo;
import com.ztemt.test.platform.model.Message;
import com.ztemt.test.platform.util.FileUtils;
import com.ztemt.test.platform.util.ProcessUtils;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class ConnectionService extends Service {

    private static final String TAG = "ConnectionService";
    private static final String KEY_CONNECTED = "persist.sys.testplat.connected";
    private static final String KEY_SSID = "persist.sys.testplat.ssid";

    public static final String ACTION_HEARTBEAT = "com.ztemt.test.platform.action.HEARTBEAT";
    public static final String ACTION_STATE_CHANGED = "com.ztemt.test.platform.action.STATE_CHANGED";
    public static final String EXTRA_STATE   = "state";
    public static final String EXTRA_COMMAND = "command";

    public static final int STATE_CONNECTING     = 1;
    public static final int STATE_CONNECT_FAILED = 2;
    public static final int STATE_CONNECT_OPENED = 3;
    public static final int STATE_CONNECT_CLOSED = 4;

    public static final int CMD_SEND_MSG = 1;

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    // Web socket connection
    private WebSocketConnection mConnection = new WebSocketConnection();
    private boolean mFromUser;

    // Alarm pending intent
    private PendingIntent mPendingIntent;

    // Manage wifi
    private WifiManager mWifiManager;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_HEARTBEAT.equals(action)) {
                beat();
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                NetworkInfo info = (NetworkInfo) intent
                        .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info != null
                        && info.getType() == ConnectivityManager.TYPE_WIFI
                        && info.getState() == State.CONNECTED) {
                    connect(false);
                }
                if (SystemProperties.getBoolean(KEY_CONNECTED, false)) {
                    startDaemon();
                }
            }
        }
    };

    private WebSocketHandler mHandler = new WebSocketHandler() {

        @Override
        public void onOpen() {
            setTimerTask(60000);

            SystemProperties.set(KEY_CONNECTED, String.valueOf(true));

            // Switch to adb tcp mode
            //new AdbMode().setAdbMode(AdbMode.MODE_TCP);

            // Start connection daemon
            startDaemon();

            // Keep screen on
            keepScreenOn(true);

            // Notify ui update
            broadcastStateChanged(STATE_CONNECT_OPENED);
            startForeground(getString(R.string.server_connected));

            // Update task status
            startService(new Intent(ConnectionService.this, TaskStatusService.class));

            if (mFromUser) {
                // Save the ssid by manual connected
                WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
                SystemProperties.set(KEY_SSID, wifiManager.getConnectionInfo().getSSID());

                // Handle message queue
                sendMessage(new Message(Message.TYPE_TASK_PUBLISH));
                mFromUser = false;
            }
        }

        @Override
        public void onTextMessage(String payload) {
            Log.d(TAG, "Recv " + payload);
            handleMessage(MessageService.TYPE_RECEIVE, new Message(payload));
        }

        @Override
        public void onClose(int code, String reason) {
            cancelTimerTask();
            broadcastStateChanged(STATE_CONNECT_CLOSED);
            startForeground(getString(R.string.server_disconnected));
            if (mFromUser) {
                SystemProperties.set(KEY_CONNECTED, String.valueOf(false));
                mFromUser = false;
            }
        }
    };

    private class RegisterTask extends AsyncTask<String, Void, RegisterInfo> {

        @Override
        protected RegisterInfo doInBackground(String... params) {
            Registration registration = new Registration(ConnectionService.this);
            RegisterInfo info = registration.register(params[0]);
            return info;
        }

        @Override
        protected void onPostExecute(RegisterInfo result) {
            switch (result.result) {
            case 1:
                try {
                    broadcastStateChanged(STATE_CONNECTING);
                    mConnection.connect(result.getWebSocketUrl(), mHandler);
                } catch (WebSocketException e) {
                    Log.e(TAG, "Error connect to web socket", e);
                    broadcastStateChanged(STATE_CONNECT_FAILED);
                }
                break;
            case 8001002:
                broadcastStateChanged(STATE_CONNECT_FAILED);
                showToast(R.string.wifi_mac_invalid);
                mFromUser = false;
                break;
            case 8001004:
                broadcastStateChanged(STATE_CONNECT_FAILED);
                showToast(R.string.user_name_invalid);
                Intent intent = new Intent(ConnectionService.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(MainActivity.DRAWER_POSITION, 0);
                startActivity(intent);
                mFromUser = false;
                break;
            case 8001005:
                broadcastStateChanged(STATE_CONNECT_FAILED);
                showToast(R.string.user_name_inactivated);
                mFromUser = false;
                break;
            case 8001006:
                broadcastStateChanged(STATE_CONNECT_FAILED);
                showToast(R.string.device_related);
                mFromUser = false;
                break;
            default:
                showToast("Register error code: " + result.result);
                broadcastStateChanged(STATE_CONNECT_FAILED);
                mFromUser = false;
                break;
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        mPendingIntent = PendingIntent.getBroadcast(this, 0,
                new Intent(ACTION_HEARTBEAT), PendingIntent.FLAG_UPDATE_CURRENT);
        IntentFilter filter = new IntentFilter(ACTION_HEARTBEAT);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(mReceiver, filter);

        // Start a foreground service, avoid kill by out of memory
        startForeground(getString(R.string.server_disconnected));

        // Create work directory and copy asset files
        FileUtils.init(this);

        // Request message queue
        Message message = new Message(Message.TYPE_TASK_PUBLISH);
        handleMessage(MessageService.TYPE_RECEIVE, message);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            int cmdId = intent.getIntExtra(EXTRA_COMMAND, 0);
            switch (cmdId) {
            case CMD_SEND_MSG:
                Message message = intent.getParcelableExtra(MessageService.EXTRA_MESG);
                sendMessage(message);
                break;
            default:
                if (ActivityManager.isUserAMonkey()) {
                    enableWifi();
                }
                break;
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        unregisterReceiver(mReceiver);
        cancelTimerTask();
        disconnect(false);
    }

    public boolean isConnected() {
        return mConnection.isConnected();
    }

    public void connect(boolean fromUser) {
        boolean connected = SystemProperties.getBoolean(KEY_CONNECTED, false);
        if (!isConnected() && (fromUser || connected)) {
            mFromUser = fromUser;
            new RegisterTask().execute(getString(R.string.register_url));
        }
    }

    public void disconnect(boolean fromUser) {
        if (isConnected()) {
            mFromUser = fromUser;
            mConnection.disconnect();
        }
    }

    public boolean sendMessage(Message message) {
        if (isConnected()) {
            String text = message.toString();
            mConnection.sendTextMessage(text);
            Log.d(TAG, "Send " + text);
            return true;
        }
        return false;
    }

    public void handleMessage(int type, Message message) {
        Intent intent = new Intent(this, MessageService.class);
        intent.putExtra(MessageService.EXTRA_TYPE, type);
        intent.putExtra(MessageService.EXTRA_MESG, message);
        startService(intent);
    }

    public class LocalBinder extends Binder {
        public ConnectionService getService() {
            return ConnectionService.this;
        }
    }

    private void setTimerTask(int delay) {
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, delay, delay, mPendingIntent);
    }

    private void cancelTimerTask() {
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(mPendingIntent);
    }

    private void broadcastStateChanged(int state) {
        Intent i = new Intent(ACTION_STATE_CHANGED).putExtra(EXTRA_STATE, state);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    private void startForeground(String text) {
        PendingIntent p = PendingIntent.getActivity(this, 0, new Intent(this,
                MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder b = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker(getString(R.string.app_name))
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getString(R.string.app_name))
                .setContentText(text)
                .setTicker(Registration.getUserName(this))
                .setContentIntent(p);
        startForeground(R.string.app_name, b.build());
    }

    private WifiConfiguration getWifiConfiguration(String ssid) {
        List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
        if (configs == null) return null;
        for (WifiConfiguration config : configs) {
            if (ssid.equals(config.SSID)) {
                return config;
            }
        }
        return null;
    }

    private void enableWifi() {
        if (mWifiManager.isWifiEnabled()) {
            String ssid = SystemProperties.get(KEY_SSID, "");
            if (SystemProperties.getBoolean(KEY_CONNECTED, false)) {
                WifiConfiguration config = getWifiConfiguration(ssid);
                if (config != null && config.status == Status.ENABLED) {
                    mWifiManager.enableNetwork(config.networkId, true);
                }
            }
        } else {
            mWifiManager.setWifiEnabled(true);
        }
    }

    private void beat() {
        Message message = new Message(Message.TYPE_HEARTBEAT);
        if (!sendMessage(message)) {
            Log.e(TAG, "Connection is closed");
            cancelTimerTask();
            broadcastStateChanged(STATE_CONNECT_CLOSED);
        }
    }

    private void startDaemon() {
        ProcessUtils.startProcess("platd", getPackageName(), getClass().getName());
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void keepScreenOn(boolean on) {
        int value = on ? (BatteryManager.BATTERY_PLUGGED_AC
                | BatteryManager.BATTERY_PLUGGED_USB) : 0;
        if (Build.VERSION.SDK_INT < 17) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STAY_ON_WHILE_PLUGGED_IN, value);
        } else {
            Settings.Global.putInt(getContentResolver(),
                    Settings.Global.STAY_ON_WHILE_PLUGGED_IN, value);
        }
    }

    private void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
