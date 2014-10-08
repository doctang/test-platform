package com.ztemt.test.mtbf;

import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;

/**
 * 下载图片文件并打开
 * 循环次数：5
 * @author 0016001973
 *
 */
public class Cer_MTBF_11 extends Cer_MTBF_Browser implements UiAutomatorTestable {

    @Override
    public void test() throws UiObjectNotFoundException {
        loop(5);
    }

    @Override
    protected void execute() throws UiObjectNotFoundException {
        super.execute();

        // 设置允许加载图片
        setLoadPicture();

        // 下载指定图片文件
        load("testplat.server.ztemt.com.cn/resource/Picture.jpg");

        // 断言图片显示
        UiObject picture = new UiObject(new UiSelector().descriptionStartsWith(
                "Picture.jpg"));
        assertTrue("Unable to load picture", picture.waitForExists(8000));

        // 退出浏览器
        exit();
    }

    protected void setLoadPicture() throws UiObjectNotFoundException {
        // 点击更多按钮
        showMore();

        // 选择设置
        UiObject settings = new UiObject(new UiSelector().resourceId(
                "android:id/title").text("设置"));
        settings.clickAndWaitForNewWindow();

        // 选择带宽管理
        UiObject privacy = new UiObject(new UiSelector().resourceId(
                "android:id/title").text("带宽管理"));
        privacy.clickAndWaitForNewWindow();

        // 勾选加载图片
        UiObject list = new UiObject(new UiSelector().className(ListView.class));
        for (int i = 0; i < list.getChildCount(); i++) {
            UiObject ll = list.getChild(new UiSelector().className(
                    LinearLayout.class).index(i));
            UiObject tv = ll.getChild(new UiSelector()
                    .className(TextView.class).index(0));
            if (tv.getText().equals("加载图片")) {
                UiObject enable = ll.getChild(new UiSelector().resourceId(
                        "android:id/checkbox"));
                if (enable.isCheckable() && !enable.isChecked()) {
                    enable.click();
                }
                break;
            }
        }

        // 返回浏览界面
        getUiDevice().pressBack();
        getUiDevice().pressBack();
    }
}
