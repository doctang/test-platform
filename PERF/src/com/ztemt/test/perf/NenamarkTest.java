package com.ztemt.test.perf;

import java.io.File;
import java.io.FilenameFilter;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;

/**
 * Nenamark性能测试
 * @author 0016001973
 *
 */
public class NenamarkTest extends PerfTest {

    public void test() throws UiObjectNotFoundException {
        // 获取nenamark安装文件
        File dir = new File("/sdcard/perfres/BENCHMARK/nenamark/");
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".apk");
            }
        });

        // 断言nenamark安装文件存在
        assertTrue("Unable to find nenamark file", files.length == 1);

        // 安装nenamark
        Utils.exec(String.format("pm install -r %s", files[0].getAbsolutePath()));

        // 启动nenamark
        Utils.exec("am start --user 0 -W -n se.nena.nenamark2/.NenaMark2");

        // 断言nenamark启动完成
        UiObject validation = new UiObject(new UiSelector().packageName(
                "se.nena.nenamark2"));
        assertTrue("Unable to launch nenamark", validation.waitForExists(3000));

        // 点击运行按钮
        int width = getUiDevice().getDisplayWidth();
        int height = getUiDevice().getDisplayHeight();
        getUiDevice().click(width * 3 / 14, height * 1 / 3);

        // 等待测试结束
        UiObject back = new UiObject(new UiSelector().resourceId(
                "se.nena.nenamark2:id/finished_ButtonBack"));
        assertTrue("Unable to finish nenamark", back.waitForExists(300000));

        // 点击返回按钮
        back.click();

        // 截图测试结果
        String jpg = getParams().getString("jpg", "/data/local/tmp/test.jpg");
        File store = new File(jpg);
        getUiDevice().takeScreenshot(store);
        store.setReadable(true, false);
        store.setWritable(true, false);

        // 卸载nenamark
        Utils.exec("pm uninstall se.nena.nenamark2");
    }
}
