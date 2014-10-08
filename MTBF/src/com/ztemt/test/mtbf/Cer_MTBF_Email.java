package com.ztemt.test.mtbf;

import android.widget.TextView;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;

public class Cer_MTBF_Email extends Cer_MTBF {

    @Override
    protected void execute() throws UiObjectNotFoundException {
        // 返回主界面
        getUiDevice().pressHome();

        // 进入电子邮件
        UiScrollable apps = new UiScrollable(new UiSelector().scrollable(true));
        apps.setAsHorizontalList();
        apps.waitForExists(60000);
        UiObject title = apps.getChildByText(new UiSelector().className(
                TextView.class), "电子邮件");
        title.clickAndWaitForNewWindow();

        // 断言当前是电子邮件模块
        UiObject validation = new UiObject(new UiSelector().packageName(
                "com.android.email"));
        assertTrue("Unable to detect email", validation.exists());

        // 根据标题判断是否已添加帐户
        UiObject actionBarTitle = new UiObject(new UiSelector().resourceId(
                "android:id/action_bar_title"));
        if (actionBarTitle.exists() && actionBarTitle.getText().equals("添加帐户")) {
            // 选择其他
            UiObject list = new UiObject(new UiSelector().resourceId(
                    "android:id/list"));
            UiObject other = list.getChild(new UiSelector().index(
                    list.getChildCount() - 1));
            other.clickAndWaitForNewWindow();

            // 输入电子邮件地址
            UiObject email = new UiObject(new UiSelector().resourceId(
                    "com.android.email:id/account_email"));
            email.setText("ztemt.settings@gmail.com");

            // 输入密码
            UiObject password = new UiObject(new UiSelector().resourceId(
                    "com.android.email:id/account_password"));
            password.setText("settings");

            // 点击下一步按钮
            UiObject next1 = new UiObject(new UiSelector().resourceId(
                    "com.android.email:id/next"));
            next1.clickAndWaitForNewWindow();

            // 等待检查进度消失
            UiObject checker = new UiObject(new UiSelector().resourceId(
                    "android:id/message"));
            assertTrue("Unable to connect server", checker.waitUntilGone(30000));

            // 点击下一步按钮
            UiObject next2 = new UiObject(new UiSelector().resourceId(
                    "com.android.email:id/next"));
            next2.clickAndWaitForNewWindow();

            // 输入姓名
            UiObject name = new UiObject(new UiSelector().resourceId(
                    "com.android.email:id/account_name"));
            name.setText("MTBF");

            // 点击下一步按钮
            UiObject next3 = new UiObject(new UiSelector().resourceId(
                    "com.android.email:id/next"));
            next3.clickAndWaitForNewWindow();

            // 等待同步
            UiObject inbox = new UiObject(new UiSelector().resourceId(
                    "com.android.email:id/spinner_line_1"));
            inbox.waitForExists(60000);
        }
    }

    protected void sendEmail(boolean addAttachment) throws UiObjectNotFoundException {
        // 点击写邮件按钮
        UiObject compose = new UiObject(new UiSelector().resourceId(
                "com.android.email:id/compose"));
        compose.clickAndWaitForNewWindow();

        // 输入收件人
        UiObject to = new UiObject(new UiSelector().resourceId(
                "com.android.email:id/to"));
        to.setText("ztemt.settings@gmail.com");

        // 输入主题
        UiObject subject = new UiObject(new UiSelector().resourceId(
                "com.android.email:id/subject"));
        subject.setText("test");

        if (addAttachment) {
            // 点击添加附件
            UiObject attachment = new UiObject(new UiSelector().resourceId(
                    "com.android.email:id/add_attachment"));
            attachment.click();
    
            // 选择我的文件
            UiObject myfile = new UiObject(new UiSelector().className(
                    TextView.class).text("我的文件"));
            myfile.clickAndWaitForNewWindow();
    
            // 选择手机存储
            UiObject storage = new UiObject(new UiSelector().resourceId(
                    "cn.nubia.myfile:id/file_name").text("手机存储"));
            storage.click();
    
            // 选择person.jpg文件
            UiScrollable list = new UiScrollable(new UiSelector().resourceId(
                    "cn.nubia.myfile:id/file_path_list"));
            list.setAsVerticalList();
            list.waitForExists(60000);
            UiObject picture = list.getChildByText(new UiSelector().className(
                    TextView.class), "person.jpg");
            picture.clickAndWaitForNewWindow();
        }

        // 点击发送按钮
        UiObject send = new UiObject(new UiSelector().resourceId(
                "com.android.email:id/send"));
        send.click();

        // 选择确定按钮
        UiObject confirm = new UiObject(new UiSelector().resourceId(
                "android:id/button1"));
        confirm.click();
    }

    protected void clearEmail() throws UiObjectNotFoundException {
        clearEmail("发件箱");
        sleep(1000);
        clearEmail("已发送");
        sleep(1000);
        clearEmail("已删除");
    }

    protected void clearEmail(String name) throws UiObjectNotFoundException {
        // 点击查看全部按钮
        UiObject showAll = new UiObject(new UiSelector().resourceId(
                "com.android.email:id/show_all_mailboxes"));
        showAll.click();

        // 选择类型
        UiObject outbox = new UiObject(new UiSelector().resourceId(
                "com.android.email:id/mailbox_name").text(name));
        outbox.click();

        // 为空退出
        UiObject empty = new UiObject(new UiSelector().resourceId(
                "android:id/internalEmpty"));
        if (empty.exists()) {
            return;
        }

        UiScrollable list = new UiScrollable(new UiSelector().resourceId(
                "android:id/list"));
        list.setAsVerticalList();
        list.waitForExists(60000);
        int count = list.getChildCount(new UiSelector().className(
                "android.view.View"));
        if (count > 0) {
            try {
                // 单击右下角勾选
                UiObject first = list.getChild(new UiSelector().index(0));
                first.clickBottomRight();
    
                // 如果数量大于1则全选
                if (count > 1) {
                    UiObject selectAll = new UiObject(new UiSelector().resourceId(
                            "com.android.email:id/select_all"));
                    selectAll.click();
                }
    
                // 点击删除按钮
                UiObject delete = new UiObject(new UiSelector().resourceId(
                        "com.android.email:id/delete"));
                delete.click();
            } catch (UiObjectNotFoundException e) {
                // 视图被删除
            }
        }

        // 返回第一级
        getUiDevice().pressBack();
        sleep(1000);
        getUiDevice().pressBack();
    }
}
