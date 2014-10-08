package com.ztemt.test.platform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.ztemt.test.platform.model.SystemTest;
import com.ztemt.test.platform.model.SystemUpdate;
import com.ztemt.test.platform.model.Task;

public class TaskAdapter extends BaseExpandableListAdapter {

    private static final String NAME = "NAME";
    private static final String VALUE = "VALUE";

    private Context mContext;

    private List<Map<String, String>> mGroupData = new ArrayList<Map<String, String>>();
    private List<List<Task>> mChildData = new ArrayList<List<Task>>();

    public TaskAdapter(Context context) {
        mContext = context;
        for (int i = TaskList.get().size() - 1; i >= 0; i--) {
            long timestamp = TaskList.get().get(i).getTimestamp();
            Map<String, String> curGroupMap = new HashMap<String, String>();
            curGroupMap.put(NAME, String.valueOf(timestamp));

            List<Task> children = new ArrayList<Task>();
            while (true) {
                Task task = TaskList.get().get(i);
                children.add(0, task);
                if (i > 0 && TaskList.get().get(i - 1).getTimestamp() == timestamp) {
                    i--;
                } else {
                    break;
                }
            }
            curGroupMap.put(VALUE, String.valueOf(mContext.getResources()
                    .getQuantityString(R.plurals.tasks, children.size(),
                            children.size())));
            mGroupData.add(curGroupMap);
            mChildData.add(children);
        }
    }

    @Override
    public int getGroupCount() {
        return mGroupData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChildData.get(groupPosition).size();
    }

    @Override
    public Map<String, String> getGroup(int groupPosition) {
        return mGroupData.get(groupPosition);
    }

    @Override
    public Task getChild(int groupPosition, int childPosition) {
        return mChildData.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    android.R.layout.simple_expandable_list_item_2, null);
            holder.text1 = (TextView) convertView
                    .findViewById(android.R.id.text1);
            holder.text2 = (TextView) convertView
                    .findViewById(android.R.id.text2);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text1.setText(mGroupData.get(groupPosition).get(NAME));
        holder.text2.setText(mGroupData.get(groupPosition).get(VALUE));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    android.R.layout.simple_expandable_list_item_2, null);
            holder.text1 = (TextView) convertView
                    .findViewById(android.R.id.text1);
            holder.text2 = (TextView) convertView
                    .findViewById(android.R.id.text2);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Task task = mChildData.get(groupPosition).get(childPosition);
        if (task.getType() == Task.TYPE_SYSTEM_UPDATE) {
            holder.text1.setText(R.string.system_update);
            holder.text2.setText(((SystemUpdate) task).getBuildDate());
        } else if (task.getType() == Task.TYPE_SYSTEM_TEST) {
            holder.text1.setText(R.string.system_test);
            holder.text2.setText(((SystemTest) task).getName());
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private class ViewHolder {
        TextView text1;
        TextView text2;
    }
}
