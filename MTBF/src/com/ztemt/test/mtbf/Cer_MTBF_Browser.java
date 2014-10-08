package com.ztemt.test.mtbf;

import android.view.Surface;
import android.widget.TextView;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;

public class Cer_MTBF_Browser extends Cer_MTBF {

    @Override
    protected void execute() throws UiObjectNotFoundException {
        // 返回主界面
        getUiDevice().pressHome();

        // 进入浏览器
        UiObject title = new UiObject(new UiSelector().className(
                TextView.class).text("浏览器"));
        title.clickAndWaitForNewWindow();

        // 断言当前是浏览器模块
        UiObject validation = new UiObject(new UiSelector().packageName(
                "com.android.browser"));
        assertTrue("Unable to detect browser", validation.exists());
    }

    protected void load(String url) throws UiObjectNotFoundException {
        // 清空地址栏并输入特定网址
        UiObject address = new UiObject(new UiSelector().resourceId(
                "com.android.browser:id/url"));
        address.click();  // 单击全选地址栏内容
        getUiDevice().pressDelete();
        address.setText(url);

        // 回车前往
        getUiDevice().pressEnter();
    }

    protected void exit() throws UiObjectNotFoundException {
        // 点击更多按钮
        showMore();

        // 选择退出
        UiObject exit = new UiObject(new UiSelector().resourceId(
                "android:id/title").text("退出"));
        exit.click();

        // 选择确认
        UiObject ok = new UiObject(new UiSelector().resourceId(
                "android:id/button1"));
        ok.click();
    }

    protected void showMore() throws UiObjectNotFoundException {
        int rotation = getUiDevice().getDisplayRotation();
        if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            // 横屏方向
            UiObject more = new UiObject(new UiSelector().resourceId(
                    "com.android.browser:id/show_more"));
            more.click();
        } else {
            // 竖屏方向
            UiObject more = new UiObject(new UiSelector().resourceId(
                    "com.android.browser:id/more"));
            more.click();
        }
    }
}
