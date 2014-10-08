package com.ztemt.test.perf;

import java.io.File;
import java.io.FilenameFilter;

import android.os.RemoteException;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;

/**
 * 流畅性测试
 * @author 0016001973
 *
 */
public class MobileXPRTTest extends PerfTest {

    public void test() throws UiObjectNotFoundException, RemoteException {
        // 获取安装文件
        File dir = new File("/sdcard/perfres/BENCHMARK/mobilexprt/");
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".apk");
            }
        });

        // 断言安装文件存在
        assertTrue("Unable to find mobilexprt files", files.length == 2);

        // 拷贝MobileXPRT测试资源
        Utils.exec(String.format("cp -rf %s %s",
                "/sdcard/perfres/BENCHMARK/mobilexprt/com.mobilexprt",
                "/sdcard/Android/data/"));

        // 安装MobileXPRT
        Utils.exec(String.format("pm install -r %s", files[0].getAbsolutePath()));
        Utils.exec(String.format("pm install -r %s", files[1].getAbsolutePath()));

        // 启动MobileXPRT
        Utils.exec("am start --user 0 -W -n com.mobilexprt/.MobileXPRT");

        // 断言MobileXPRT启动完成
        UiObject validation = new UiObject(new UiSelector().packageName(
                "com.mobilexprt"));
        assertTrue("Unable to launch mobilexprt", validation.waitForExists(3000));

        // 点击所有测试
        UiObject allTests = new UiObject(new UiSelector().resourceId(
                "com.mobilexprt:id/button_all_tests"));
        allTests.clickAndWaitForNewWindow();

        // 等待测试结果显示
        UiObject result = new UiObject(new UiSelector().resourceId(
                "com.mobilexprt:id/elv"));
        assertTrue("Unable to find tests result", result.waitForExists(1800000));

        // 竖屏显示
        getUiDevice().setOrientationNatural();
        sleep(3000);

        // 截屏
        String jpg = getParams().getString("jpg", "/data/local/tmp/test.jpg");
        File store = new File(jpg);
        getUiDevice().takeScreenshot(store);
        store.setReadable(true, false);
        store.setWritable(true, false);

        // 卸载MobileXPRT
        Utils.exec("pm uninstall com.mobilexprt");
        Utils.exec("pm uninstall com.mobilexprt.scrollperf");
    }
}
