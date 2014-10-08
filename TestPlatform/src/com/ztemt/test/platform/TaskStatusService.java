package com.ztemt.test.platform;

import java.util.ArrayList;
import java.util.List;

import com.ztemt.test.platform.data.TextData;
import com.ztemt.test.platform.data.TextDataFactory;
import com.ztemt.test.platform.model.Message;
import com.ztemt.test.platform.model.Task;

import android.app.IntentService;
import android.content.Intent;

public class TaskStatusService extends IntentService {

    public static final String EXTRA_TASK = "task";

    public TaskStatusService() {
        super("task status");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        List<TextData> list = new ArrayList<TextData>();

        if (intent.hasExtra(EXTRA_TASK)) {
            Task task = (Task) intent.getSerializableExtra(EXTRA_TASK);
            list.add(createStatusData(task));
        } else {
            for (Task task : TaskList.get()) {
                list.add(createStatusData(task));
            }
        }

        Message message = new Message(Message.TYPE_UPDATE_STATUS);
        message.setArrayExtra("update", list);

        Intent sender = new Intent(this, MessageService.class);
        sender.putExtra(MessageService.EXTRA_TYPE, MessageService.TYPE_SEND);
        sender.putExtra(MessageService.EXTRA_MESG, message);
        startService(sender);

        stopSelf();
    }

    private TextData createStatusData(Task task) {
        TextData data = TextDataFactory.create();
        data.putLong(Task.ID, task.getId());
        data.putInt(Task.STATUS, task.getStatus());
        return data;
    }
}
