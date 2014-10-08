package com.ztemt.test.mtbf;

import android.widget.TextView;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;

public class Cer_MTBF_Alarm extends Cer_MTBF {

    @Override
    protected void execute() throws UiObjectNotFoundException {
        // 返回主界面
        getUiDevice().pressHome();

        // 进入时间管理
        UiScrollable apps = new UiScrollable(new UiSelector().scrollable(true));
        apps.setAsHorizontalList();
        apps.waitForExists(60000);
        UiObject title = apps.getChildByText(new UiSelector().className(
                TextView.class), "时间管理");
        title.clickAndWaitForNewWindow();

        // 断言当前是时间管理模块
        UiObject validation = new UiObject(new UiSelector().packageName(
                "cn.nubia.deskclock.preset"));
        assertTrue("Unable to detect deskclock", validation.exists());

        // 选择闹钟标签页
        UiObject alarm = new UiObject(new UiSelector().resourceId(
                "cn.nubia.deskclock.preset:id/alarm_clock"));
        alarm.click();
    }

    protected void addAlarm() throws UiObjectNotFoundException {
        // 点击添加闹钟按钮
        UiObject add = new UiObject(new UiSelector().resourceId(
                "cn.nubia.deskclock.preset:id/addNewAlarm"));
        add.clickAndWaitForNewWindow();

        // 点击确定按钮
        UiObject confirm = new UiObject(new UiSelector().resourceId(
                "cn.nubia.deskclock.preset:id/alarm_save"));
        confirm.clickAndWaitForNewWindow();

        // 断言至少有一个闹钟
        UiObject alarms = new UiObject(new UiSelector().resourceId(
                "cn.nubia.deskclock.preset:id/alarms"));
        assertTrue("Unable to add alarm",
                alarms.exists() && alarms.getChildCount() > 0);
    }

    protected void clearAlarm() throws UiObjectNotFoundException {
        UiObject alarms = new UiObject(new UiSelector().resourceId(
                "cn.nubia.deskclock.preset:id/alarms"));

        // 如果存在闹钟则长按第一个
        if (alarms.exists() && alarms.getChildCount() > 0) {
            UiObject first = alarms.getChild(new UiSelector().index(0));
            longClick(first);

            // 如果数量超过一个则全选
            if (alarms.getChildCount() > 1) {
                UiObject selectAll = new UiObject(new UiSelector().resourceId(
                        "cn.nubia.deskclock.preset:id/selected_all"));
                selectAll.click();
            }

            // 点击删除按钮
            UiObject delete = new UiObject(new UiSelector().resourceId(
                    "cn.nubia.deskclock.preset:id/delete_current_alarm"));
            delete.click();

            // 选择确定按钮
            UiObject confirm = new UiObject(new UiSelector().resourceId(
                    "android:id/button1"));
            confirm.click();
        }

        // 断言是否已清空闹钟
        assertFalse("Unable to clear alarm",
                alarms.exists() && alarms.getChildCount() > 0);
    }
}
