package com.ztemt.test.platform;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;

public class ConnectionPreference extends PreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String KEY_USER_NAME = "user_name";

    private EditTextPreference mUserName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_connection);

        mUserName = (EditTextPreference) findPreference(KEY_USER_NAME);
        mUserName.setText(Registration.getUserName(getActivity()));
        mUserName.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mUserName.setSummary(Registration.getUserName(getActivity()));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mUserName) {
            Registration.setUserName(newValue.toString());
            mUserName.setSummary(Registration.getUserName(getActivity()));
        }
        return true;
    }
}
