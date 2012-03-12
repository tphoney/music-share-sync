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
		setContentView(R.layout.splashscreen);
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
		boolean returnVal;
		Intent intent;
		switch (item.getItemId()) {
		// case MUSIC_SCREEN:
		case R.id.home_menu_sync:
			intent = new Intent(SplashScreenActivity.this,
					DisplayShareScreenActivity.class);
			startActivity(intent);
			returnVal = true;
			break;
		case R.id.home_menu_settings:
			intent = new Intent(SplashScreenActivity.this,
					SettingsScreenActivity.class);
			startActivity(intent);
			returnVal = true;
			break;
		case R.id.home_menu_delete:
			intent = new Intent(SplashScreenActivity.this,
					DeleteScreenActivity.class);
			startActivity(intent);
			returnVal = true;
			break;
			
		case R.id.home_menu_refresh:
			intent = new Intent(SplashScreenActivity.this,
					SettingsScreenActivity.class);
			startActivity(intent);
			returnVal = true;
			break;
//		case R.id.home_menu_tree_test:
//				intent = new Intent(SplashScreenActivity.this,
//						TreeTestScreenActivity.class);
//				startActivity(intent);
//				returnVal = true;
//				break;
//		case R.id.home_menu_tree_test2:
//			intent = new Intent(SplashScreenActivity.this,
//					TreeTestScreenActivity2.class);
//			startActivity(intent);
//			returnVal = true;
//			break;
		default:
			returnVal = super.onMenuItemSelected(featureId, item);
			break;
		}
		return returnVal;
	}

}
