package com.ztemt.test.platform;

import com.ztemt.test.platform.R;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;

public class SettingsPreference extends PreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String KEY_NEVER_UPDATE = "never_update";
    private static final String KEY_NEVER_TEST = "never_test";

    private CheckBoxPreference mNeverUpdate;
    private CheckBoxPreference mNeverTest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_settings);

        mNeverUpdate = (CheckBoxPreference) findPreference(KEY_NEVER_UPDATE);
        mNeverUpdate.setOnPreferenceChangeListener(this);
        mNeverTest = (CheckBoxPreference) findPreference(KEY_NEVER_TEST);
        mNeverTest.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mNeverUpdate.setChecked(SystemUpdateHandler.isNeverUpdate());
        mNeverTest.setChecked(SystemTestHandler.isNeverTest());
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mNeverUpdate) {
            SystemUpdateHandler.setNeverUpdate((Boolean) newValue);
        } else if (preference == mNeverTest) {
            SystemTestHandler.setNeverTest((Boolean) newValue);
        }
        return true;
    }
}
