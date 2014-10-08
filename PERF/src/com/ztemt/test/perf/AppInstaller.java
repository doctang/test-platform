package com.ztemt.test.perf;

import java.io.File;

import android.widget.TextView;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;

/**
 * TOP10APK 安装、启动、卸载
 * @author 0016001973
 *
 */
public class AppInstaller extends PerfTest {

    @Override
    public boolean checkForCondition() {
        UiObject browser = new UiObject(new UiSelector().packageName(
                "com.android.browser"));
        UiObject selection = new UiObject(new UiSelector().resourceId(
                "android:id/alertTitle").text("选择要使用的应用："));
        if (browser.exists() || selection.exists()) {
            getUiDevice().pressBack();
            return true;
        }
        return super.checkForCondition();
    }

    public void test() throws UiObjectNotFoundException {
        // 打印表头
        System.err.println(String.format("%-5s%-30s%-20s%-20s%-20s%-20s%-20s",
                "No.", "Package Name", "App Name", "App Version", "Installation",
                "Running", "Uninstall"));

        for (int i = 0; i < 10; i++) {
            // 强制停止我的文件
            Utils.exec("am force-stop cn.nubia.myfile");

            // 进入我的文件
            Utils.exec("am start --user 0 -W -n cn.nubia.myfile/.CategoryActivity");

            // 断言当前是我的文件模块
            UiObject validation1 = new UiObject(new UiSelector().packageName(
                    "cn.nubia.myfile"));
            assertTrue("Unable to detect myfile", validation1.waitForExists(3000));

            // 进入手机存储
            UiObject storage = new UiObject(new UiSelector().className(
                    TextView.class).text("手机存储"));
            storage.click();

            // 进入perfres文件夹
            UiScrollable files = new UiScrollable(new UiSelector().resourceId(
                    "cn.nubia.myfile:id/file_path_list"));
            files.setAsVerticalList();
            files.waitForExists(60000);
            UiObject perfres = files.getChildByText(new UiSelector().className(
                    TextView.class), "perfres");
            assertTrue("Unable to find perfres", perfres.exists());
            perfres.click();

            // 进入TOP10APK文件夹
            UiObject top10apk = files.getChildByText(new UiSelector().className(
                    TextView.class), "TOP10APK");
            assertTrue("Unable to find top10apk", top10apk.exists());
            top10apk.click();

            // 等待显示完成
            sleep(1000);

            // 滑动到顶部或底部
            if (i == 0) {
                files.scrollToBeginning(1);
            } else if (i == 9) {
                files.scrollToEnd(1);
            }

            // 获得APK新并点击APK文件
            UiObject item = files.getChildByInstance(new UiSelector().resourceId(
                    "cn.nubia.myfile:id/file_browser_item"), i);
            UiObject filename = item.getChild(new UiSelector().resourceId(
                    "cn.nubia.myfile:id/file_name"));
            String[] info = Utils.getApkInfo(new File("/sdcard/perfres/TOP10APK/"
                    + filename.getText()));
            item.clickAndWaitForNewWindow();

            // 断言当前是安装模块
            UiObject validation2 = new UiObject(new UiSelector().packageName(
                    "com.android.packageinstaller"));
            assertTrue("Unable to detect packageinstaller", validation2.exists());

            // 设置允许安装未知源否则点击下一步
            UiObject settings = new UiObject(new UiSelector().resourceId(
                    "android:id/button1").text("设置"));
            if (settings.exists()) {
                // 点击设置按钮
                settings.clickAndWaitForNewWindow();

                // 断言当前是设置模块
                UiObject validation3 = new UiObject(new UiSelector().packageName(
                        "com.android.settings"));
                assertTrue("Unable to detect settings", validation3.exists());

                // 点击未知来源
                UiObject unknown = new UiObject(new UiSelector().resourceId(
                        "android:id/title").text("未知来源"));
                unknown.click();

                // 点击确定
                UiObject confirm = new UiObject(new UiSelector().resourceId(
                        "android:id/button1"));
                confirm.click();

                // 返回我的文件
                getUiDevice().pressBack();

                // 重新安装
                i--;
            } else {
                UiObject appName = new UiObject(new UiSelector().resourceId(
                        "com.android.packageinstaller:id/app_name"));
                String name = appName.getText();
                boolean installed = false;
                boolean launched = false;
                boolean uninstalled = false;

                // 点击下一步按钮直到开始安装
                UiObject next = new UiObject(new UiSelector().resourceId(
                        "com.android.packageinstaller:id/ok_button"));
                while (true) {
                    if (next.getText().equals("下一步")) {
                        next.click();
                    } else {
                        next.clickAndWaitForNewWindow();
                        break;
                    }
                }

                // 断言安装完成
                UiObject launch = new UiObject(new UiSelector().resourceId(
                        "com.android.packageinstaller:id/launch_button"));
                assertTrue("Unable to install " + name, launch.waitForExists(300000));

                // 判断是否安装成功
                installed = launch.isEnabled();
                if (installed) {
                    // 是否启动成功
                    launched = launch.clickAndWaitForNewWindow();

                    // 进入应用管理
                    Utils.exec("am start --user 0 -W --activity-clear-task -a android.settings.MANAGE_ALL_APPLICATIONS_SETTINGS");
                    sleep(3000);

                    // 点击应用名称
                    UiScrollable list = new UiScrollable(new UiSelector().resourceId(
                            "android:id/list"));
                    list.setAsVerticalList();
                    list.waitForExists(60000);
                    UiObject option = list.getChildByText(new UiSelector().resourceId(
                            "com.android.settings:id/app_name"), name, true);
                    option.click();

                    // 点击卸载
                    UiObject uninstall = new UiObject(new UiSelector().resourceId(
                            "com.android.settings:id/right_button"));
                    uninstall.clickAndWaitForNewWindow();

                    // 点击确定按钮
                    uninstalled = next.click();
                }

                // 打印表内容
                int padding = 20 - Utils.getHanziCount(name);
                System.err.println(String.format("%-5s%-30s%-" + padding
                        + "s%-20s%-20s%-20s%-20s", i + 1, info[0], name, info[1],
                        installed ? "Pass" : "Fail",
                        launched ? "Pass" : "Fail",
                        uninstalled ? "Pass" : "Fail"));
            }
        }
    }
}
