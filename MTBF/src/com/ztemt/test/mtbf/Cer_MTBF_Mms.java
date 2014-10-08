package com.ztemt.test.mtbf;

import android.widget.CheckedTextView;
import android.widget.TextView;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;

public class Cer_MTBF_Mms extends Cer_MTBF_Sms {

    @Override
    protected void newMessage() throws UiObjectNotFoundException {
        super.newMessage();

        // 点击插入附件按钮
        UiObject insert = new UiObject(new UiSelector().resourceId(
                "com.android.contacts:id/insert_attachment"));
        insert.click();

        // 点击铃音
        UiScrollable grid = new UiScrollable(new UiSelector().resourceId(
                "com.android.contacts:id/option_panel_zone"));
        grid.setAsHorizontalList();
        grid.waitForExists(60000);
        UiObject ringer = grid.getChildByText(new UiSelector().className(
                TextView.class), "铃音");
        ringer.click();

        // 选择纯真的爱
        UiObject item = new UiObject(new UiSelector().className(
                CheckedTextView.class).text("纯真的爱"));
        item.click();

        // 点击确定
        UiObject ok = new UiObject(new UiSelector().resourceId("android:id/button1"));
        ok.click();
    }
}
