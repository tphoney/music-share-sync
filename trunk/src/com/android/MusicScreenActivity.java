package com.android;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.List;

import jcifs.smb.SmbException;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		cifsInteraction = new CifsInteraction();
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
		if (! isClickedItemALeaf(itemClicked)) {
			//sync entire dir
			
		} 
		Dialog dialog = new Dialog(MusicScreenActivity.this);
		dialog.setTitle("Long press: " + itemClicked);
		dialog.setCancelable(true);
		dialog.show();

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
					(currentWorkingDirectory + "/" + itemClicked));
		} catch (MalformedURLException e) {			
			displayErrorMessage(e);
		} catch (SmbException e) {
			displayErrorMessage(e);
		}
		return returnVal;
	}

	protected void copyFile(String fileToCopy) {
		try {
			cifsInteraction.copyFileTo(settings.getString("remoteHostname",
					getString(R.string.preferences_remote_hostname)),
					currentWorkingDirectory, fileToCopy,
					getString(R.string.preferences_local_basedir));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			displayErrorMessage(e);
		}
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

}
