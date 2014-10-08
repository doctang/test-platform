package com.ztemt.test.perf;

import android.os.SystemProperties;
import android.text.TextUtils;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;

/**
 * 拨号盘到呼出界面时间
 * @author 0016001973
 *
 */
public class DialerTest extends PerfTest {

    public void test() throws UiObjectNotFoundException {
        // 进入拨号盘
        Utils.exec("am start --user 0 -W -a android.intent.action.MAIN "
                + "-c android.intent.category.LAUNCHER "
                + "-n com.android.contacts/.DialerActivity");

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
        dial.click();

        // 开始时间
        long startTime = System.currentTimeMillis();

        // 等待通话界面出现
        UiObject validation1 = new UiObject(new UiSelector().packageName(
                "com.android.incallui"));
        assertTrue("Unable to detect incallui", validation1.waitForExists(10000));

        // 结束时间
        long endTime = System.currentTimeMillis();

        // 打印拨号时长
        System.err.println(endTime - startTime);

        // 点击挂断结束通话
        UiObject end = new UiObject(new UiSelector().resourceId(
                "com.android.incallui:id/endButton"));
        end.click();
    }

    private String getOperatorNumber() {
        String operator = SystemProperties.get("gsm.operator.numeric");
        String phoneNumber = null;

        if ("46000".equals(operator) || "46002".equals(operator)
                || "46007".equals(operator)) {
            phoneNumber = "10086";
        } else if ("46001".equals(operator)) {
            phoneNumber = "10010";
        } else if ("46003".equals(operator)) {
            phoneNumber = "10000";
        } else {
            phoneNumber = "";
        }

        return phoneNumber;
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
