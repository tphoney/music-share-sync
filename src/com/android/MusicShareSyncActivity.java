package com.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class MusicShareSyncActivity extends Activity {
	/** Called when the activity is first created. */
	// private Collection musicCollection = new Collection();
	private static final int SETTINGS_SCREEN = Menu.FIRST;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
//		ImageView image = (ImageView) findViewById(R.drawable.background);
//		setContentView(image);
		
		
		// TextView tv = new TextView(this);
		// tv.setText("Please set up the defaults.");
		// setContentView(tv);

		// CifsInteraction bla = new CifsInteraction();
		// try {
		// bla.createConnection(targetDomain, remoteUsername, remotePassword,
		// remoteHostname);
		// } catch (SmbException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (UnknownHostException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// List<String> temp = new ArrayList<String>();
		// try {
		// temp = bla.getListOfDirs(remoteHostname, remoteBaseDirectory);
		// } catch (MalformedURLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (SmbException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// tv.setText(temp.toString());
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, SETTINGS_SCREEN, 0, "Settings");
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case SETTINGS_SCREEN:
			Intent i = new Intent(MusicShareSyncActivity.this,
					SettingsScreenActivity.class);
			startActivity(i);
			return true;

		}
		return super.onMenuItemSelected(featureId, item);
	}

}
