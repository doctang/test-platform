package com.ztemt.test.mtbf;

import com.android.uiautomator.core.UiObjectNotFoundException;

/**
 * 发送带有附件的邮件
 * 循环次数：5
 * @author 0016001973
 *
 */
public class Cer_MTBF_06 extends Cer_MTBF_Email implements UiAutomatorTestable {

    @Override
    public void test() throws UiObjectNotFoundException {
        loop(5);
    }

    @Override
    protected void execute() throws UiObjectNotFoundException {
        super.execute();

        // 发送邮件
        sendEmail(true);

        // 清空邮箱
        clearEmail();

        // 退出电子邮件
        getUiDevice().pressBack();
    }
}
