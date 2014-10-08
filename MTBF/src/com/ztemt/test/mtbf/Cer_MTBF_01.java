package com.ztemt.test.mtbf;

import android.text.TextUtils;
import android.widget.TextView;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;

/**
 * 从拨号盘发起呼叫
 * 循环次数：100
 * @author 0016001973
 *
 */
public class Cer_MTBF_01 extends Cer_MTBF_Telephony implements
        UiAutomatorTestable {

    @Override
    public void test() throws UiObjectNotFoundException {
        loop(100);
    }

    @Override
    protected void execute() throws UiObjectNotFoundException {
        // 返回主界面
        getUiDevice().pressHome();

        // 进入拨号盘
        UiObject title = new UiObject(new UiSelector().className(
                TextView.class).text("拨号"));
        title.clickAndWaitForNewWindow();

        // 断言当前是联系人模块
        UiObject validation = new UiObject(new UiSelector().packageName(
                "com.android.contacts"));
        assertTrue("Unable to detect contacts", validation.exists());

        // 断言运营商号码不为空
        String operatorNumber = getOperatorNumber();
        assertTrue("Unable to detect operator", !TextUtils.isEmpty(operatorNumber));

        // 拨运营商号码
        dial(operatorNumber);

        // 呼出电话
        UiObject dial = new UiObject(new UiSelector().resourceId(
                "com.android.contacts:id/dial_button"));
        dial.clickAndWaitForNewWindow();

        // 断言当前是通话界面模块
        UiObject validation1 = new UiObject(new UiSelector().packageName(
                "com.android.incallui"));
        assertTrue("Unable to detect incallui", validation1.exists());

        // 等待接通
        sleep(15000);

        // 点击挂断结束通话
        UiObject end = new UiObject(new UiSelector().resourceId(
                "com.android.incallui:id/endButton"));
        end.click();

        // 退出拨号盘
        getUiDevice().pressBack();
    }

    private void dial(String phoneNumber) throws UiObjectNotFoundException {
        String resId = null;

        for (int i = 0; i < phoneNumber.length(); i++) {
            char c = phoneNumber.charAt(i);
            switch (c) {
            case '0':
                resId = "com.android.contacts:id/zero";
                break;
            case '1':
                resId = "com.android.contacts:id/one";
                break;
            case '2':
                resId = "com.android.contacts:id/two";
                break;
            case '3':
                resId = "com.android.contacts:id/three";
                break;
            case '4':
                resId = "com.android.contacts:id/four";
                break;
            case '5':
                resId = "com.android.contacts:id/five";
                break;
            case '6':
                resId = "com.android.contacts:id/six";
                break;
            case '7':
                resId = "com.android.contacts:id/seven";
                break;
            case '8':
                resId = "com.android.contacts:id/eight";
                break;
            case '9':
                resId = "com.android.contacts:id/nine";
                break;
            default:
                resId = "";
                break;
            }

            UiObject number = new UiObject(new UiSelector().resourceId(resId));
            number.click();
        }
    }
}
