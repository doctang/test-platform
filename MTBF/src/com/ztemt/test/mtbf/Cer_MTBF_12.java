package com.ztemt.test.mtbf;

import android.widget.ProgressBar;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;

/**
 * 下载视频并能正确打开
 * 循环次数：5
 * @author 0016001973
 *
 */
public class Cer_MTBF_12 extends Cer_MTBF_Browser implements UiAutomatorTestable {

    @Override
    public void test() throws UiObjectNotFoundException {
        loop(5);
    }

    @Override
    protected void execute() throws UiObjectNotFoundException {
        super.execute();

        // 载入指定视频文件
        load("testplat.server.ztemt.com.cn/resource/Video.avi");

        // 等待打开视频播放界面
        UiObject video = new UiObject(new UiSelector().packageName(
                "com.android.video"));
        assertTrue("Unable to open video", video.waitForExists(8000));

        // 等待载入视频完成
        UiObject progress = new UiObject(new UiSelector().className(
                ProgressBar.class));
        assertTrue("Unable to load video", progress.waitUntilGone(600000));

        // 等待视频播放完成
        UiObject browser = new UiObject(new UiSelector().packageName(
                "com.android.browser"));
        assertTrue("Unable to exit video", browser.waitForExists(300000));

        // 退出浏览器
        exit();
    }
}
