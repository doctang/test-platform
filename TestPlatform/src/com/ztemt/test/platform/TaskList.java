package com.ztemt.test.platform;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ztemt.test.platform.model.Task;
import com.ztemt.test.platform.util.FileUtils;

@SuppressWarnings("unchecked")
public class TaskList {

    private static File sFile = FileUtils.getFileStreamPath("tasks.txt");

    private static List<Task> sList = new ArrayList<Task>();

    static {
        Object obj = FileUtils.readObject(sFile);

        if (obj != null) {
            sList = (List<Task>) obj;
        }
    }

    /** Returns this list */
    public static List<Task> get() {
        return sList;
    }

    /** Synchronize this queue to file system */
    public static void sync() {
        FileUtils.writeObject(sList, sFile);
    }
}
