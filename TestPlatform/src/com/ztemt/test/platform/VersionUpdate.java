package com.ztemt.test.platform;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;

public class VersionUpdate {

    private static final String TAG = "VersionUpdate";
    private Context mContext;

    private VersionUpdateListener mListener;

    private class QueryVersionTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String url = mContext.getString(R.string.update_url, "current.txt");
            return getHttpResult(url);
        }

        @Override
        protected void onPostExecute(String version) {
            if (version != null && version.compareTo(getVersion()) > 0) {
                new QueryChangesTask().execute(version);
            } else if (mListener != null) {
                mListener.onVersionUpdate(null);
            }
        }
    }

    private class QueryChangesTask extends AsyncTask<String, Void, String> {

        private String mVersion;

        @Override
        protected String doInBackground(String... params) {
            mVersion = params[0];
            String path = String.format("%s/%s", mVersion, "content.txt");
            String url = mContext.getString(R.string.update_url, path);
            return getHttpResult(url);
        }

        @Override
        protected void onPostExecute(String changes) {
            showConfirmDialog(mVersion, changes);
        }
    }

    public VersionUpdate(Context context) {
        mContext = context;
    }

    public interface VersionUpdateListener {
        public void onVersionUpdate(String version);
    }

    public void setVersionUpdateListener(VersionUpdateListener listener) {
        mListener = listener;
    }

    public String getVersion() {
        String version = "1.0";
        try {
            version = mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            // Unknown version
        }
        return version;
    }

    public void queryVersion() {
        new QueryVersionTask().execute();
    }

    public void startInstallVersion(String version) {
        Intent intent = new Intent(mContext, VersionService.class);
        intent.putExtra("version", version);
        mContext.startService(intent);
    }

    private void showConfirmDialog(final String version, String changes) {
        AlertDialog.Builder b = new AlertDialog.Builder(mContext);
        b.setTitle(R.string.version_update);
        b.setMessage(mContext.getString(R.string.version_new, version, changes));
        b.setCancelable(false);
        b.setPositiveButton(R.string.update, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mListener != null) {
                    mListener.onVersionUpdate(version);
                }
            }
        });
        b.setNegativeButton(android.R.string.cancel, null);
        b.create().show();
    }

    private String getHttpResult(String url) {
        try {
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = new DefaultHttpClient().execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                return EntityUtils.toString(response.getEntity(), "GBK");
            } else {
                Log.e(TAG, "http status code = " + statusCode);
            }
        } catch (IOException e) {
            Log.e(TAG, "response", e);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "response", e);
        }
        return null;
    }
}
