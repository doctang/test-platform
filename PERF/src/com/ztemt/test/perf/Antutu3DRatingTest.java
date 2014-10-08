package com.ztemt.test.perf;

import java.io.File;
import java.io.FilenameFilter;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;

/**
 * 安兔兔3D跑分测试
 * @author 0016001973
 *
 */
public class Antutu3DRatingTest extends PerfTest {

    public void test() throws UiObjectNotFoundException {
        // 获取antutu3Drating安装文件
        File dir = new File("/sdcard/perfres/BENCHMARK/antutu3Drating/");
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".apk");
            }
        });

        // 断言antutu3Drating安装文件存在
        Utils.exec(String.format("pm install -r %s", files[0].getAbsolutePath()));

        // 安装antutu3Drating
        Utils.exec("pm install -r \"" + files[0].getAbsolutePath() + "\"");

        // 启动antutu3Drating
        Utils.exec("am start --user 0 -W -n com.antutu.ABenchMark.GL3/.UnityPlayerProxyActivity");

        // 断言antutu3Drating启动完成
        UiObject validation = new UiObject(new UiSelector().packageName(
                "com.antutu.ABenchMark.GL3"));
        assertTrue("Unable to launch antutu3Drating", validation.waitForExists(3000));

        // 等待加载完成
        sleep(10000);

        // 点击左下角开始测试
        int width = getUiDevice().getDisplayWidth();
        int height = getUiDevice().getDisplayHeight();
        getUiDevice().click(width * 2 / 9, height * 15 / 17);

        // 等待测试结束
        sleep(180000);

        // 截图测试结果
        String jpg = getParams().getString("jpg", "/data/local/tmp/test.jpg");
        File store = new File(jpg);
        getUiDevice().takeScreenshot(store);
        store.setReadable(true, false);
        store.setWritable(true, false);

        // 卸载antutu3Drating
        Utils.exec("pm uninstall com.antutu.ABenchMark.GL3");
    }
}
