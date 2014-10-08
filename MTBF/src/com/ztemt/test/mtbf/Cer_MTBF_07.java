package com.ztemt.test.mtbf;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;

/**
 * 打开邮件
 * 循环次数：10
 * @author 0016001973
 *
 */
public class Cer_MTBF_07 extends Cer_MTBF_Email implements UiAutomatorTestable {

    @Override
    public void test() throws UiObjectNotFoundException {
        loop(10);
    }

    @Override
    protected void execute() throws UiObjectNotFoundException {
        super.execute();

        // 发送邮件
        sendEmail(true);

        // 点击查看全部按钮
        UiObject showAll = new UiObject(new UiSelector().resourceId(
                "com.android.email:id/show_all_mailboxes"));
        showAll.click();

        // 查看发件箱是由有邮件
        UiObject list = new UiObject(new UiSelector().resourceId(
                "android:id/list"));
        UiObject outbox = list.getChild(new UiSelector().index(2));
        UiObject count = outbox.getChild(new UiSelector().resourceId(
                "com.android.email:id/message_count"));

        // 如果有邮件则进入发件箱否则进入已发送
        if (count.exists()) {
            outbox.click();
        } else {
            UiObject sendbox = list.getChild(new UiSelector().index(3));
            sendbox.click();
        }

        // 查看第一封邮件
        UiObject first = list.getChild(new UiSelector().index(0));
        if (first.exists()) {
            try {
                first.click();
            } catch (UiObjectNotFoundException e) {
                // 视图被删除
            }
        }

        // 返回第一级
        getUiDevice().pressBack();
        getUiDevice().pressBack();

        // 退出电子邮件
        getUiDevice().pressBack();
    }
}
