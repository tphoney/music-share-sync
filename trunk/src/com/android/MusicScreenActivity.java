package com.android;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jcifs.smb.SmbException;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MusicScreenActivity extends ListActivity {

	public static final String PREFS_NAME = "MusicShareSync.preferences";
	private CifsInteraction cifsInteraction;
	private SharedPreferences settings;
	private String currentWorkingDirectory;
	private ProgressDialog pd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cifsInteraction = new CifsInteraction();
		cifsInteraction.setHandler (updateProgress);
		settings = getSharedPreferences(PREFS_NAME, 0);
		currentWorkingDirectory = settings.getString("remoteBaseDirectory",
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
		} catch (SmbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final ListView lv = getListView();
		// Then you can create a listener like so:
		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> av, View v, int pos,
					long id) {
				onLongListItemClick(lv, v, pos, id);
				return false;
			}
		});

		displayFolderContents();

	}

	protected void onLongListItemClick(ListView l, View v, int position, long id) {
		String itemClicked = (String) getListAdapter().getItem(position);
		if (!isClickedItemALeaf(itemClicked)) {
			// sync entire dir
			try {
				cifsInteraction.copyFolder(settings.getString("remoteHostname",
						getString(R.string.preferences_remote_hostname)),
						currentWorkingDirectory, itemClicked,
						getString(R.string.preferences_local_basedir));
			} catch (IOException e) { // TODO Auto-generated catch block
				displayErrorMessage(e);
			}
		}
		Dialog dialog = new Dialog(MusicScreenActivity.this);
		dialog.setTitle("Syncing: " + itemClicked);
		dialog.setCancelable(true);
		dialog.show();
		refreshMedia();
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		String itemClicked = (String) getListAdapter().getItem(position);
		if (itemClicked.equals("UP")) {
			// remove lastFolder
			currentWorkingDirectory = getMuckyParent();
			displayFolderContents();
		} else {
			if (isClickedItemALeaf(itemClicked)) {
				copyFile(itemClicked);
			} else {
				currentWorkingDirectory = currentWorkingDirectory + itemClicked;
				displayFolderContents();
			}
		}
	}

	protected boolean isClickedItemALeaf(String itemClicked) {
		boolean returnVal = false;
		try {
			returnVal = cifsInteraction.isLeaf(settings.getString(
					"remoteHostname",
					getString(R.string.preferences_remote_hostname)),
					currentWorkingDirectory, itemClicked);
		} catch (MalformedURLException e) {
			displayErrorMessage(e);
		} catch (SmbException e) {
			displayErrorMessage(e);
		}
		return returnVal;
	}

	protected void copyFile(final String fileToCopy) {
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("Searching Device");
		pd.setIndeterminate(false);
		pd.show();
		Thread thread = new Thread(new LongTask(fileToCopy, true));
		thread.start();
		
		
		refreshMedia();
	}

	protected void displayFolderContents() {
		try {
			List<String> directoryContents = cifsInteraction
					.getDirectoryContents(settings.getString("remoteHostname",
							getString(R.string.preferences_remote_hostname)),
							currentWorkingDirectory);

			if (!currentWorkingDirectory.equals(settings.getString(
					"remoteBaseDirectory",
					getString(R.string.preferences_remote_basedir)))) {
				directoryContents.add(0, "UP");
			}
			ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, directoryContents);
			setListAdapter(directoryList);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			displayErrorMessage(e);
		} catch (SmbException e) {
			e.printStackTrace();
			displayErrorMessage(e);
		}
		getListView().setFastScrollEnabled(true);
	}

	protected String getMuckyParent() {
		String returnVal = currentWorkingDirectory;
		try {
			returnVal = cifsInteraction.getParent(settings.getString(
					"remoteHostname",
					getString(R.string.preferences_remote_hostname)),
					currentWorkingDirectory);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			displayErrorMessage(e);
		} catch (SmbException e) {
			e.printStackTrace();
			displayErrorMessage(e);
		}
		return returnVal;
	}

	protected void displayErrorMessage(Throwable e) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		e.printStackTrace(printWriter);
		String stacktrace = result.toString();
		Dialog dialog = new Dialog(MusicScreenActivity.this);
		dialog.setTitle("Something went wrong: ");
		final TextView tx = new TextView(this);
		tx.setText(stacktrace);
		dialog.setContentView(tx);
		dialog.setCancelable(true);
		dialog.show();
	}

	private void refreshMedia() {
		sendBroadcast(new Intent(
				Intent.ACTION_MEDIA_MOUNTED,
				Uri.parse("file://" + Environment.getExternalStorageDirectory())));
	}

	private Handler updateProgress = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				pd.dismiss();
				break;
			case 1:
				pd.setProgress( Integer.parseInt(msg.obj.toString()));
				break;
			default:
				pd.setProgress(0);
				pd.setMessage((CharSequence) msg.obj);
			}
		}
	};
	
	private class LongTask implements Runnable {
		private final String fileage;
		private final boolean isFile;
		
		public LongTask(String filebla, boolean isFilebla) {
			fileage = filebla;
			isFile = isFilebla;
		}
		
		public void run() {
			updateProgress.sendMessage(Message.obtain(updateProgress,
					100, "copying file"));
			
			ExecutorService executor = Executors
					.newFixedThreadPool(1);
			
				Runnable worker = new CopyFile(fileage);
				executor.execute(worker);
			
			// This will make the executor accept no new threads
			// and finish all existing threads in the queue
			executor.shutdown();
			// Wait until all threads are finish
			while (!executor.isTerminated()) {

			}
			
			updateProgress.sendMessage(Message.obtain(updateProgress, 0, ""));
		}
	}
	
	class CopyFile implements Runnable {
		private final String filage;

		public CopyFile(String inputNumber) {
			filage = inputNumber;
		}

		public void run() {
			try {
				cifsInteraction.copyFileTo(settings.getString("remoteHostname",
						getString(R.string.preferences_remote_hostname)),
						currentWorkingDirectory, filage,
						getString(R.string.preferences_local_basedir), updateProgress);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				displayErrorMessage(e);
			}
		//	updateProgress.sendMessage(Message.obtain(updateProgress, 1, ""));
		}
	}
}
