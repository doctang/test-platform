package com.ztemt.test.platform;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.ztemt.test.platform.model.Message;

public class MessageService extends Service {

    public static final String EXTRA_MESG = "mesg";
    public static final String EXTRA_TYPE = "type";

    public static final int TYPE_RECEIVE  = 1;
    public static final int TYPE_SEND     = 2;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int type = intent.getIntExtra(EXTRA_TYPE, 0);
        Message message = intent.getParcelableExtra(EXTRA_MESG);
        switch (type) {
        case TYPE_RECEIVE:
            onReceive(message);
            break;
        case TYPE_SEND:
            onSend(message);
            break;
        }
        return START_NOT_STICKY;
    }

    private void onReceive(Message message) {
        switch (message.getType()) {
        case Message.TYPE_TASK_PUBLISH:
            onTaskPublish(message);
            break;
        case Message.TYPE_UPDATE_STATUS:
            onUpdateStatus(message);
            break;
        }
    }

    private void onSend(Message message) {
        Intent intent = new Intent(this, ConnectionService.class);
        intent.putExtra(ConnectionService.EXTRA_COMMAND, ConnectionService.CMD_SEND_MSG);
        intent.putExtra(EXTRA_MESG, message);
        startService(intent);
    }

    private void onTaskPublish(Message message) {
        Intent intent = new Intent(this, TaskService.class);
        intent.putExtra(EXTRA_MESG, message);
        startService(intent);
    }

    private void onUpdateStatus(Message message) {
        // TODO do something after status updated
    }
}
