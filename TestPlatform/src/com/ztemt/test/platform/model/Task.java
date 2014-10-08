package com.ztemt.test.platform.model;

import java.io.Serializable;

import com.ztemt.test.platform.data.TextData;
import com.ztemt.test.platform.data.TextDataFactory;

import android.os.Parcel;

@SuppressWarnings("serial")
public abstract class Task implements Serializable {

    /** 系统升级 */
    public static final int TYPE_SYSTEM_UPDATE = 7001;

    /** 系统测试 */
    public static final int TYPE_SYSTEM_TEST = 7002;

    /** 清除记录 */
    public static final int TYPE_SYSTEM_CLEAR = 7003;

    /** 未开始 */
    public static final int STATUS_NOT_START = 0;

    /** 取消 */
    public static final int STATUS_CANCEL = 1;

    /** 等待 */
    public static final int STATUS_WAIT = 2;

    /** 进行中 */
    public static final int STATUS_GOING = 3;

    /** 失败 */
    public static final int STATUS_FAIL = 4;

    /** 完成 */
    public static final int STATUS_OK = 5;

    /** 任务标识字段 */
    public static final String ID = "id";

    /** 任务类型字段
     * {@link #TYPE_SYSTEM_UPDATE}
     * {@link #TYPE_SYSTEM_TEST}
     * {@link #TYPE_CLEAR}
     */
    public static final String TYPE = "type";

    /** 任务状态字段
     * {@link #STATUS_FAIL}
     * {@link #STATUS_CANCEL}
     * {@link #STATUS_WAIT}
     * {@link #STATUS_OK}
     * {@link #STATUS_GOING}
     * {@link #STATUS_NOT_START}
     */
    public static final String STATUS = "status";

    /** 任务延时字段 */
    public static final String DELAY = "delay";

    /** 任务产生时间戳 */
    public static final String TIMESTAMP = "timestamp";

    private long mId;

    private int mType;

    private int mStatus = STATUS_NOT_START;

    private long mDelay;

    private long mTimestamp;

    public Task(Parcel in) {
        mId = in.readLong();
        mType = in.readInt();
        mStatus = in.readInt();
        mDelay = in.readLong();
        mTimestamp = in.readLong();
    }

    public Task(TextData data) {
        mId = data.getLong(ID);
        mType = data.getInt(TYPE);
        mStatus = data.getInt(STATUS);
        mDelay = data.getLong(DELAY);
        mTimestamp = data.getLong(TIMESTAMP);
    }

    public Task(int type) {
        setType(type);
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public long getDelay() {
        return mDelay;
    }

    public void setDelay(long delay) {
        mDelay = delay;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(long timestamp) {
        mTimestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null) {
            return false;
        } else if (o instanceof Task) {
            return ((Task) o).mId == mId;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        TextData data = TextDataFactory.create();
        data.putLong(ID, mId);
        data.putInt(TYPE, mType);
        data.putInt(STATUS, mStatus);
        data.putLong(DELAY, mDelay);
        data.putLong(TIMESTAMP, mTimestamp);
        return data.toString();
    }
}
