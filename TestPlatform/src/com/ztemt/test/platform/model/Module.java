package com.ztemt.test.platform.model;

import com.ztemt.test.platform.data.TextData;

public class Module {

    /** 脚本  */
    public static final int TYPE_SCRIPT = 0;

    /** APK */
    public static final int TYPE_APK = 1;

    /** Robotium */
    public static final int TYPE_ROBOTIUM = 2;

    /** UiAutomator */
    public static final int TYPE_UIAUTOMATOR = 3;

    /** 模块名称字段 */
    public static final String NAME = "name";

    /** 模块类型字段 */
    public static final String TYPE = "type";

    /** 模块版本字段 */
    public static final String VERSION = "version";

    /** 模块描述字段 */
    public static final String DESCRIPTION = "description";

    private TextData mTextData;

    public Module(TextData data) {
        mTextData = data;
    }

    public String getName() {
        return mTextData.getString(NAME);
    }

    public void setName(String name) {
        mTextData.putString(NAME, name);
    }

    public int getType() {
        return mTextData.getInt(TYPE);
    }

    public void setType(int type) {
        mTextData.putInt(TYPE, type);
    }

    public String getVersion() {
        return mTextData.getString(VERSION);
    }

    public void setVersion(String version) {
        mTextData.putString(VERSION, version);
    }

    public String getDescription() {
        return mTextData.getString(DESCRIPTION);
    }

    public void setDescription(String description) {
        mTextData.putString(DESCRIPTION, description);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null) {
            return false;
        } else if (o instanceof Module) {
            return ((Module) o).getName() == getName();
        } else {
            return false;
        }
    }
}
