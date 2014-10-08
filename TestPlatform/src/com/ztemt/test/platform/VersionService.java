package com.ztemt.test.platform;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.SystemProperties;
import android.provider.Settings;

import com.ztemt.test.platform.util.HttpDownloader;
import com.ztemt.test.platform.util.HttpDownloader.HttpDownloadListener;

public class VersionService extends IntentService implements HttpDownloadListener {

    public VersionService() {
        super("version");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        downloadUpdate(intent.getStringExtra("version"));
    }

    @Override
    public void onDownloadComplete(File file) {
        if (file != null) {
            setNonMarketAppsAllowed(true);
            installPackage(file);
        }
        stopSelf();
    }

    private void downloadUpdate(String version) {
        String tags = SystemProperties.get("ro.build.tags", "release-keys");
        String path = String.format("%s/%s/%s/TestPlatform.apk", version,
                Build.MODEL, tags);
        String url = getString(R.string.update_url, path);
        if (isFileNotFound(url)) {
            path = String.format("%s/%s/TestPlatform.apk", version, tags);
            url = getString(R.string.update_url, path);
        }
        HttpDownloader downloader = new HttpDownloader(this);
        downloader.setDownloadListener(this);
        downloader.download(url, getPackageName() + ".apk",
                getString(R.string.version_download_title),
                getString(R.string.version_download_text, version));
    }

    private boolean isFileNotFound(String url) {
        HttpPost request = new HttpPost(url.replaceAll(" ", "%20"));
        try {
            HttpResponse response = new DefaultHttpClient().execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            return statusCode == 404;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setNonMarketAppsAllowed(boolean enabled) {
        // Change the system setting
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.INSTALL_NON_MARKET_APPS,
                    enabled ? 1 : 0);
        } else {
            Settings.Global.putInt(getContentResolver(),
                    Settings.Global.INSTALL_NON_MARKET_APPS,
                    enabled ? 1 : 0);
        }
    }

    private void installPackage(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }
}
