package com.ztemt.test.platform.model;

import com.ztemt.test.platform.data.TextData;
import com.ztemt.test.platform.data.TextDataFactory;

import android.os.Parcel;

@SuppressWarnings("serial")
public class SystemTest extends Task {

    /** 测试名称字段 */
    public static final String NAME = "name";

    /** 测试所需模块名称字段 */
    public static final String MODULE = "module";

    /** 测试参数字段 */
    public static final String EXTRAS = "extras";

    /** 测试是否可等待字段 ，如普通APK需要等待测试完成*/
    public static final String WAITABLE = "waitable";

    /** 测试进程号字段，用户结束进程 */
    public static final String PID = "pid";

    private String mName;

    private String mModule;

    private String mExtras;

    private boolean mWaitable;

    private int mPid;

    public SystemTest() {
        super(TYPE_SYSTEM_TEST);
    }

    public SystemTest(Parcel in) {
        super(in);
        mName = in.readString();
        mModule = in.readString();
        mExtras = in.readString();
        mWaitable = in.readInt() == 1;
        mPid = in.readInt();
    }

    public SystemTest(TextData data) {
        super(data);
        mName = data.getString(NAME);
        mModule = data.getString(MODULE);
        mExtras = data.getString(EXTRAS);
        mWaitable = data.getBoolean(WAITABLE);
        mPid = data.getInt(PID);
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getModule() {
        return mModule;
    }

    public void setModule(String module) {
        mModule = module;
    }

    public String getExtras() {
        return mExtras;
    }

    public void setExtras(String extras) {
        mExtras = extras;
    }

    public boolean isWaitable() {
        return mWaitable;
    }

    public void setWaitable(boolean waitable) {
        mWaitable = waitable;
    }

    public int getPid() {
        return mPid;
    }

    public void setPid(int pid) {
        mPid = pid;
    }

    @Override
    public String toString() {
        TextData data = TextDataFactory.create(super.toString());
        data.putString(NAME, mName);
        data.putString(MODULE, mModule);
        data.putString(EXTRAS, mExtras);
        data.putBoolean(WAITABLE, mWaitable);
        data.putInt(PID, mPid);
        return data.toString();
    }
}
