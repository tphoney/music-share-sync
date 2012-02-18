package com.zook.ui.activities;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.zook.ui.activities.R;
import com.zook.services.CifsRemoteFileCopy;
import com.zook.services.RemoteFileCopyInterface;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class DisplayShareScreenActivity extends ListActivity {

	public static final String PREFS_NAME = "MusicShareSync.preferences";
	private transient RemoteFileCopyInterface cifsInteraction;
	private transient SharedPreferences settings;
	private transient String currentDirectory;
	private transient ProgressDialog progressDialog;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cifsInteraction = new CifsRemoteFileCopy();

		settings = getSharedPreferences(PREFS_NAME, 0);
		currentDirectory = settings.getString("remoteBaseDirectory",
				getString(R.string.preferences_remote_basedir));
		try {
			cifsInteraction.createConnection(settings.getString("targetDomain",
					getString(R.string.preferences_target_domain)), settings
					.getString("remoteUsername",
							getString(R.string.preferences_remote_username)),
					settings.getString("remotePassword",
							getString(R.string.preferences_remote_password)),
					settings.getString("remoteHostname",
							getString(R.string.preferences_remote_hostname)));
		} catch (Exception e) {
			displayErrorMessage(e);
		}

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
		if (!isClickedItemALeaf(itemClicked)) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setMessage("Syncing Folder");
			progressDialog.setIndeterminate(false);
			progressDialog.show();
			final Thread thread = new Thread(new LongCopyOperation(itemClicked,
					false));
			thread.start();
		}
		refreshMedia();
		displayFolderContents();
	}

	protected void onListItemClick(final ListView listview, final View view,
			final int position, final long id) {
		final String itemClicked = (String) getListAdapter().getItem(position);
		if ("..".equals(itemClicked)) {
			// remove lastFolder
			currentDirectory = getcurrentWorkingDirectoryParent();
			displayFolderContents();
		} else {
			if (isClickedItemALeaf(itemClicked)) {
				copyFile(itemClicked);
			} else {
				currentDirectory = currentDirectory + itemClicked;
				displayFolderContents();
			}
		}
	}

	protected boolean isClickedItemALeaf(final String itemClicked) {
		boolean returnVal = false;
		try {
			returnVal = cifsInteraction.isLeaf(currentDirectory,
					itemClicked);
		} catch (Exception e) {
			displayErrorMessage(e);
		}
		return returnVal;
	}

	protected void copyFile(final String fileToCopy) {
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setMessage("Searching Device");
		progressDialog.setIndeterminate(false);
		progressDialog.show();
		final Thread thread = new Thread(
				new LongCopyOperation(fileToCopy, true));
		thread.start();

		displayFolderContents();
		refreshMedia();
	}

	protected void displayFolderContents() {
		try {
			final List<String> directoryContents = cifsInteraction
					.getDirectoryContents(currentDirectory);
			final List<Boolean> directoryContentsSyncStatus = cifsInteraction
					.getDirectoryContentsSyncStatus(currentDirectory, getString(R.string.preferences_local_basedir));
			
			if (!currentDirectory.equals(settings.getString(
					"remoteBaseDirectory",
					getString(R.string.preferences_remote_basedir)))) {
				directoryContents.add(0, "..");
				directoryContentsSyncStatus.add (0, false);
			}
			setListAdapter(new MyCustomAdapter(this, this, R.layout.row,
					directoryContents, directoryContentsSyncStatus));
		} catch (Exception e) {
			displayErrorMessage(e);
		}
		getListView().setFastScrollEnabled(true);
	}

	protected String getcurrentWorkingDirectoryParent() {
		String returnVal = currentDirectory;
		try {
			returnVal = cifsInteraction.getParent(currentDirectory);
		} catch (Exception e) {
			displayErrorMessage(e);
		}
		return returnVal;
	}

	protected void displayErrorMessage(final Throwable exception) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		exception.printStackTrace(printWriter);
		final String stacktrace = result.toString();
		final Dialog dialog = new Dialog(DisplayShareScreenActivity.this);
		dialog.setTitle("Something went wrong: ");
		final TextView textview = new TextView(this);
		textview.setText(stacktrace);
		dialog.setContentView(textview);
		dialog.setCancelable(true);
		dialog.show();
	}

	private void refreshMedia() {
		sendBroadcast(new Intent(
				Intent.ACTION_MEDIA_MOUNTED,
				Uri.parse("file://" + Environment.getExternalStorageDirectory())));
	}

	private final Handler progressHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case R.integer.progressDialogDismiss:
				progressDialog.dismiss();
				break;
			case R.integer.progressDialogSetProgress:
				progressDialog
						.setProgress(Integer.parseInt(msg.obj.toString()));
				break;
			case R.integer.progressDialogSetSecondaryProgress:
				progressDialog.setSecondaryProgress(Integer.parseInt(msg.obj
						.toString()));
			case R.integer.progressDialogSetTitle:
				progressDialog.setMessage((CharSequence) msg.obj);
			default:
				progressDialog.setProgress(0);
				progressDialog.setMessage((CharSequence) msg.obj);
			}
		}
	};

	private class LongCopyOperation implements Runnable {
		private final transient String thingToCopy;
		private final transient boolean isFile;

		public LongCopyOperation(final String fileName, final boolean isFile) {
			thingToCopy = fileName;
			this.isFile = isFile;
		}

		public void run() {
			progressHandler.sendMessage(Message.obtain(progressHandler,
					R.integer.progressDialogInit, "Copying: " + thingToCopy));

			final ExecutorService executor = Executors.newFixedThreadPool(1);
			if (isFile) {
				final Runnable worker = new CopyFile(thingToCopy);
				executor.execute(worker);
			} else {
				final Runnable worker = new CopyFolder(thingToCopy);
				executor.execute(worker);
			}

			// This will make the executor accept no new threads
			// and finish all existing threads in the queue
			executor.shutdown();
			// Wait until all threads are finish
			while (!executor.isTerminated()) {
				// there is nothing to do
			}

			progressHandler.sendMessage(Message.obtain(progressHandler,
					R.integer.progressDialogDismiss, ""));
		}
	}

	class CopyFile implements Runnable {
		private transient final String fileToCopy;

		public CopyFile(final String inputFile) {
			fileToCopy = inputFile;
		}

		public void run() {
			try {
				cifsInteraction.copyFileTo(currentDirectory, fileToCopy,
						getString(R.string.preferences_local_basedir),
						progressHandler);
			} catch (Exception e) {
				displayErrorMessage(e);
			}
		}
	}

	class CopyFolder implements Runnable {
		private transient final String folderToCopy;

		public CopyFolder(String inputFile) {
			folderToCopy = inputFile;
		}

		public void run() {
			try {
				cifsInteraction.copyFolder(currentDirectory,
						folderToCopy,
						getString(R.string.preferences_local_basedir),
						progressHandler);
			} catch (Exception e) {
				displayErrorMessage(e);
			}
		}
	}

}
