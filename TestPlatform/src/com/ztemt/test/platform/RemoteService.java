package com.ztemt.test.platform;

import java.io.File;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.ztemt.test.platform.model.SystemTest;
import com.ztemt.test.platform.model.Task;
import com.ztemt.test.platform.util.FileUtils;

public class RemoteService extends Service {

    private static final String TAG = "RemoteService";

    private final PlatformService.Stub mBinder = new PlatformService.Stub() {

        @Override
        public void updateProgress(int progress) throws RemoteException {
            Log.d(TAG, "updateProgress to " + progress);
        }

        @Override
        public void notifyStop(byte[] bytes) throws RemoteException {
            Log.d(TAG, "notifyStop");
            SystemTest test = notifyStop();
            if (test != null && bytes != null) {
                File file = new File(OutputManager.OUTPUT, test.getId() + "-"
                        + test.getName() + ".txt");
                FileUtils.write(bytes, file);
            }
        }

        @Override
        public void notifyStopWithName(byte[] bytes, String fileName)
                throws RemoteException {
            Log.d(TAG, "notifyStop");
            SystemTest test = notifyStop();
            if (test != null && bytes != null) {
                File file = new File(OutputManager.OUTPUT, test.getId() + "-"
                        + fileName);
                FileUtils.write(bytes, file);
            }
        }

        @Override
        public void notifyStart() throws RemoteException {
            Log.d(TAG, "notifyStart");
        }

        private SystemTest notifyStop() {
            SystemTest test = null;
            for (Task task : TaskList.get()) {
                if (task instanceof SystemTest) {
                    test = (SystemTest) task;
                    if (test.getStatus() == Task.STATUS_GOING && test.isWaitable()) {
                        test.setStatus(Task.STATUS_OK);
                        TaskList.sync();
                        break;
                    }
                }
            }
            return test;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
