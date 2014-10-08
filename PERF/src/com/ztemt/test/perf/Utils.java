package com.ztemt.test.perf;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.xmlpull.v1.XmlPullParser;

import android4me.res.AXMLParser;

public class Utils {

    public static int getHanziCount(String str) {
        int count = 0;
        String regEx = "[\\u4e00-\\u9fa5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        while (m.find()) {
            for (int i = 0; i <= m.groupCount(); i++) {
                count++;
            }
        }
        return count;
    }

    public static void exec(String prog) {
        try {
            Process p = Runtime.getRuntime().exec(prog);
            p.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String[] getApkInfo(File file) {
        String[] info = new String[2];
        AXMLParser parser;
        ZipFile zipfile;
        ZipEntry entry;

        try {
            zipfile = new ZipFile(file);
            entry = zipfile.getEntry("AndroidManifest.xml");
            parser = new AXMLParser(zipfile.getInputStream(entry));
            while (true) {
                int type = parser.next();
                if (type == XmlPullParser.START_TAG) {
                    for (int i = 0; i != parser.getAttributeCount(); i++) {
                        String name = parser.getAttributeName(i);
                        if ("package".equals(name)) {
                            info[0] = parser.getAttributeValueString(i);
                        } else if ("versionName".equals(name)) {
                            info[1] = parser.getAttributeValueString(i);
                        }
                    }
                } else if (type == XmlPullParser.END_DOCUMENT) {
                    break;
                }
            }
            zipfile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return info;
    }

}
