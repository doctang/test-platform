package com.ztemt.test.platform.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;

public class FileUtils {

    @SuppressLint("SdCardPath")
    public static final File DIR = new File("/data/data/com.ztemt.apps/files");

    public static void init(Context context) {
        mkdirs(DIR);
        copy(context.getPackageCodePath(),
                getFileStreamPath(context.getPackageName() + ".apk"));
        copy(context.getAssets(), "apps");
    }

    public static void chmod(File file) {
        if (file.isDirectory()) {
            file.setExecutable(true, false);
            file.setReadable(true, false);
            file.setWritable(true);
        } else if (file.isFile()) {
            file.setExecutable(false);
            file.setReadable(true, false);
            file.setWritable(true);
        }
    }

    public static void mkdir(File file) {
        file.mkdir();
        chmod(file);
    }

    public static void mkdirs(File file) {
        List<File> files = new ArrayList<File>();
        File dir = file;

        while (!dir.exists()) {
            files.add(dir);
            dir = dir.getParentFile();
        }

        file.mkdirs();
        for (File f : files) {
            chmod(f);
        }
    }

    public static File getFileStreamPath(String name) {
        return new File(DIR, name);
    }

    public static boolean copy(InputStream is, File t) {
        return copy(is, t, false);
    }

    public static boolean copy(InputStream is, File t, boolean executable) {
        FileOutputStream os = null;
        byte[] buffer = new byte[1024];
        int size = 0;

        try {
            os = new FileOutputStream(t);
            while ((size = is.read(buffer)) != -1) {
                os.write(buffer, 0, size);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Set the file permission
            t.setExecutable(executable, false);
            t.setReadable(true, false);
            t.setWritable(true);
        }
        return true;
    }

    public static boolean copy(String s, File t) {
        return copy(new File(s), t, false);
    }

    public static boolean copy(File s, File t) {
        return copy(s, t, false);
    }

    public static boolean copy(File s, File t, boolean executable) {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;

        try {
            fi = new FileInputStream(s);
            fo = new FileOutputStream(t);
            in = fi.getChannel();
            out = fo.getChannel();
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fi != null) {
                    fi.close();
                }
                if (in != null) {
                    in.close();
                }
                if (fo != null) {
                    fo.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Set the file permission
            t.setExecutable(executable, false);
            t.setReadable(true, false);
            t.setWritable(true);
        }
        return true;
    }

    public static void copy(AssetManager assetMgr, String subdir) {
        try {
            String[] assets = assetMgr.list(subdir);
            for (String asset : assets) {
                String name = subdir + File.separator + asset;
                if (asset.endsWith(".apk") || asset.endsWith(".html")) {
                    copy(assetMgr.open(name), getFileStreamPath(asset));
                } else {
                    copy(assetMgr.open(name), getFileStreamPath(asset), true);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(String line, File t) {
        write(new String[] { line }, t);
    }

    public static void write(String[] lines, File t) {
        BufferedWriter bw = null;
        try {
            FileWriter fw = new FileWriter(t, false);
            bw = new BufferedWriter(fw);
            for (String line : lines) {
                bw.write(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Set the file permission
            t.setReadable(true, false);
            t.setWritable(true);
        }
    }

    public static void write(byte[] bytes, File t) {
        FileOutputStream fo = null;
        try {
            fo = new FileOutputStream(t);
            fo.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fo != null) {
                try {
                    fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Set the file permission
            t.setReadable(true, false);
            t.setWritable(true);
        }
    }

    public static void writeObject(Object obj, File t) {
        FileOutputStream fo = null;
        ObjectOutputStream oo = null;

        try {
            fo = new FileOutputStream(t);
            oo = new ObjectOutputStream(fo);
            oo.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (oo != null) {
                try {
                    oo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fo != null) {
                try {
                    fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Set the file permission
            t.setReadable(true, false);
            t.setWritable(true);
        }
    }

    public static Object readObject(File t) {
        FileInputStream fi = null;
        ObjectInputStream oi = null;

        try {
            fi = new FileInputStream(t);
            oi = new ObjectInputStream(fi);
            return oi.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (oi != null) {
                try {
                    oi.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fi != null) {
                try {
                    fi.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static String readLine(InputStream is) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = "";

        try {
            line = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    public static String readLine(File file) {
        String line = "";

        try {
            line = readLine(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return line;
    }

    public static List<File> listFile(File dir) {
        List<File> list = new ArrayList<File>();
        File[] files = dir.listFiles();
        for (int i = 0; files != null && i < files.length; i++) {
            File f = files[i];
            if (f.isFile()) {
                list.add(f);
            } else if (f.isDirectory()) {
                list.addAll(listFile(f));
            }
        }
        return list;
    }

    public static List<File> listDir(File dir) {
        List<File> list = new ArrayList<File>();
        File[] files = dir.listFiles();
        for (int i = 0; files != null && i < files.length; i++) {
            File f = files[i];
            if (f.isDirectory()) {
                list.add(f);
                list.addAll(listDir(f));
            }
        }
        return list;
    }

    public static void delete(List<File> files) {
        for (File file : files) {
            file.delete();
        }
    }

    public static boolean zip(File resource, File zipfile) {
        final int bufferSize = 1024 * 1024;
        byte buffer[] = new byte[bufferSize];
        int size = 0;

        BufferedInputStream in = null;
        ZipOutputStream out = null;

        try {
            out = new ZipOutputStream(new BufferedOutputStream(
                    new FileOutputStream(zipfile), bufferSize));
            in = new BufferedInputStream(new FileInputStream(resource),
                    bufferSize);
            out.putNextEntry(new ZipEntry(resource.getName()));
            while ((size = in.read(buffer)) != -1) {
                out.write(buffer, 0, size);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.flush();
                    out.closeEntry();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static boolean unzip(File zipfile, File target) {
        InputStream is = null;
        ZipEntry entry;
        File file;

        try {
            ZipFile zf = new ZipFile(zipfile);
            Enumeration<?> entries = zf.entries();
            while (entries.hasMoreElements()) {
                entry = (ZipEntry) entries.nextElement();
                if (entry.isDirectory()) {
                    file = new File(target, entry.getName());
                    file.mkdir();
                    chmod(file);
                } else {
                    is = zf.getInputStream(entry);
                    file = new File(target, entry.getName());
                    if (file.getName().endsWith(".sh")) {
                        copy(is, file, true);
                    } else {
                        copy(is, file);
                    }
                }
            }
            return true;
        } catch (ZipException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
