package com.ztemt.test.mtbf;

import com.android.uiautomator.core.UiObjectNotFoundException;

/**
 * 添加闹铃
 * 循环次数：5
 * @author 0016001973
 *
 */
public class Cer_MTBF_13 extends Cer_MTBF_Alarm implements UiAutomatorTestable {

    @Override
    public void test() throws UiObjectNotFoundException {
        loop(5);
    }

    @Override
    protected void execute() throws UiObjectNotFoundException {
        super.execute();

        // 清空闹钟
        clearAlarm();

        // 添加闹钟
        addAlarm();

        // 清空闹钟
        clearAlarm();

        // 退出时间管理
        getUiDevice().pressHome();
    }
}
