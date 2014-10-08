package com.ztemt.test.platform.data;

public class TextDataFactory {

    public static TextData create(String text) {
        return new JsonData(text);
    }

    public static TextData create() {
        return new JsonData();
    }
}
