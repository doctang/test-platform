package com.ztemt.test.mtbf;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;

/**
 * 打开特定的web页面
 * 循环次数：5
 * @author 0016001973
 *
 */
public class Cer_MTBF_09 extends Cer_MTBF_Browser implements UiAutomatorTestable {

    @Override
    public void test() throws UiObjectNotFoundException {
        loop(5);
    }

    @Override
    protected void execute() throws UiObjectNotFoundException {
        super.execute();

        // 清除缓存，历史记录和cookie数据
        clearData();

        // 前往指定网址
        load("sina.cn");

        // 等待载入
        sleep(8000);

        // 退出浏览器
        exit();
    }

    protected void clearData() throws UiObjectNotFoundException {
        // 点击更多按钮
        showMore();

        // 选择设置
        UiObject settings = new UiObject(new UiSelector().resourceId(
                "android:id/title").text("设置"));
        settings.clickAndWaitForNewWindow();

        // 选择隐私和安全
        UiObject privacy = new UiObject(new UiSelector().resourceId(
                "android:id/title").text("隐私和安全"));
        privacy.clickAndWaitForNewWindow();

        // 点击清除缓存
        UiObject cache = new UiObject(new UiSelector().resourceId(
                "android:id/title").text("清除缓存"));
        cache.click();

        // 点击确定按钮
        UiObject ok1 = new UiObject(new UiSelector().resourceId(
                "android:id/button1"));
        ok1.click();

        // 点击清除历史记录
        UiObject history = new UiObject(new UiSelector().resourceId(
                "android:id/title").text("清除历史记录"));
        history.click();

        // 点击确定按钮
        UiObject ok2 = new UiObject(new UiSelector().resourceId(
                "android:id/button1"));
        ok2.click();

        // 点击清除所有Cookie数据
        UiObject cookie = new UiObject(new UiSelector().resourceId(
                "android:id/title").text("清除所有 Cookie 数据"));
        cookie.click();

        // 点击确定按钮
        UiObject ok3 = new UiObject(new UiSelector().resourceId(
                "android:id/button1"));
        ok3.click();

        // 返回浏览界面
        getUiDevice().pressBack();
        getUiDevice().pressBack();
    }
}
