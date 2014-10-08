package com.ztemt.test.platform.data;

import java.util.List;

public abstract class TextData {

    public int getInt(String name) {
        return getInt(name, 0);
    }

    public long getLong(String name) {
        return getLong(name, 0);
    }

    public String getString(String name) {
        return getString(name, "");
    }

    public double getDouble(String name) {
        return getDouble(name, 0);
    }

    public boolean getBoolean(String name) {
        return getBoolean(name, false);
    }

    public abstract int getInt(String name, int defValue);

    public abstract void putInt(String name, int value);

    public abstract long getLong(String name, long defValue);

    public abstract void putLong(String name, long value);

    public abstract String getString(String name, String defValue);

    public abstract void putString(String name, String value);

    public abstract double getDouble(String name, double defValue);

    public abstract void putDouble(String name, double value);

    public abstract boolean getBoolean(String name, boolean defValue);

    public abstract void putBoolean(String name, boolean value);

    public abstract List<TextData> getArray(String name);

    public abstract void putArray(String name, List<TextData> value);
}
