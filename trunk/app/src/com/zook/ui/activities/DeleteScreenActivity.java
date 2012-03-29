package com.zook.ui.activities;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.zook.services.LocalCrap;
import com.zook.ui.LocalContentsArrayAdapter;

public class DeleteScreenActivity extends ListActivity {

	public static final String PREFS_NAME = "MusicShareSync.preferences";
	private transient LocalCrap localCrap;
	private transient SharedPreferences settings;
	private transient String currentDirectory;

	@Override
	public void onCreate(final Bundle state) {
		super.onCreate(state);
		this.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		localCrap = new LocalCrap();

		settings = getSharedPreferences(PREFS_NAME, 0);
		currentDirectory = settings.getString("remoteBaseDirectory",
				getString(R.string.preferences_remote_basedir));

		final ListView listView = getListView();
		// Then you can create a listener like so:
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(final AdapterView<?> adapterView,
					final View view, final int pos, final long rowid) {
				onLongListItemClick(listView, view, pos, rowid);
				return false;
			}
		});

		displayFolderContents();
	}

	protected void onLongListItemClick(final ListView listView,
			final View view, final int position, final long rowid) {
		final String itemClicked = (String) getListAdapter().getItem(position);
		removeFile(itemClicked);
		refreshMedia();
		displayFolderContents();
	}

	protected void onListItemClick(final ListView listview, final View view,
			final int position, final long rowId) {
		final String itemClicked = (String) getListAdapter().getItem(position);
		if ("..".equals(itemClicked)) {
			// remove lastFolder
			currentDirectory = localCrap.getParent(currentDirectory);
			displayFolderContents();
		} else {
			if (!localCrap.isLeaf(currentDirectory, itemClicked)) {
				currentDirectory = currentDirectory + "/" + itemClicked;
				displayFolderContents();
			}
		}
	}

	protected void removeFile(final String itemClicked) {
		localCrap.removeFileLocally(currentDirectory, itemClicked,
				getString(R.string.preferences_local_basedir));

		refreshMedia();
		displayFolderContents();
	}

	protected void displayFolderContents() {
		final List<String> dirContents = localCrap
				.getDirectoryContents(currentDirectory);

		if (!currentDirectory.equals(settings.getString("remoteBaseDirectory",
				getString(R.string.preferences_remote_basedir)))) {
			dirContents.add(0, "..");
		}
		setListAdapter(new LocalContentsArrayAdapter(this, this, R.layout.row,
				dirContents));

		getListView().setFastScrollEnabled(true);
	}

	private void refreshMedia() {
		sendBroadcast(new Intent(
				Intent.ACTION_MEDIA_MOUNTED,
				Uri.parse("file://" + Environment.getExternalStorageDirectory())));
	}

}
