package com.android;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import jcifs.smb.SmbException;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

public class MusicScreenActivity extends ListActivity {

	public static final String PREFS_NAME = "MusicShareSync.preferences";
	CifsInteraction cifsInteraction;
	SharedPreferences settings;
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
		displayFolderContents();

	}

	protected void onListItemLongClick (ListView l, View v, int position, long id) {
		CheckedTextView textView = (CheckedTextView)v;
		  textView.setChecked(!textView.isChecked());
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {

		String itemClicked = (String) getListAdapter().getItem(position);
		if (itemClicked.equals("UP")) {
			//remove lastFolder
			currentWorkingDirectory = getMuckyParent();
			displayFolderContents();
		} else {
			try {
				if (cifsInteraction.isLeaf(settings.getString("remoteHostname",
						getString(R.string.preferences_remote_hostname)),
						currentWorkingDirectory)) {

					cifsInteraction.copyFileTo(settings.getString(
							"remoteHostname",
							getString(R.string.preferences_remote_hostname)),
							currentWorkingDirectory,
							getString(R.string.preferences_local_basedir));
					Dialog dialog = new Dialog(MusicScreenActivity.this);
					dialog.setTitle("Clicked: " + currentWorkingDirectory);
					dialog.setCancelable(true);
					dialog.show();
				} else {
					currentWorkingDirectory = currentWorkingDirectory
							+ itemClicked;
					displayFolderContents();
				}
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SmbException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// //is leaf ? no sub folders
		// try {
		// for (String string : temp) {
		// String fullRemote = newpath + string;
		// // dialog.setTitle("Clicked: " + fullRemote);
		// // dialog.setCancelable(true);
		// // dialog.show();
		// cifsInteraction.copyFileTo(settings.getString("remoteHostname",
		// getString (R.string.preferences_remote_hostname)),
		// fullRemote, getString (R.string.preferences_local_basedir));
		// }
		//
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SmbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// add in back button

	}
	
	protected String getMuckyParent() {
		String returnVal = currentWorkingDirectory;
		try {
			returnVal = cifsInteraction
					.getParent(settings.getString("remoteHostname",
							getString(R.string.preferences_remote_hostname)),
							currentWorkingDirectory);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SmbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnVal;
	}

}
