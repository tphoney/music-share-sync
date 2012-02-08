package com.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MusicShareSyncActivity extends Activity {
	/** Called when the activity is first created. */
	// private Collection musicCollection = new Collection();
	private static final int SETTINGS_SCREEN = Menu.FIRST + 1;
	private static final int MUSIC_SCREEN = Menu.FIRST;

	@Override
	public void onCreate(final Bundle instanceState) {
		super.onCreate(instanceState);
		setContentView(R.layout.main);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, MUSIC_SCREEN, 0, "Start to Sync");
		menu.add(0, SETTINGS_SCREEN, 0, "Settings");
		return true;
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case MUSIC_SCREEN:
			intent = new Intent(MusicShareSyncActivity.this,
					MusicScreenActivity.class);
			startActivity(intent);
			return true;
		case SETTINGS_SCREEN:
			intent = new Intent(MusicShareSyncActivity.this,
					SettingsScreenActivity.class);
			startActivity(intent);
			return true;
		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

}
