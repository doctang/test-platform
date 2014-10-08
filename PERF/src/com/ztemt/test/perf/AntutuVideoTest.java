package com.ztemt.test.perf;

import java.io.File;
import java.io.FilenameFilter;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;

/**
 * 安兔兔视频播放兼容性测试
 * @author 0016001973
 *
 */
public class AntutuVideoTest extends PerfTest {

    public void test() throws UiObjectNotFoundException {
        // 获取安装文件
        File dir = new File("/sdcard/perfres/BENCHMARK/antutuvideobench/");
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".apk");
            }
        });

        // 断言安装文件存在
        assertTrue("Unable to find antutuvideobench file", files.length == 1);

        // 安装antutuvideobench
        Utils.exec(String.format("pm install -r %s", files[0].getAbsolutePath()));

        // 启动antutuvideobench
        Utils.exec("am start --user 0 -W -n com.antutu.videobench/.activity.VideoMainActivity");

        // 断言antutuvideobench启动完成
        UiObject play = new UiObject(new UiSelector().resourceId(
                "com.antutu.videobench:id/test_movieplayBT"));
        assertTrue("Unable to launch antutuvideobench", play.waitForExists(30000));

        // 点击视频测试
        play.click();

        // 提示下载视频资源
        UiObject message = new UiObject(new UiSelector().resourceId(
                "android:id/alertTitle").text("提示"));
        if (message.waitForExists(3000)) {
            // 点击确定
            UiObject ok = new UiObject(new UiSelector().resourceId(
                    "android:id/button1"));
            ok.click();

            // 等待下载完成
            UiObject progress = new UiObject(new UiSelector().resourceId(
                    "com.antutu.videobench:id/progress"));
            assertTrue("Unable to download video", progress.waitUntilGone(1800000));
        }

        // 断言测试启动完成
        UiObject movie = new UiObject(new UiSelector().resourceId(
                "com.antutu.videobench:id/movieplayerSV"));
        assertTrue("Unable to start test", movie.waitForExists(30000));

        // 等待测试结果显示
        UiObject detail = new UiObject(new UiSelector().resourceId(
                "com.antutu.videobench:id/main_titleTV").text("详情"));
        assertTrue("Unable to find detail", detail.waitForExists(600000));

        // 点击返回
        UiObject back = new UiObject(new UiSelector().resourceId(
                "com.antutu.videobench:id/result_backBT"));
        back.clickAndWaitForNewWindow();

        // 截屏
        String jpg = getParams().getString("jpg", "/data/local/tmp/test.jpg");
        File store = new File(jpg);
        getUiDevice().takeScreenshot(store);
        store.setReadable(true, false);
        store.setWritable(true, false);

        // 卸载antutuvideobench
        Utils.exec("pm uninstall com.antutu.videobench");
    }
}
