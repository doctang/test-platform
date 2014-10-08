package com.ztemt.test.perf;

import java.io.File;
import java.io.FilenameFilter;

import android.os.RemoteException;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;

/**
 * 安兔兔跑分测试
 * @author 0016001973
 *
 */
public class AntutuBenchmarkTest extends PerfTest {

    public void test() throws UiObjectNotFoundException, RemoteException  {
        // 获取安兔兔安装文件
        File dir = new File("/sdcard/perfres/BENCHMARK/Antutu/");
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".apk");
            }
        });

        // 断言安兔兔安装文件存在
        assertTrue("Unable to find antutu file", files.length == 1);

        // 安装安兔兔
        Utils.exec(String.format("pm install -r %s", files[0].getAbsolutePath()));

        // 启动安兔兔
        Utils.exec("am start --user 0 -W -n com.antutu.ABenchMark/.ABenchMarkStart");

        // 断言安兔兔启动完成
        UiObject title = new UiObject(new UiSelector().resourceId(
                "com.antutu.ABenchMark:id/title_text"));
        assertTrue("Unable to launch antutu", title.waitForExists(10000));

        // 点击开始测试
        UiObject test = new UiObject(new UiSelector().resourceId(
                "com.antutu.ABenchMark:id/test_btn"));
        test.clickAndWaitForNewWindow();

        // 点击开始测试
        UiObject text = new UiObject(new UiSelector().resourceId(
                "com.antutu.ABenchMark:id/start_test_text"));
        text.clickAndWaitForNewWindow();

        // 等待测试结束
        UiObject detail = new UiObject(new UiSelector().resourceId(
                "com.antutu.ABenchMark:id/detail_btn"));
        assertTrue("Unalbe to finish test", detail.waitForExists(900000));

        // 返回上一级
        UiObject back = new UiObject(new UiSelector().resourceId(
                "com.antutu.ABenchMark:id/menu_back_img"));
        back.clickAndWaitForNewWindow();

        // 等待返回完成
        test.waitForExists(3000);

        // 切换成竖屏
        getUiDevice().setOrientationNatural();
        sleep(3000);

        // 截图
        String jpg = getParams().getString("jpg", "/data/local/tmp/test.jpg");
        File store = new File(jpg);
        getUiDevice().takeScreenshot(store);
        store.setReadable(true, false);
        store.setWritable(true, false);

        // 卸载安兔兔
        Utils.exec("pm uninstall com.antutu.ABenchMark");
    }
}
