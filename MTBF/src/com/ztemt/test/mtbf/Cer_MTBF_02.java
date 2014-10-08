package com.ztemt.test.mtbf;

import com.android.uiautomator.core.UiObjectNotFoundException;

/**
 * 发送短信
 * 循环次数：20
 * @author 0016001973
 *
 */
public class Cer_MTBF_02 extends Cer_MTBF_Sms implements
        UiAutomatorTestable {

    @Override
    public void test() throws UiObjectNotFoundException {
        loop(20);
    }

    @Override
    protected void execute() throws UiObjectNotFoundException {
        super.execute();

        // 清空会话
        clearMessage();

        // 新建信息
        newMessage();

        // 发送信息
        sendMessage();

        // 退出信息
        getUiDevice().pressBack();
        getUiDevice().pressBack();
    }
}
