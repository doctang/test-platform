package com.ztemt.test.platform;

import com.ztemt.test.platform.util.DeviceUtils;

import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.widget.Toast;

public class AboutPreference extends PreferenceFragment implements
        VersionUpdate.VersionUpdateListener {

    private static final String KEY_ADDRESS = "address";
    private static final String KEY_MODEL = "model";
    private static final String KEY_DISPLAY = "display";
    private static final String KEY_BUILD = "build";
    private static final String KEY_VERSION = "version";
    private static final String KEY_UPDATE = "update";

    private Preference mAddress;
    private Preference mModel;
    private Preference mDisplay;
    private Preference mBuild;
    private Preference mVersion;
    private Preference mUpdate;

    private VersionUpdate mVersionUpdate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_about);

        mVersionUpdate = new VersionUpdate(getActivity());
        mVersionUpdate.setVersionUpdateListener(this);

        mAddress = findPreference(KEY_ADDRESS);
        mAddress.setSummary(DeviceUtils.getWifiMacAddress(getActivity()));
        mModel = findPreference(KEY_MODEL);
        mModel.setSummary(Build.MODEL);
        mDisplay = findPreference(KEY_DISPLAY);
        mDisplay.setSummary(Build.DISPLAY);
        mBuild = findPreference(KEY_BUILD);
        mBuild.setSummary(DeviceUtils.getBuildDate2());
        mVersion = findPreference(KEY_VERSION);
        mVersion.setSummary(mVersionUpdate.getVersion());
        mUpdate = findPreference(KEY_UPDATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mVersionUpdate.setVersionUpdateListener(null);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        if (preference == mUpdate) {
            mVersionUpdate.queryVersion();
        }
        return true;
    }

    @Override
    public void onVersionUpdate(String version) {
        if (version != null) {
            mVersionUpdate.startInstallVersion(version);
        } else {
            Toast.makeText(getActivity(), R.string.version_uptodate,
                    Toast.LENGTH_SHORT).show();
        }
    }
}
