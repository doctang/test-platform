package com.ztemt.test.platform;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ListFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileObserver;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ShareActionProvider;

import com.ztemt.test.platform.util.FileUtils;

public class OutputManager extends ListFragment {

    public static File OUTPUT = FileUtils.getFileStreamPath("output");
    private OutputAdapter mAdapter;

    private FileObserver mObserver = new FileObserver(OUTPUT.getAbsolutePath()) {

        @Override
        public void onEvent(int event, String path) {
            switch (event & FileObserver.ALL_EVENTS) {
            case FileObserver.CREATE:
            case FileObserver.DELETE:
            case FileObserver.DELETE_SELF:
                getListView().post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.replace(OUTPUT);
                    }
                });
                break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new OutputAdapter(getActivity(), OUTPUT);
        setListAdapter(mAdapter);
        mObserver.startWatching();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.output_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getActionBar().setSubtitle(R.string.long_press_select);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        getListView().setMultiChoiceModeListener(new ModeCallback());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().getActionBar().setSubtitle(null);
        mObserver.stopWatching();
    }

    private class ModeCallback implements ListView.MultiChoiceModeListener {

        private ShareActionProvider mShareActionProvider;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.output_options, menu);
            mode.setTitle(R.string.select_items);
            setSubtitle(mode);

            // Set file with share history to the provider and set the share intent
            MenuItem actionItem = menu.findItem(R.id.item_share);
            mShareActionProvider = (ShareActionProvider) actionItem.getActionProvider();
            mShareActionProvider.setShareHistoryFileName(
                    ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
            case R.id.item_upload:
                getActivity().startService(new Intent(getActivity(),
                        OutputService.class));
                mode.finish();
                break;
            case R.id.item_delete:
                FileUtils.delete(getSelectedFiles());
                mode.finish();
                break;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position,
                long id, boolean checked) {
            mShareActionProvider.setShareIntent(createShareIntent());
            setSubtitle(mode);
        }

        private void setSubtitle(ActionMode mode) {
            final int checkedCount = getListView().getCheckedItemCount();
            switch (checkedCount) {
            case 0:
                mode.setSubtitle(null);
                break;
            case 1:
                mode.setSubtitle(getResources().getQuantityString(
                        R.plurals.selects, 1, 1));
                break;
            default:
                mode.setSubtitle(getResources().getQuantityString(
                        R.plurals.selects, checkedCount, checkedCount));
                break;
            }
        }

        private List<File> getSelectedFiles() {
            List<File> files = new ArrayList<File>();
            for (int i = 0; i < mAdapter.getCount(); i++) {
                if (getListView().getCheckedItemPositions().get(i)) {
                    files.add(mAdapter.getItem(i));
                }
            }
            return files;
        }

        private Intent createShareIntent() {
            List<File> files = getSelectedFiles();
            ArrayList<Uri> uris = new ArrayList<Uri>();
            for (File file : files) {
                uris.add(Uri.fromFile(file));
            }

            Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.setType("*/*");
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            return shareIntent;
        }
    }
}
