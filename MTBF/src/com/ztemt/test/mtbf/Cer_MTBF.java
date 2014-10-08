package com.ztemt.test.mtbf;

import android.util.Log;

import com.android.uiautomator.core.Configurator;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.core.UiWatcher;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

public abstract class Cer_MTBF extends UiAutomatorTestCase implements UiWatcher {

    @Override
    public boolean checkForCondition() {
        UiObject message = new UiObject(new UiSelector().resourceId(
                "android:id/message").textStartsWith("很抱歉"));
        UiObject security = new UiObject(new UiSelector().resourceId(
                "android:id/alertTitle").text("权限"));
        if (message.exists()) {
            try {
                Log.e(getClass().getSimpleName(), message.getText());
            } catch (UiObjectNotFoundException e) {
                e.printStackTrace();
            }
            UiObject ok = new UiObject(new UiSelector().resourceId(
                    "android:id/button1"));
            try {
                ok.click();
            } catch (UiObjectNotFoundException e) {
                e.printStackTrace();
            }
            return true;
        } else if (security.exists()) {
            UiObject remember = new UiObject(new UiSelector().resourceId(
                    "android:id/permission_remember_choice_checkbox"));
            try {
                if (!remember.isChecked()) {
                    remember.click();
                }
            } catch (UiObjectNotFoundException e) {
                e.printStackTrace();
            }
            UiObject allow = new UiObject(new UiSelector().resourceId(
                    "android:id/button1"));
            try {
                allow.click();
            } catch (UiObjectNotFoundException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        getUiDevice().registerWatcher("mtbf", this);

        Configurator conf = Configurator.getInstance();
        conf.setWaitForSelectorTimeout(15000);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        getUiDevice().removeWatcher("mtbf");
    }

    protected void loop(final int count) throws UiObjectNotFoundException {
        for (int i = 1; i <= count; i++) {
            Log.i(getClass().getSimpleName(), String.format("%s/%s", i, count));
            execute();
        }
    }

    protected void longClick(UiObject uiObject) throws UiObjectNotFoundException {
        int x = uiObject.getBounds().centerX();
        int y = uiObject.getBounds().centerY();
        getUiDevice().swipe(x, y, x, y, 120);
    }

    protected abstract void execute() throws UiObjectNotFoundException;
}
