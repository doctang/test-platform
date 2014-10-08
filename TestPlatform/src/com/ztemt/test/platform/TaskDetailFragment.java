package com.ztemt.test.platform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.ztemt.test.platform.model.SystemTest;
import com.ztemt.test.platform.model.SystemUpdate;
import com.ztemt.test.platform.model.Task;

public class TaskDetailFragment extends DialogFragment {

    public static final String TASK = "task";
    public static final String TITLE = "title";
    public static final String SUMMARY = "summary";

    private OnClickListener mPositiveListener;
    private OnClickListener mNegativeListener;
    private OnDismissListener mDismissListener;

    @Override
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton(android.R.string.ok, mPositiveListener);
        if (mNegativeListener != null) {
            builder.setNegativeButton(android.R.string.cancel, mNegativeListener);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            builder.setOnDismissListener(mDismissListener);
        }
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        Task task = (Task) getArguments().getSerializable(TASK);
        if (task instanceof SystemUpdate) {
            builder.setTitle(R.string.system_update);
            data.addAll(buildSystemUpdateData((SystemUpdate) task));
        } else if (task instanceof SystemTest) {
            builder.setTitle(R.string.system_test);
            data.addAll(buildSystemTestData((SystemTest) task));
        }
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), data,
                android.R.layout.simple_list_item_2, new String[] { TITLE,
                        SUMMARY }, new int[] { android.R.id.text1,
                        android.R.id.text2 });
        ListView lv = new ListView(getActivity());
        lv.setAdapter(adapter);
        builder.setView(lv);
        return builder.create();
    }

    public void setPositiveListener(OnClickListener listener) {
        mPositiveListener = listener;
    }

    public void setNegativeListener(OnClickListener listener) {
        mNegativeListener = listener;
    }

    public void setDismissListener(OnDismissListener listener) {
        mDismissListener = listener;
    }

    private Map<String, String> buildItemData(String title, String summary) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(TITLE, title);
        map.put(SUMMARY, summary);
        return map;
    }

    private List<Map<String, String>> buildSystemUpdateData(
            SystemUpdate systemUpdate) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        list.add(buildItemData(getString(R.string.build_date),
                systemUpdate.getBuildDate()));
        list.add(buildItemData(getString(R.string.build_url),
                systemUpdate.getUpdateUrl()));
        return list;
    }

    private List<Map<String, String>> buildSystemTestData(SystemTest systemTest) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        list.add(buildItemData(getString(R.string.test_name),
                systemTest.getName()));
        list.add(buildItemData(getString(R.string.test_module),
                systemTest.getModule()));
        if (!TextUtils.isEmpty(systemTest.getExtras())) {
            list.add(buildItemData(getString(R.string.test_extras),
                    systemTest.getExtras()));
        }
        String filenames = "";
        if (OutputManager.OUTPUT.exists()) {
            for (String filename : OutputManager.OUTPUT.list()) {
                if (filename.startsWith(systemTest.getId() + "-")) {
                    if (filenames.equals("")) {
                        filenames += filename;
                    } else {
                        filenames += "\n" + filename;
                    }
                }
            }
        }
        if (!TextUtils.isEmpty(filenames)) {
            list.add(buildItemData(getString(R.string.test_output), filenames));
        }
        if (systemTest.getStatus() != Task.STATUS_NOT_START) {
            list.add(buildItemData(getString(R.string.task_status),
                    getTaskStatusString(systemTest.getStatus())));
        }
        return list;
    }

    private String getTaskStatusString(int status) {
        switch (status) {
        case Task.STATUS_NOT_START:
            return getString(R.string.task_not_start);
        case Task.STATUS_CANCEL:
            return getString(R.string.task_cancel);
        case Task.STATUS_WAIT:
            return getString(R.string.task_wait);
        case Task.STATUS_GOING:
            return getString(R.string.task_going);
        case Task.STATUS_FAIL:
            return getString(R.string.task_fail);
        case Task.STATUS_OK:
            return getString(R.string.task_ok);
        default:
            return "Unknown";
        }
    }
}
