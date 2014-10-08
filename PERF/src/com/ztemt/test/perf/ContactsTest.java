package com.ztemt.test.perf;

import android.view.View;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;

/**
 * 联系人批量导入、删除
 * @author 0016001973
 *
 */
public class ContactsTest extends PerfTest {

    public void test() throws UiObjectNotFoundException {
        // 进入联系人
        Utils.exec("am start --user 0 -W -a android.intent.action.MAIN "
                + "-c android.intent.category.LAUNCHER "
                + "-n com.android.contacts/.activities.PeopleActivity");

        // 断言当前是联系人模块
        UiObject validation = new UiObject(new UiSelector().packageName(
                "com.android.contacts"));
        assertTrue("Unable to detect contacts", validation.exists());

        // 清空联系人
        clearContacts();

        // 打印表头
        System.err.println(String.format("%-12s%-12s%-12s", "Item Number",
                "Import Time", "Delete Time"));

        // 导入200联系人
        long time1 = importContacts("ContactsData200.vcf");

        // 清空联系人
        long time2 = clearContacts();

        // 打印200联系人时间数据
        System.err.println(String.format("%-12s%-12s%-12s", "200", time1, time2));

        // 导入1000联系人
        long time3 = importContacts("ContactsData1000.vcf");

        // 清空联系人
        long time4 = clearContacts();

        // 打印1000联系人时间数据
        System.err.println(String.format("%-12s%-12s%-12s", "1000", time3, time4));

        // 导入5000联系人
        long time5 = importContacts("ContactsData5000.vcf");

        // 清空联系人
        long time6 = clearContacts();

        // 打印5000联系人时间数据
        System.err.println(String.format("%-12s%-12s%-12s", "5000", time5, time6));

        // 导入10000联系人
        long time7 = importContacts("ContactsData10000.vcf");

        // 清空联系人
        long time8 = clearContacts();

        // 打印10000联系人时间数据
        System.err.println(String.format("%-12s%-12s%-12s", "5000", time7, time8));
    }

    public long importContacts(String vCard) throws UiObjectNotFoundException {
        long start = 0, end = 0;

        // 点击更多按钮
        UiObject more = new UiObject(new UiSelector().resourceId(
                "com.android.contacts:id/btn_more"));
        more.click();

        // 点击联系人高级选项
        UiObject advance = new UiObject(new UiSelector().resourceId(
                "android:id/title").text("联系人高级选项"));
        advance.clickAndWaitForNewWindow();

        // 点击导入联系人
        UiObject importContacts = new UiObject(new UiSelector().resourceId(
                "android:id/title").text("导入 vCard 格式联系人"));
        importContacts.click();

        // 等待选择显示完成并选择导入一个Vcard文件项
        UiObject selection = new UiObject(new UiSelector().resourceId(
                "android:id/text1").text("导入一个 vCard 文件"));
        assertTrue("Unable to find any vcard", selection.waitForExists(10000));
        selection.click();

        // 点击确定按钮
        UiObject ok = new UiObject(new UiSelector().resourceId("android:id/button1"));
        ok.click();

        // 选择vCard文件
        UiObject file = new UiObject(new UiSelector().resourceId(
                "android:id/text1").textStartsWith(vCard));
        file.click();

        // 点击确定按钮
        ok.click();

        // 开始时间
        start = System.currentTimeMillis();

        // 打开通知栏
        getUiDevice().openNotification();

        // 等待导入完成
        UiObject finish = new UiObject(new UiSelector().resourceId(
                "android:id/title").text("导入 vCard " + vCard + " 已完成"));
        finish.waitForExists(900000);

        // 结束时间
        end = System.currentTimeMillis();

        // 关闭通知栏
        getUiDevice().pressBack();

        // 返回联系人主界面
        UiObject up = new UiObject(new UiSelector().resourceId("android:id/up"));
        up.click();

        // 等待显示完成
        UiObject list = new UiObject(new UiSelector().resourceId("android:id/list"));
        UiObject item = list.getChild(new UiSelector().index(2));
        item.waitForExists(10000);

        return end - start;
    }

    public long clearContacts() throws UiObjectNotFoundException {
        long start = 0, end = 0;

        UiScrollable list = new UiScrollable(new UiSelector().resourceId(
                "android:id/list"));
        int count = list.getChildCount(new UiSelector().className(View.class));

        if (count > 0) {
            // 点击更多按钮
            UiObject more = new UiObject(new UiSelector().resourceId(
                    "com.android.contacts:id/btn_more"));
            more.click();

            // 点击删除联系人
            UiObject delete = new UiObject(new UiSelector().resourceId(
                    "android:id/title").text("删除联系人"));
            delete.clickAndWaitForNewWindow();

            // 等待联系人列表显示完成
            UiObject item = list.getChild(new UiSelector().index(0));
            item.waitForExists(10000);

            // 点击全选
            UiObject selectAll = new UiObject(new UiSelector().resourceId(
                    "com.android.contacts:id/btn_selectAll"));
            selectAll.click();

            // 点击确定按钮
            UiObject ok = new UiObject(new UiSelector().resourceId(
                    "com.android.contacts:id/btn_ok"));
            ok.click();

            // 点击确认删除
            UiObject confirm = new UiObject(new UiSelector().resourceId(
                    "android:id/button1"));
            confirm.click();

            // 开始时间
            start = System.currentTimeMillis();

            // 等待删除完成
            UiObject progress = new UiObject(new UiSelector().resourceId(
                    "android:id/progress"));
            progress.waitUntilGone(60000);

            // 结束时间
            end = System.currentTimeMillis();
        }

        return end - start;
    }
}
