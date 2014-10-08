package com.ztemt.test.mtbf;

import android.widget.Button;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;

/**
 * 下载音频文件并打开
 * 循环次数：5
 * @author 0016001973
 *
 */
public class Cer_MTBF_10 extends Cer_MTBF_Browser implements UiAutomatorTestable {

    @Override
    public void test() throws UiObjectNotFoundException {
        loop(5);
    }

    @Override
    protected void execute() throws UiObjectNotFoundException {
        super.execute();

        // 下载指定音频资源
        load("testplat.server.ztemt.com.cn/resource/Music.mp3");

        // 断言音频播放按钮存在
        UiObject play = new UiObject(new UiSelector().className(
                Button.class).description("播放"));
        assertTrue("Unable to detect play button", play.waitForExists(8000));

        // 保存播放按钮坐标
        int x = play.getBounds().centerX();
        int y = play.getBounds().centerY();

        // 点击播放按钮
        play.click();

        // 播放音乐
        sleep(15000);

        // 点击暂停按钮
        getUiDevice().click(x, y);

        // 退出浏览器
        exit();
    }
}
