package com.ztemt.test.mtbf;

import android.widget.TextView;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;

public class Cer_MTBF_Sms extends Cer_MTBF_Telephony {

    @Override
    protected void execute() throws UiObjectNotFoundException {
        // 返回主界面
        getUiDevice().pressHome();

        // 进入信息
        UiObject title = new UiObject(new UiSelector().className(
                TextView.class).text("信息"));
        title.clickAndWaitForNewWindow();

        // 断言当前是联系人模块
        UiObject validation = new UiObject(new UiSelector().packageName(
                "com.android.contacts"));
        assertTrue("Unable to detect contacts", validation.exists());
    }

    protected void clearMessage() throws UiObjectNotFoundException {
        UiObject list = new UiObject(new UiSelector().resourceId("android:id/list"));

        if (list.exists()) {
            int count = list.getChildCount();

            if (count > 0) {
                // 长按第一条会话
                UiObject item = list.getChild(new UiSelector().index(0));
                longClick(item);

                // 如果会话条数超过一条则点击全选按钮
                if (count > 1) {
                    UiObject selectAll = new UiObject(new UiSelector().resourceId(
                            "com.android.contacts:id/select_all"));
                    selectAll.click();
                }

                // 点击删除按钮
                UiObject delete = new UiObject(new UiSelector().resourceId(
                        "com.android.contacts:id/delete"));
                delete.click();

                // 点击确定删除
                UiObject confirm = new UiObject(new UiSelector().resourceId(
                        "android:id/button1"));
                confirm.click();
            }
        }
    }

    protected void newMessage() throws UiObjectNotFoundException {
        // 点击新建信息按钮
        UiObject newConversation = new UiObject(new UiSelector().resourceId(
                "com.android.contacts:id/empty_conversation_new_button"));
        if (newConversation.exists()) {
            newConversation.clickAndWaitForNewWindow();
        } else {
            newConversation = new UiObject(new UiSelector().resourceId(
                    "com.android.contacts:id/new_message_btn"));
            newConversation.click();
        }

        // 输入收件人
        UiObject recipients = new UiObject(new UiSelector().resourceId(
                "com.android.contacts:id/recipients_editor"));
        recipients.setText(getOperatorNumber());

        // 输入内容
        UiObject text = new UiObject(new UiSelector().resourceId(
                "com.android.contacts:id/embedded_text_editor"));
        text.setText("test");
    }

    protected void sendMessage() throws UiObjectNotFoundException {
        // 点击发送按钮
        UiObject send = new UiObject(new UiSelector().resourceId(
                "com.android.contacts:id/send_message"));
        assertTrue("Unable to send message", send.isEnabled());
        send.click();

        // 等待回信
        sleep(15000);
    }
}
