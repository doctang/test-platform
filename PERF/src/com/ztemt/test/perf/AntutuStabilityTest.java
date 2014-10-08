package com.ztemt.test.perf;

import java.io.File;
import java.io.FilenameFilter;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;

/**
 * 安兔兔稳定性测试
 * @author 0016001973
 *
 */
public class AntutuStabilityTest extends PerfTest {

    public void test() throws UiObjectNotFoundException {
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

        // 点击菜单图标
        UiObject menu = new UiObject(new UiSelector().resourceId(
                "com.antutu.ABenchMark:id/menu_back_img"));
        menu.click();

        // 点击稳定性菜单
        UiObject stability = new UiObject(new UiSelector().resourceId(
                "com.antutu.ABenchMark:id/title").text("稳定性"));
        stability.click();

        // 如果已测试，则点击重新测试
        UiObject again = new UiObject(new UiSelector().resourceId(
                "com.antutu.ABenchMark:id/test_again_btn"));
        if (again.exists()) {
            again.click();
        }

        // 等待停止按钮消失
        UiObject stop = new UiObject(new UiSelector().resourceId(
                "com.antutu.ABenchMark:id/stop"));
        stop.waitUntilGone(1800000);

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
