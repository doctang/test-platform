package com.ztemt.test.auto.item;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.util.Log;

import com.ztemt.test.auto.R;

public class FileSystemTest extends BaseTest {

    private static final String LOG_TAG = "FileSystemTest";
    private static final String[] FILE_PARTITIONS = { "/cache", "/data" };
    private static final int[] RAW_RES_IDS = { R.raw.test };

    public FileSystemTest(Context context) {
        super(context);
    }

    @Override
    public void onRun() {
        for (String name : FILE_PARTITIONS) {
            for (int i = 0; i < RAW_RES_IDS.length; i++) {
                if (!verifyFilePartition(RAW_RES_IDS[i], name)) {
                    Log.e(LOG_TAG, "Verify(" + i + ", " + name + ") fail");
                    setFailure();
                    return;
                }
            }
        }
        setSuccess();
    }

    @Override
    public String getTitle() {
        return mContext.getString(R.string.file_system_test);
    }

    private boolean verifyFilePartition(int rawId, String name) {
        String str1 = getFileDigest(rawId);
        File file = new File(name, "test.mp3");
        if (copyFileFromRaw(rawId, file)) {
            String str2 = getFileDigest(file);
            file.delete();
            return str1 != null && str2 != null && str1.equals(str2);
        }

        Log.e(LOG_TAG, "Copy raw file failed");
        return false;
    }

    private boolean copyFileFromRaw(int rawId, File dest) {
        InputStream is = mContext.getResources().openRawResource(rawId);
        OutputStream os = null;
        int length = 1024;
        int size;
        byte[] buffer = new byte[length];

        try {
            os = new FileOutputStream(dest);
            while ((size = is.read(buffer, 0, length)) != -1) {
                os.write(buffer, 0, size);
            }
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (os != null) {
                try {
                    os.close();
                    os = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                    is = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getFileDigest(int rawId) {
        return getFileDigest(mContext.getResources().openRawResource(rawId));
    }

    private String getFileDigest(File file) {
        try {
            return getFileDigest(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    private String getFileDigest(InputStream is) {
        byte[] buffer = new byte[1024];
        DigestInputStream dis = null;

        try {
            dis = new DigestInputStream(is, MessageDigest.getInstance("MD5"));
            while (dis.read(buffer) != -1) {
                /* Nothing to do! */
            }
            BigInteger bi = new BigInteger(1, dis.getMessageDigest().digest());
            return bi.toString(16);
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (IOException e) {
            return null;
        } finally {
            if (dis != null) {
                try {
                    dis.close();
                    dis = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
