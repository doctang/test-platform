package com.ztemt.test.perf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.os.RemoteException;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;

/**
 * 开机时系统内存占用
 * @author 0016001973
 *
 */
public class MeminfoDumper extends PerfTest {

    public void test() throws UiObjectNotFoundException, RemoteException {
        // 返回主界面
        getUiDevice().pressHome();

        // 打开最近使用的应用程序用户向导
        Utils.exec("am start --user 0 -n com.android.systemui/.NubiaRecent.UserGuideActivity");

        // 没有启动完成则按下最近使用应用程序
        UiObject validation = new UiObject(new UiSelector().packageName(
                "com.android.systemui"));
        if (validation.waitForExists(3000)) {
            // 点击我知道了
            UiObject hand = new UiObject(new UiSelector().resourceId(
                    "com.android.systemui:id/hand_click"));
            if (hand.exists()) {
                hand.click();
            }
        } else {
            getUiDevice().pressRecentApps();
        }

        // 点击回收并等待消失
        UiObject recycle = new UiObject(new UiSelector().resourceId(
                "com.android.systemui:id/recycle"));
        if (recycle.waitForExists(3000)) {
            recycle.click();
            recycle.waitUntilGone(8000);
        }

        // 获取系统内存信息
        dumpsysMeminfo();
    }

    private void dumpsysMeminfo() {
        String prog = "dumpsys meminfo";

        try {
            Process p = Runtime.getRuntime().exec(prog);
            InputStreamReader in = new InputStreamReader(p.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String line = null;
            while ((line = br.readLine()) != null) {
                System.err.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
