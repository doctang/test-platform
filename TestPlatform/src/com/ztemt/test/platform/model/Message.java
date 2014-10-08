package com.ztemt.test.platform.model;

import java.util.List;

import com.ztemt.test.platform.data.TextData;
import com.ztemt.test.platform.data.TextDataFactory;

import android.os.Parcel;
import android.os.Parcelable;

public class Message implements Parcelable {

    /** 注册连接 */
    public static final int TYPE_REGISTER = 6001;

    /** 心跳 */
    public static final int TYPE_HEARTBEAT = 6002;

    /** 任务发布 */
    public static final int TYPE_TASK_PUBLISH = 6003;

    /** 任务取消 */
    public static final int TYPE_TASK_CANCEL = 6004;

    /** 更新状态 */
    public static final int TYPE_UPDATE_STATUS = 6005;

    /** 消息标识字段 */
    public static final String ID = "id";

    /** 消息类型字段 */
    public static final String TYPE = "type";

    /** 消息时间戳字段 */
    public static final String TIMESTAMP = "timestamp";

    private TextData mTextData;

    public Message(TextData data) {
        mTextData = data;
    }

    public Message(String text) {
        mTextData = TextDataFactory.create(text);
    }

    public Message(int type) {
        mTextData = TextDataFactory.create();
        setType(type);
    }

    public long getId() {
        return mTextData.getInt(ID);
    }

    public void setId(long id) {
        mTextData.putLong(ID, id);
    }

    public int getType() {
        return mTextData.getInt(TYPE);
    }

    public void setType(int type) {
        mTextData.putInt(TYPE, type);
    }

    public long getTimestamp() {
        return mTextData.getLong(TIMESTAMP);
    }

    public void setTimestamp(long timestamp) {
        mTextData.putLong(TIMESTAMP, timestamp);
    }

    public int getIntExtra(String name) {
        return mTextData.getInt(name);
    }

    public void setIntExtra(String name, int value) {
        mTextData.putInt(name, value);
    }

    public long getLongExtra(String name) {
        return mTextData.getLong(name);
    }

    public void setLongExtra(String name, long value) {
        mTextData.putLong(name, value);
    }

    public String getStringExtra(String name) {
        return mTextData.getString(name);
    }

    public void setStringExtra(String name, String value) {
        mTextData.putString(name, value);
    }

    public double getDoubleExtra(String name) {
        return mTextData.getDouble(name);
    }

    public void setDoubleExtra(String name, double value) {
        mTextData.putDouble(name, value);
    }

    public boolean getBooleanExtra(String name) {
        return mTextData.getBoolean(name);
    }

    public void setBooleanExtra(String name, boolean value) {
        mTextData.putBoolean(name, value);
    }

    public List<TextData> getArrayExtra(String name) {
        return mTextData.getArray(name);
    }

    public void setArrayExtra(String name, List<TextData> value) {
        mTextData.putArray(name, value);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null) {
            return false;
        } else if (o instanceof Message) {
            return ((Message) o).getId() == getId();
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return mTextData.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTextData.toString());
    }

    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {

        @Override
        public Message createFromParcel(Parcel source) {
            return new Message(source.readString());
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}
