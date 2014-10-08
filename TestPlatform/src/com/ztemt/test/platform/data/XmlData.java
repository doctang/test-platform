package com.ztemt.test.platform.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class XmlData extends TextData {

    private Map<String, String> mMap = new HashMap<String, String>();

    public XmlData(String xml) {
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(new StringReader(xml));
            fillData(parser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public XmlData(InputStream is) {
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(is, HTTP.UTF_8);
            fillData(parser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public XmlData(File file) {
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(new FileInputStream(file), HTTP.UTF_8);
            fillData(parser);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getInt(String name, int defValue) {
        if (mMap.containsKey(name)) {
            try {
                return Integer.parseInt(mMap.get(name));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return defValue;
    }

    @Override
    public void putInt(String name, int value) {
        mMap.put(name, String.valueOf(value));
    }

    @Override
    public long getLong(String name, long defValue) {
        if (mMap.containsKey(name)) {
            try {
                return Long.parseLong(mMap.get(name));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return defValue;
    }

    @Override
    public void putLong(String name, long value) {
        mMap.put(name, String.valueOf(value));
    }

    @Override
    public String getString(String name, String defValue) {
        if (mMap.containsKey(name)) {
            return mMap.get(name);
        }
        return defValue;
    }

    @Override
    public void putString(String name, String value) {
        mMap.put(name, value);
    }

    @Override
    public double getDouble(String name, double defValue) {
        if (mMap.containsKey(name)) {
            try {
                return Double.parseDouble(mMap.get(name));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return defValue;
    }

    @Override
    public void putDouble(String name, double value) {
        mMap.put(name, String.valueOf(value));
    }

    @Override
    public boolean getBoolean(String name, boolean defValue) {
        if (mMap.containsKey(name)) {
            return Boolean.parseBoolean(mMap.get(name));
        }
        return defValue;
    }

    @Override
    public void putBoolean(String name, boolean value) {
        mMap.put(name, String.valueOf(value));
    }

    @Override
    public List<TextData> getArray(String name) {
        throw new IllegalStateException("getArray is not implement");
    }

    @Override
    public void putArray(String name, List<TextData> value) {
        throw new IllegalStateException("putArray is not implement");
    }

    private void fillData(XmlPullParser parser) throws XmlPullParserException,
            IOException {
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String nodeName = parser.getName();
            switch (eventType) {
            case XmlPullParser.START_DOCUMENT:
                break;
            case XmlPullParser.START_TAG:
                eventType = parser.next();
                if (parser.getDepth() < 2)
                    continue;
                mMap.put(nodeName, parser.getText());
                break;
            case XmlPullParser.END_TAG:
                break;
            }
            eventType = parser.next();
        }
    }
}
