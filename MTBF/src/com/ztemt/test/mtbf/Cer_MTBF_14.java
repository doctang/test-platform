package com.ztemt.test.mtbf;

import com.android.uiautomator.core.UiObjectNotFoundException;

/**
 * 删除闹铃
 * 循环次数：5
 * @author 0016001973
 *
 */
public class Cer_MTBF_14 extends Cer_MTBF_Alarm implements UiAutomatorTestable {

    @Override
    public void test() throws UiObjectNotFoundException {
        loop(5);
    }

    @Override
    protected void execute() throws UiObjectNotFoundException {
        super.execute();

        // 添加闹钟
        addAlarm();

        // 清空闹钟
        clearAlarm();

        // 退出时间管理
        getUiDevice().pressHome();
    }
}
