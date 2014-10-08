package com.ztemt.test.auto.item;

import java.util.HashSet;
import java.util.Set;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.ztemt.test.auto.R;

public class ApnTest extends BaseTest {

    private static final Uri CONTENT_URI = Uri
            .parse("content://telephony/carriers");
    private static final Uri PREFERRED_APN_URL = Uri
            .parse("content://telephony/carriers/preferapn");
    private static final String LOG_TAG = "ApnTest";
    private static final String APN_ID = "apn_id";
    private static final String DEFAULT_SORT_ORDER = "name ASC";

    private static final int ID_INDEX = 0;
    private static final int TYPES_INDEX = 1;

    private Set<String> mKeys = new HashSet<String>();
    private String mSelectedKey = null;

    public ApnTest(Context context) {
        super(context);
        fillList();
    }

    @Override
    public void onRun() {
        if (!mKeys.isEmpty()) {
            String key = getUnselectedApnKey();
            if (!TextUtils.isEmpty(key)) {
                setSelectedApnKey(key);
                sleep(30000);
                setSuccess();
            } else {
                setFailure();
            }
        } else {
            setFailure();
        }
    }

    @Override
    public String getTitle() {
        return mContext.getString(R.string.apn_test);
    }

    private String getOperatorNumericSelection() {
        TelephonyManager tm = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        String numeric = tm.getSimOperator();
        String where;
        where = !TextUtils.isEmpty(numeric) ? "numeric=\"" + numeric + "\"" : "";
        Log.d(LOG_TAG, "getOperatorNumericSelection: " + where);
        return where;
    }

    private void fillList() {
        String where = getOperatorNumericSelection();

        if (TextUtils.isEmpty(where)) {
            Log.d(LOG_TAG, "getOperatorNumericSelection is empty");
            return;
        }

        Cursor cursor = mContext.getContentResolver()
                .query(CONTENT_URI, new String[] { "_id", "type" }, where,
                        null, DEFAULT_SORT_ORDER);

        if (cursor != null) {
            mSelectedKey = getSelectedApnKey();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String key = cursor.getString(ID_INDEX);
                String type = cursor.getString(TYPES_INDEX);

                boolean selectable = ((type == null) || !type.equals("mms"));
                if (selectable) {
                    Log.d(LOG_TAG, "Selectable key(" + key + ")");
                    mKeys.add(key);
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
    }

    private void setSelectedApnKey(String key) {
        mSelectedKey = key;
        ContentResolver resolver = mContext.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(APN_ID, mSelectedKey);
        resolver.update(PREFERRED_APN_URL, values, null, null);
    }

    private String getSelectedApnKey() {
        String key = null;

        Cursor cursor = mContext.getContentResolver().query(PREFERRED_APN_URL,
                new String[] { "_id" }, null, null, DEFAULT_SORT_ORDER);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            key = cursor.getString(ID_INDEX);
        }
        cursor.close();
        return key;
    }

    private String getUnselectedApnKey() {
        Set<String> set = new HashSet<String>();
        set.addAll(mKeys);
        set.remove(mSelectedKey);
        String[] keys = set.toArray(new String[set.size()]);
        if (keys.length > 0) {
            int index = (int) (Math.random() * keys.length);
            return keys[index];
        }
        return null;
    }
}
