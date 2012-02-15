package com.zook.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Main screen, default home Splash Screen Uses the home_menu menu options
 * (home_menu.xml)
 * 
 * @author tp
 * 
 */
public class SplashScreenActivity extends Activity {

	/**
	 * Main layout and display creation hook
	 */
	@Override
	public void onCreate(final Bundle instanceState) {
		super.onCreate(instanceState);
		setContentView(R.layout.main);
	}

	/**
	 * Main splash screen menu creation hook
	 */
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home_menu, menu);
		return true;
	}

	/**
	 * Event handler for this main screen/splash screen menu option
	 */
	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		// case MUSIC_SCREEN:
		case R.id.home_menu_sync:
			intent = new Intent(SplashScreenActivity.this,
					DisplayShareScreenActivity.class);
			startActivity(intent);
			return true;
			// case SETTINGS_SCREEN:
		case R.id.home_menu_settings:
			intent = new Intent(SplashScreenActivity.this,
					SettingsScreenActivity.class);
			startActivity(intent);
			return true;
			// case SETTINGS_SCREEN:
			// case R.id.home_menu_tree_tests:
			// intent = new Intent(MusicShareSyncActivity.this,
			// TreeTestScreenActivity.class);
			// startActivity(intent);
			// return true;
		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

}
