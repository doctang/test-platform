package com.ztemt.test.platform.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class JsonData extends TextData implements Parcelable {

    private static final String TAG = "JsonData";

    private JSONObject mJson;

    public JsonData() {
        mJson = new JSONObject();
    }

    public JsonData(String json) {
        try {
            mJson = new JSONObject(json);
        } catch (JSONException e) {
            mJson = new JSONObject();
        }
    }

    public JsonData(JSONObject json) {
        if (json != null) {
            mJson = json;
        } else {
            mJson = new JSONObject();
        }
    }

    @Override
    public int getInt(String name, int defValue) {
        return mJson.optInt(name, defValue);
    }

    @Override
    public void putInt(String name, int value) {
        try {
            mJson.put(name, value);
        } catch (JSONException e) {
            Log.e(TAG, "putInt", e);
        }
    }

    @Override
    public long getLong(String name, long defValue) {
        return mJson.optLong(name, defValue);
    }

    @Override
    public void putLong(String name, long value) {
        try {
            mJson.put(name, value);
        } catch (JSONException e) {
            Log.e(TAG, "putLong", e);
        }
    }

    @Override
    public String getString(String name, String defValue) {
        return mJson.optString(name, defValue);
    }

    @Override
    public void putString(String name, String value) {
        try {
            mJson.put(name, value);
        } catch (JSONException e) {
            Log.e(TAG, "putString", e);
        }
    }

    @Override
    public double getDouble(String name, double defValue) {
        return mJson.optDouble(name, defValue);
    }

    @Override
    public void putDouble(String name, double value) {
        try {
            mJson.put(name, value);
        } catch (JSONException e) {
            Log.e(TAG, "putDouble", e);
        }
    }

    @Override
    public boolean getBoolean(String name, boolean defValue) {
        return mJson.optBoolean(name, defValue);
    }

    @Override
    public void putBoolean(String name, boolean value) {
        try {
            mJson.put(name, value);
        } catch (JSONException e) {
            Log.e(TAG, "putBoolean", e);
        }
    }

    @Override
    public List<TextData> getArray(String name) {
        List<TextData> list = new ArrayList<TextData>();
        JSONArray array = null;

        try {
            array = new JSONArray(mJson.optString(name, "[]"));
        } catch (JSONException e) {
            Log.e(TAG, "getArray", e);
        }

        for (int i = 0; array != null && i < array.length(); i++) {
            list.add(new JsonData(array.optJSONObject(i)));
        }

        return list;
    }

    @Override
    public void putArray(String name, List<TextData> value) {
        JSONArray array = new JSONArray();
        try {
            for (TextData data : value) {
                array.put(new JSONObject(data.toString()));
            }
            mJson.put(name, array);
        } catch (JSONException e) {
            Log.e(TAG, "putArray", e);
        }
    }

    @Override
    public String toString() {
        return mJson.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(toString());
    }

    public static final Parcelable.Creator<JsonData> CREATOR = new Parcelable.Creator<JsonData>() {

        @Override
        public JsonData createFromParcel(Parcel source) {
            return new JsonData(source.readString());
        }

        @Override
        public JsonData[] newArray(int size) {
            return new JsonData[size];
        }
    };
}
