package com.zook.ui.activities;

import com.zook.ui.activities.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


/**
 * Main screen, default home Splash Screen
 * Uses the home_menu menu options (home_menu.xml)
 * @author tp
 *
 */
public class SplashScreenActivity extends Activity {
	
	/** Called when the activity is first created. */
	// private Collection musicCollection = new Collection();
	//private static final int SETTINGS_SCREEN = Menu.FIRST + 1;
	//private static final int MUSIC_SCREEN = Menu.FIRST;

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
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home_menu, menu);
		return true;

		//super.onCreateOptionsMenu(menu);
		//menu.add(0, MUSIC_SCREEN, 0, "Start to Sync");
		//menu.add(0, SETTINGS_SCREEN, 0, "Settings");
		//return true;
	}

	/** 
	 * Event handler for this main screen/splash screen menu option 
	 */
	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		//case MUSIC_SCREEN:
		case R.id.home_menu_sync:
			intent = new Intent(SplashScreenActivity.this,
					DisplayShareScreenActivity.class);
			startActivity(intent);
			return true;
		//case SETTINGS_SCREEN:
		case R.id.home_menu_settings:
			intent = new Intent(SplashScreenActivity.this,
					SettingsScreenActivity.class);
			startActivity(intent);
			return true;
			//case SETTINGS_SCREEN:
		//case R.id.home_menu_tree_tests:
		//		intent = new Intent(MusicShareSyncActivity.this,
		//				TreeTestScreenActivity.class);
		//		startActivity(intent);
		//		return true;
		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

}
