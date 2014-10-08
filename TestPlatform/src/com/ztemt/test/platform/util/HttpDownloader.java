package com.ztemt.test.platform.util;

import static android.app.DownloadManager.ACTION_DOWNLOAD_COMPLETE;
import static android.app.DownloadManager.ACTION_NOTIFICATION_CLICKED;
import static android.app.DownloadManager.COLUMN_LOCAL_FILENAME;
import static android.app.DownloadManager.COLUMN_STATUS;
import static android.app.DownloadManager.EXTRA_DOWNLOAD_ID;
import static android.app.DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS;
import static android.app.DownloadManager.STATUS_SUCCESSFUL;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.ztemt.test.platform.R;

public class HttpDownloader  {

    private Context mContext;

    private DownloadManager mDM;
    private long mDownloadId;

    private HttpDownloadListener mListener;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                handleDownloadComplete(intent.getLongExtra(EXTRA_DOWNLOAD_ID, 0));
            } else if (ACTION_NOTIFICATION_CLICKED.equals(action)) {
                mDM.remove(intent.getLongArrayExtra(EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS));
                if (mListener != null) {
                    mListener.onDownloadComplete(null);
                }
            }
        }
    };

    private void handleDownloadComplete(long downloadId) {
        if (downloadId == mDownloadId) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            Cursor c = mDM.query(query);
            File file = null;

            if (c.moveToFirst()) {
                int status = c.getInt(c.getColumnIndex(COLUMN_STATUS));
                if (status == STATUS_SUCCESSFUL) {
                    int index = c.getColumnIndex(COLUMN_LOCAL_FILENAME);
                    file = new File(c.getString(index));
                }
                if (mListener != null) {
                    mListener.onDownloadComplete(file);
                }
            }
        }
    }

    public HttpDownloader(Context context) {
        mDM = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        mContext = context;
    }

    public void register() {
        IntentFilter filter = new IntentFilter(ACTION_DOWNLOAD_COMPLETE);
        filter.addAction(ACTION_NOTIFICATION_CLICKED);
        mContext.registerReceiver(mReceiver, filter);
    }

    public void unregister() {
        mContext.unregisterReceiver(mReceiver);
    }

    public void download(String url, String fileName, String title, String text) {
        DownloadTask downloadTask = new DownloadTask(title, text);
        downloadTask.execute(url, fileName);
    }

    public void download(String url, String fileName) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Uri uri = Uri.parse(url);
    
            DownloadManager.Request r = new DownloadManager.Request(uri);
            r.setAllowedNetworkTypes(Request.NETWORK_WIFI);
            r.setAllowedOverRoaming(false);
    
            // set mime type
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            String mimeType = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap
                    .getFileExtensionFromUrl(url));
            r.setMimeType(mimeType);
    
            // set in notification
            r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
            r.setVisibleInDownloadsUi(true);
    
            // sdcard
            if (TextUtils.isEmpty(fileName)) fileName = uri.getLastPathSegment();
            r.setDestinationInExternalFilesDir(mContext, File.separator, fileName);
            r.setTitle(fileName);
    
            // start download
            mDownloadId = mDM.enqueue(r);
        } else if (mListener != null) {
            SystemClock.sleep(1000);
            mListener.onDownloadComplete(null);
        }
    }

    public void setDownloadListener(HttpDownloadListener listener) {
        mListener = listener;
    }

    public interface HttpDownloadListener {
        /* file is null when download fail */
        public void onDownloadComplete(File file);
    }

    public class DownloadTask extends AsyncTask<String, Integer, File> {

        private static final int MAX_BUFFER_SIZE = 1024;

        private NotificationManager mNotifyManager;
        private Builder mBuilder;

        public DownloadTask(String title, String text) {
            mNotifyManager = (NotificationManager) mContext.getSystemService(
                    Context.NOTIFICATION_SERVICE);
            mBuilder = new NotificationCompat.Builder(mContext);
            mBuilder.setSmallIcon(R.drawable.ic_launcher);
            mBuilder.setContentTitle(title);
            mBuilder.setContentText(text);
            mBuilder.setAutoCancel(false);
            mBuilder.setOnlyAlertOnce(true);
            mBuilder.setOngoing(true);
            mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        }

        @Override
        protected File doInBackground(String... params) {
            String fileName = params[1];
            if (TextUtils.isEmpty(fileName)) {
                fileName = Uri.parse(params[0]).getLastPathSegment();
            }

            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                double fileSize = conn.getContentLength();
                ByteArrayOutputStream os = new ByteArrayOutputStream((int) fileSize);
                conn.connect();
                InputStream is = conn.getInputStream();
                double downloaded = 0;
                byte[] buffer;
                int progress = 0;

                while (true) {
                    if (fileSize - downloaded > MAX_BUFFER_SIZE) {
                        buffer = new byte[MAX_BUFFER_SIZE];
                    } else {
                        buffer = new byte[(int) (fileSize - downloaded)];
                    }

                    int read = is.read(buffer);
                    if (read == -1) {
                        publishProgress(100);
                        break;
                    }

                    os.write(buffer, 0, read);
                    downloaded += read;

                    int p = (int) ((downloaded / fileSize) * 100);
                    if (progress != p) {
                        progress = p;
                        publishProgress(progress);
                    }
                }

                File file = FileUtils.getFileStreamPath(fileName);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(os.toByteArray());
                fos.close();

                FileUtils.chmod(file);
                return file;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // Start download notification
            mBuilder.setProgress(100, values[0], false);
            mNotifyManager.notify(R.string.module_update_title, mBuilder.build());
        }

        @Override
        protected void onPostExecute(File result) {
            // Stop download notification
            mNotifyManager.cancel(R.string.module_update_title);

            if (mListener != null) {
                mListener.onDownloadComplete(result);
            }
        }
    }
}
