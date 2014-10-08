package com.ztemt.test.platform.model;

import com.ztemt.test.platform.data.TextData;
import com.ztemt.test.platform.data.TextDataFactory;

import android.os.Parcel;

@SuppressWarnings("serial")
public class SystemUpdate extends Task {

    /** 构建版本获取地址字段 */
    public static final String UPDATE_URL = "updateUrl";

    /** 构建版本日期字段 */
    public static final String BUILD_DATE = "buildDate";

    private String mUpdateUrl;

    private String mBuildDate;

    public SystemUpdate() {
        super(TYPE_SYSTEM_UPDATE);
    }

    public SystemUpdate(Parcel in) {
        super(in);
        mUpdateUrl = in.readString();
        mBuildDate = in.readString();
    }

    public SystemUpdate(TextData data) {
        super(data);
        mUpdateUrl = data.getString(UPDATE_URL);
        mBuildDate = data.getString(BUILD_DATE);
    }

    public String getUpdateUrl() {
        return mUpdateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        mUpdateUrl = updateUrl;
    }

    public String getBuildDate() {
        return mBuildDate;
    }

    public void setBuildDate(String buildDate) {
        mBuildDate = buildDate;
    }

    @Override
    public String toString() {
        TextData data = TextDataFactory.create(super.toString());
        data.putString(UPDATE_URL, mUpdateUrl);
        data.putString(BUILD_DATE, mBuildDate);
        return data.toString();
    }
}
