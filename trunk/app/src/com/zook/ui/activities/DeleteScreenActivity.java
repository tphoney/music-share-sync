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

import com.zook.services.CifsRemoteFileCopy;
import com.zook.services.LocalCrap;
import com.zook.services.RemoteFileCopyInterface;
import com.zook.ui.ExceptionDialog;
import com.zook.ui.LocalContentsArrayAdapter;
import com.zook.ui.utils.UIUtils;

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
		if (isClickedItemALeaf(itemClicked)) {
			if (fileExistsLocally(itemClicked)) {
				removeFile(itemClicked);
			}
		}
		refreshMedia();
		displayFolderContents();
	}

	protected void onListItemClick(final ListView listview, final View view,
			final int position, final long rowId) {
		final String itemClicked = (String) getListAdapter().getItem(position);
		if ("..".equals(itemClicked)) {
			// remove lastFolder
			currentDirectory = getcurrentWorkingDirectoryParent();
			displayFolderContents();
		} else {
			if (!isClickedItemALeaf(itemClicked)) {
				if (!fileExistsLocally(itemClicked)) {
					currentDirectory = currentDirectory + itemClicked;
					displayFolderContents();
				}
			}
		}
	}

	protected boolean fileExistsLocally(final String itemClicked) {
		boolean returnVal = false;
		try {
			returnVal = localCrap.fileExistsLocally(currentDirectory,
					itemClicked, getString(R.string.preferences_local_basedir));
		} catch (Exception e) {
			new ExceptionDialog(e, this);
		}
		return returnVal;
	}

	protected void removeFile(final String itemClicked) {
		try {
			localCrap.removeFileLocally(currentDirectory, itemClicked,
					getString(R.string.preferences_local_basedir));
		} catch (Exception e) {
			new ExceptionDialog(e, this);
		}
		refreshMedia();
		displayFolderContents();
	}

	protected boolean isClickedItemALeaf(final String itemClicked) {
		boolean returnVal = false;
		try {
			returnVal = localCrap.isLeaf(currentDirectory, itemClicked);
		} catch (Exception e) {
			UIUtils.displayErrorMessage(e, this);
		}
		return returnVal;
	}

	protected void displayFolderContents() {
		try {
			final List<String> dirContents = localCrap
					.getDirectoryContents(currentDirectory);
			
			if (!currentDirectory.equals(settings.getString(
					"remoteBaseDirectory",
					getString(R.string.preferences_remote_basedir)))) {
				dirContents.add(0, "..");
			}
			setListAdapter(new LocalContentsArrayAdapter(this, this,
					R.layout.row, dirContents));
		} catch (Exception e) {
			UIUtils.displayErrorMessage(e, this);
		}
		getListView().setFastScrollEnabled(true);
	}

	protected String getcurrentWorkingDirectoryParent() {
		String returnVal = currentDirectory;
		try {
			returnVal = localCrap.getParent(currentDirectory);
		} catch (Exception e) {
			UIUtils.displayErrorMessage(e, this);
		}
		return returnVal;
	}

	private void refreshMedia() {
		sendBroadcast(new Intent(
				Intent.ACTION_MEDIA_MOUNTED,
				Uri.parse("file://" + Environment.getExternalStorageDirectory())));
	}

}
