package com.ztemt.test.perf;

import java.io.File;
import java.io.FilenameFilter;

import android.widget.RelativeLayout;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;

/**
 * Vellamo浏览器性能测试
 * @author 0016001973
 *
 */
public class VellamoBrowserTest extends PerfTest {

    public void test() throws UiObjectNotFoundException {
        // 获取Vellamo安装文件
        File dir = new File("/sdcard/perfres/BENCHMARK/Vellamo/");
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".apk");
            }
        });

        // 断言Vellamo安装文件存在
        assertTrue("Unable to find vellamo file", files.length == 1);

        // 安装Vellamo
        Utils.exec(String.format("pm install -r %s", files[0].getAbsolutePath()));

        // 启动Vellomo
        Utils.exec("am start --user 0 -W -n com.quicinc.vellamo/.main.MainActivity");

        // 提示是否同意条款
        UiObject alert = new UiObject(new UiSelector().resourceId(
                "android:id/alertTitle").text("Vellamo EULA"));
        if (alert.waitForExists(2000)) {
            UiObject ok = new UiObject(new UiSelector().resourceId(
                    "android:id/button1"));
            ok.click();
        }

        // 断言Vellomo启动完成
        UiObject title = new UiObject(new UiSelector().resourceId(
                "com.quicinc.vellamo:id/main_toolbar_product_title"));
        assertTrue("Unable to launch velloma", title.waitForExists(3000));

        // 查找浏览器测试
        UiScrollable cards = new UiScrollable(new UiSelector().resourceId(
                "com.quicinc.vellamo:id/space_cards"));
        cards.setAsVerticalList();
        cards.waitForExists(60000);
        UiObject browser = cards.getChildByText(new UiSelector().resourceId(
                "com.quicinc.vellamo:id/card_topbar_text"), "浏览器");
        assertTrue("Unable to find cpu", browser.exists());

        // 点击运行
        UiObject card3 = new UiObject(new UiSelector().className(
                RelativeLayout.class).index(2));
        UiObject run = card3.getChild(new UiSelector().resourceId(
                "com.quicinc.vellamo:id/card_launcher_run_button"));
        run.clickAndWaitForNewWindow();

        // 滑动到提示最后
        UiScrollable pager = new UiScrollable(new UiSelector().resourceId(
                "com.quicinc.vellamo:id/tutorial_pager"));
        pager.setAsHorizontalList();
        pager.waitForExists(60000);
        if (pager.exists()) {
            pager.scrollToEnd(2);
        }

        // 断言测试开始
        UiObject container = new UiObject(new UiSelector().resourceId(
                "com.quicinc.vellamo:id/main_spaces_container"));
        assertTrue("Unable to start test", container.waitForExists(3000));

        // 等待测试结束
        UiObject score = new UiObject(new UiSelector().resourceId(
                "com.quicinc.vellamo:id/card_score_score"));
        score.waitForExists(600000);

        // 等待分数显示完成
        sleep(3000);

        // 截图测试结果
        String jpg = getParams().getString("jpg", "/data/local/tmp/test.jpg");
        File store = new File(jpg);
        getUiDevice().takeScreenshot(store);
        store.setReadable(true, false);
        store.setWritable(true, false);

        // 卸载Vellamo
        Utils.exec("pm uninstall com.quicinc.vellamo");
    }
}
