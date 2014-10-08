package com.ztemt.test.platform;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.Build;
import android.os.SystemProperties;
import android.util.Log;

import com.ztemt.test.platform.data.TextData;
import com.ztemt.test.platform.data.TextDataFactory;
import com.ztemt.test.platform.util.DeviceUtils;

public class Registration {

    private static final String TAG = "Register";
    private static final String KEY_USER_NAME = "persist.sys.testplat.username";
    private static final String IMEI = "imei";
    private static final String PLATFORM = "platform";
    private static final String VERSION = "version";
    private static final String MODEL = "model";
    private static final String BASEBAND = "baseband";
    private static final String KERNEL = "kernel";
    private static final String BUILD = "build";
    private static final String WIFI_MAC = "mac";
    private static final String USER_NAME = "username";
    private static final String BUILD_DATE = "buildtime";
    private static final String CLIENT_ID = "clientId";
    private static final String RESULT = "result";
    private static final String WEBSOCKET = "websocket";
    private static final String WIFI_ADB = "wifiAdb";

    private Context mContext;

    public Registration(Context context) {
        mContext = context;
    }

    public RegisterInfo register(String url) {
        RegisterInfo registerInfo = new RegisterInfo();
        HttpPost request = new HttpPost(url);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(IMEI, DeviceUtils
                .getDeviceId(mContext)));
        params.add(new BasicNameValuePair(PLATFORM, "Android"));
        params.add(new BasicNameValuePair(VERSION, Build.VERSION.RELEASE));
        params.add(new BasicNameValuePair(MODEL, Build.MODEL));
        params.add(new BasicNameValuePair(BASEBAND, Build.getRadioVersion()));
        params.add(new BasicNameValuePair(KERNEL, DeviceUtils
                .getFormattedKernelVersion()));
        params.add(new BasicNameValuePair(BUILD, Build.DISPLAY));
        params.add(new BasicNameValuePair(WIFI_MAC, DeviceUtils
                .getWifiMacAddress(mContext)));
        params.add(new BasicNameValuePair(USER_NAME, getUserName(mContext)));
        params.add(new BasicNameValuePair(BUILD_DATE, DeviceUtils
                .getBuildDate()));
        params.add(new BasicNameValuePair(WIFI_ADB, DeviceUtils
                .getWifiIpAddress(mContext) + ":5555"));

        try {
            request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse response = new DefaultHttpClient().execute(request);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(response.getEntity());
                TextData data = TextDataFactory.create(result);
                registerInfo.clientId = data.getString(CLIENT_ID);
                registerInfo.result = data.getInt(RESULT);
                registerInfo.websocket = data.getString(WEBSOCKET);
            } else {
                Log.e(TAG, "Error response code: " + statusCode);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return registerInfo;
    }

    public static String getUserName(Context context) {
        return SystemProperties.get(KEY_USER_NAME,
                context.getString(R.string.user_name_def));
    }

    public static void setUserName(String userName) {
        SystemProperties.set(KEY_USER_NAME, userName);
    }

    class RegisterInfo {
        String clientId = "";
        String websocket = "";
        int result = 0;

        String getWebSocketUrl() {
            return String.format("%s?%s=%s", websocket, CLIENT_ID, clientId);
        }
    }
}
