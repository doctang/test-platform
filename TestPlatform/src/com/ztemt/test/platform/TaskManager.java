package com.ztemt.test.platform;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.ztemt.test.platform.model.Task;

public class TaskManager extends ListFragment implements OnChildClickListener {

    private TaskAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new TaskAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_list, container, false);
        ExpandableListView elv = (ExpandableListView) view.findViewById(android.R.id.list);
        elv.setAdapter(mAdapter);
        elv.setOnChildClickListener(this);
        return view;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v,
            int groupPosition, int childPosition, long id) {
        if (getFragmentManager().findFragmentByTag("detail") == null) {
            Task task = mAdapter.getChild(groupPosition, childPosition);
            Bundle bundle = new Bundle();
            bundle.putSerializable(TaskDetailFragment.TASK, task);
            TaskDetailFragment detail = new TaskDetailFragment();
            detail.setArguments(bundle);
            detail.show(getFragmentManager(), "detail");
        }
        return true;
    }
}
