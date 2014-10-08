package com.ztemt.test.mtbf;

import android.widget.TextView;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;

/**
 * 播放音乐
 * 循环次数：20
 * @author 0016001973
 *
 */
public class Cer_MTBF_15 extends Cer_MTBF implements UiAutomatorTestable {

    @Override
    public void test() throws UiObjectNotFoundException {
        loop(20);
    }

    @Override
    protected void execute() throws UiObjectNotFoundException {
        // 返回主界面
        getUiDevice().pressHome();

        // 进入音乐
        UiScrollable apps = new UiScrollable(new UiSelector().scrollable(true));
        apps.setAsHorizontalList();
        apps.waitForExists(60000);
        UiObject title = apps.getChildByText(new UiSelector().className(
                TextView.class), "音乐");
        title.clickAndWaitForNewWindow();

        // 断言当前是音乐模块
        UiObject validation = new UiObject(new UiSelector().packageName(
                "cn.nubia.music.preset"));
        assertTrue("Unable to detect music", validation.exists());

        // 跳过向导
        UiObject skip = new UiObject(new UiSelector().resourceId(
                "cn.nubia.music.preset:id/skip"));
        if (skip.waitForExists(3000)) {
            skip.click();
        }

        // 跳过提示
        UiObject confirm = new UiObject(new UiSelector().resourceId(
                "android:id/button1"));
        if (confirm.exists()) {
            confirm.click();
        }

        // 跳过扫描提示
        UiObject scan = new UiObject(new UiSelector().resourceId(
                "android:id/alertTitle"));
        if (scan.waitForExists(1000)) {
            UiObject finish = new UiObject(new UiSelector().resourceId(
                    "cn.nubia.music.preset:id/scan"));
            if (finish.waitForExists(15000)) {
                finish.click();
            }
        }

        // 选择我的音乐标签
        UiObject myMusic = new UiObject(new UiSelector().resourceId(
                "cn.nubia.music.preset:id/tab_1"));
        myMusic.click();

        // 点击全部歌曲
        UiObject localMusic = new UiObject(new UiSelector().resourceId(
                "cn.nubia.music.preset:id/local_music_layout"));
        localMusic.clickAndWaitForNewWindow();

        // 点击随机播放全部
        UiObject play = new UiObject(new UiSelector().resourceId(
                "cn.nubia.music.preset:id/play_all_text"));
        if (play.exists()) {
            play.click();
    
            // 等待一段时间
            sleep(10000);
    
            // 点击停止按钮
            UiObject stop = new UiObject(new UiSelector().resourceId(
                    "cn.nubia.music.preset:id/play_btn"));
            stop.click();
        }

        // 返回上一级
        getUiDevice().pressBack();

        // 点击更多按钮
        UiObject more = new UiObject(new UiSelector().resourceId(
                "cn.nubia.music.preset:id/local_more_layout"));
        more.clickAndWaitForNewWindow();

        // 点击退出菜单
        UiScrollable list = new UiScrollable(new UiSelector().resourceId(
                "android:id/list"));
        list.setAsVerticalList();
        list.waitForExists(60000);
        UiObject exit = list.getChildByText(new UiSelector().className(
                TextView.class), "退出");
        exit.click();
    }
}
