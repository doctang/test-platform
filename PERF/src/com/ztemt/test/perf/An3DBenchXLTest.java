package com.ztemt.test.perf;

import java.io.File;
import java.io.FilenameFilter;

import android.view.View;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;

/**
 * GPU性能测试
 * @author 0016001973
 *
 */
public class An3DBenchXLTest extends PerfTest {

    public void test() throws UiObjectNotFoundException {
        // 获取an3Dbenchxl安装文件
        File dir = new File("/sdcard/perfres/BENCHMARK/an3Dbenchxl/");
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".apk");
            }
        });

        // 断言an3Dbenchxl安装文件存在
        assertTrue("Unable to find an3Dbenchxl file", files.length == 1);

        // 安装an3Dbenchxl
        Utils.exec(String.format("pm install -r %s", files[0].getAbsolutePath()));

        // 启动an3Dbenchxl
        Utils.exec("am start --user 0 -W -n com.threed.jpct.benchxl/.An3DBenchXL");

        // 断言an3Dbenchxl启动完成
        UiObject validation = new UiObject(new UiSelector().packageName(
                "com.threed.jpct.benchxl"));
        assertTrue("Unable to launch an3Dbenchxl", validation.waitForExists(3000));

        // 点击开始
        UiObject view = new UiObject(new UiSelector().className(View.class));
        view.clickAndWaitForNewWindow();

        // 等待测试结束
        UiObject result = new UiObject(new UiSelector().resourceId(
                "android:id/button1").text("Upload results"));
        result.waitForExists(600000);

        // 截图测试结果
        String jpg = getParams().getString("jpg", "/data/local/tmp/test.jpg");
        File store = new File(jpg);
        getUiDevice().takeScreenshot(store);
        store.setReadable(true, false);
        store.setWritable(true, false);

        // 卸载an3Dbenchxl
        Utils.exec("pm uninstall com.threed.jpct.benchxl");
    }
}
