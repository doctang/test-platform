package com.ztemt.test.platform;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.os.SystemProperties;

import com.ztemt.test.platform.data.XmlData;
import com.ztemt.test.platform.model.Module;
import com.ztemt.test.platform.model.SystemTest;
import com.ztemt.test.platform.model.Task;
import com.ztemt.test.platform.util.FileUtils;
import com.ztemt.test.platform.util.HttpDownloader;
import com.ztemt.test.platform.util.HttpDownloader.HttpDownloadListener;
import com.ztemt.test.platform.util.ProcessUtils;

public class SystemTestHandler extends TaskHandler implements HttpDownloadListener {

    private static final String KEY_NEVER_TEST = "persist.sys.testplat.notest";

    private SystemTest mSystemTest;

    private HttpDownloader mDownloader;

    private long mModuleLastModified;

    public SystemTestHandler(Context context, SystemTest systemTest) {
        super(context, systemTest);

        mSystemTest = systemTest;

        mDownloader = new HttpDownloader(context);
        mDownloader.setDownloadListener(this);
    }

    @Override
    public void onDownloadComplete(File file) {
        if (file != null && FileUtils.unzip(file, FileUtils.DIR)) {
            setModuleNumber(mModuleLastModified);
            file.delete();
        }
        resume();
    }

    public static boolean isNeverTest() {
        return SystemProperties.getBoolean(KEY_NEVER_TEST, false);
    }

    public static void setNeverTest(boolean neverTest) {
        SystemProperties.set(KEY_NEVER_TEST, String.valueOf(neverTest));
    }

    @Override
    public void prepare() {
        String moduleUrl = mContext.getString(R.string.update_url,
                mSystemTest.getModule() + ".zip");
        mModuleLastModified = getModuleLastModified(moduleUrl);

        if (mModuleLastModified > getModuleNumber() || getModuleNumber() == 0) {
            mDownloader.download(moduleUrl, null,
                    mContext.getString(R.string.module_update_title),
                    mContext.getString(R.string.module_update_text,
                    mSystemTest.getModule()));
            pause();
        }
    }

    @Override
    protected void onRun() {
        String title = mContext.getString(R.string.system_test);
        updateStatusText(title);
        boolean restart = false;

        int status = mSystemTest.getStatus();
        if (status == Task.STATUS_NOT_START) {
            if (isNeverTest() || waitNotificationCanceled(title, 180)) {
                setStatus(Task.STATUS_CANCEL);
                return;
            }
        } else {
            restart = true;
        }

        String name = mSystemTest.getName();
        String module = mSystemTest.getModule();
        String extras = mSystemTest.getExtras();

        updateStatusText(name);

        if (restart) {
            while (ProcessUtils.pidof("plate") > 0
                    || mSystemTest.isWaitable()
                    && mSystemTest.getStatus() != Task.STATUS_OK
                    && mSystemTest.getStatus() != Task.STATUS_FAIL
                    && mSystemTest.getStatus() != Task.STATUS_CANCEL) {
                SystemClock.sleep(5000);
            }
        } else {
            FileUtils.mkdir(OutputManager.OUTPUT);
            OutputManager.OUTPUT.setWritable(true, false);
            String path = new File(OutputManager.OUTPUT, mSystemTest.getId()
                    + "-" + name).getAbsolutePath();

            File file = new File(FileUtils.getFileStreamPath(module), "module.xml");
            Module m = new Module(new XmlData(file));
            setWaitable(m.getType() == Module.TYPE_APK);

            setStatus(Task.STATUS_GOING);
            ProcessUtils.startProcessWait("plate", module, extras, path);

            while (mSystemTest.isWaitable()
                    && mSystemTest.getStatus() != Task.STATUS_OK
                    && mSystemTest.getStatus() != Task.STATUS_FAIL
                    && mSystemTest.getStatus() != Task.STATUS_CANCEL) {
                SystemClock.sleep(5000);
            }
        }

        setStatus(Task.STATUS_OK);
        ProcessUtils.startProcessWait("sh", String.format("%s/exit.sh", module));
        mContext.startService(new Intent(mContext, OutputService.class));
    }

    private long getModuleLastModified(String moduleUrl) {
        try {
            URL url = new URL(moduleUrl);
            URLConnection conn = url.openConnection();
            return conn.getLastModified();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private long getModuleNumber() {
        File file = FileUtils.getFileStreamPath(mSystemTest.getModule() + ".txt");
        String line = FileUtils.readLine(file);

        try {
            return Long.parseLong(line);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void setModuleNumber(long number) {
        File file = FileUtils.getFileStreamPath(mSystemTest.getModule() + ".txt");
        FileUtils.write(String.valueOf(number), file);
    }

    private void setWaitable(boolean waitable) {
        int index = TaskList.get().indexOf(mSystemTest);
        if (index > -1) {
            SystemTest test = (SystemTest) TaskList.get().get(index);
            test.setWaitable(waitable);
            TaskList.sync();
        }
    }
}
