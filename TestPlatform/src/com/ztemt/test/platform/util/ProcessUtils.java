package com.ztemt.test.platform.util;

import java.io.File;
import java.io.IOException;

public class ProcessUtils {

    public static Process startProcess(File dir, String... prog) {
        File file = FileUtils.getFileStreamPath(prog[0]);
        if (file.exists()) {
            prog[0] = file.getAbsolutePath();
        }

        Process p = null;
        try {
            p = Runtime.getRuntime().exec(prog, null, dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return p;
    }

    public static void startProcessWait(File dir, String... prog) {
        Process p = startProcess(dir, prog);
        if (p != null) {
            try {
                p.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static Process startProcess(String... prog) {
        return startProcess(FileUtils.DIR, prog);
    }

    public static void startProcessWait(String... prog) {
        startProcessWait(FileUtils.DIR, prog);
    }

    public static int pidof(String processName) {
        Process p = startProcess("sh", "pidof.sh", processName);
        int pid = 0;

        if (p != null) {
            try {
                pid = Integer.parseInt(FileUtils.readLine(p.getInputStream()));
            } catch (NumberFormatException e) {
                //e.printStackTrace();
            }
        }
        return pid;
    }

    public static void kill(int pid) {
        startProcess("kill", String.valueOf(pid));
    }
}
