package com.android;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import jcifs.smb.SmbException;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class MusicScreenActivity extends Activity {

	public static final String PREFS_NAME = "MusicShareSync.preferences";
	String remoteHostname = "test-pc";
	String remoteBaseDirectory = "Music";
	String targetDomain = "workgroup";
	String remoteUsername = "guest";
	String remotePassword = "";
	String localBaseDirectory = "MusicShareSync";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.musicscreen);

		// Restore preferences
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

		TextView tv = new TextView(this);
        tv.setText("WTF:" );
        setContentView(tv);

		CifsInteraction bla = new CifsInteraction();
		try {
			bla.createConnection(
					settings.getString("targetDomain", targetDomain),
					settings.getString("remoteUsername", remoteUsername),
					settings.getString("remotePassword", remotePassword),
					settings.getString("remoteHostname", remoteHostname));
		} catch (SmbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> temp = new ArrayList<String>();
		try {
			temp = bla.getListOfDirs(settings.getString("remoteHostname",
					remoteHostname), settings.getString("remoteBaseDirectory",
					remoteBaseDirectory));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SmbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		tv.setText(  temp.toString());
        setContentView(tv);

		// boolean successful = false;
		// try {
		// successful = bla.copyFileTo(remoteHostname,
		// "/Music/Adele/19/folder.jpg", localBaseDirectory);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// if (successful) {
		// tv.setText("we just copied a fucking file");
		// }
		// setContentView(tv);

	}

}
