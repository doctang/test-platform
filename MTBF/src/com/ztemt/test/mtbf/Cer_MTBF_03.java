package com.ztemt.test.mtbf;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;

/**
 * 打开短信
 * 循环次数：20
 * @author 0016001973
 *
 */
public class Cer_MTBF_03 extends Cer_MTBF_Sms implements
        UiAutomatorTestable {

    @Override
    public void test() throws UiObjectNotFoundException {
        loop(20);
    }

    @Override
    protected void execute() throws UiObjectNotFoundException {
        super.execute();

        // 新建信息
        newMessage();

        // 发送信息
        sendMessage();

        // 选择第一条会话
        UiObject list = new UiObject(new UiSelector().resourceId("android:id/list"));
        UiObject item = list.getChild(new UiSelector().index(0));
        item.click();

        // 退出信息
        getUiDevice().pressBack();
    }
}
