package com.android;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import jcifs.smb.SmbException;
import android.os.Bundle;
import android.widget.TextView;

public class MusicShareSyncActivity extends Activity {
	/** Called when the activity is first created. */
	private Collection musicCollection = new Collection();

	String remoteHostname = "test-pc";
	String remoteBaseDirectory = "Music";
	String targetDomain = "workgroup";
	String remoteUsername = "guest";
	String remotePassword = "";
	String localBaseDirectory = "MusicShareSync";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TextView tv = new TextView(this);
		tv.setText("WTF:");
		setContentView(tv);

		CifsInteraction bla = new CifsInteraction();
		try {
			bla.createConnection(targetDomain, remoteUsername, remotePassword,
					remoteHostname);
		} catch (SmbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> temp = new ArrayList<String>();
		try {
			temp = bla.getListOfDirs(remoteHostname, remoteBaseDirectory);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SmbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		tv.setText(temp.toString());
		boolean successful = false;
		try {
			successful = bla.copyFileTo(remoteHostname, "/Music/Adele/19/folder.jpg", localBaseDirectory);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (successful) {
			tv.setText("we just copied a fucking file");
		}
		setContentView(tv);

	}

}
