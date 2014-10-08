package com.ztemt.test.platform;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ztemt.test.platform.util.FileUtils;

import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OutputAdapter extends BaseAdapter {

    private Context mContext;
    private List<File> mList;

    public OutputAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<File>();
    }

    public OutputAdapter(Context context, File dir) {
        this(context);
        mList = FileUtils.listFile(dir);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public File getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    android.R.layout.simple_list_item_activated_2, null);
            holder.text1 = (TextView) convertView
                    .findViewById(android.R.id.text1);
            holder.text2 = (TextView) convertView
                    .findViewById(android.R.id.text2);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text1.setText(mList.get(position).getName());
        holder.text2.setText(Formatter.formatFileSize(mContext,
                mList.get(position).length()));
        return convertView;
    }

    public void add(File dir) {
        mList.addAll(FileUtils.listFile(dir));
        notifyDataSetChanged();
    }

    public void replace(File dir) {
        mList.clear();
        add(dir);
    }

    private class ViewHolder {
        TextView text1;
        TextView text2;
    }
}
