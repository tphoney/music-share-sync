package com.zook.ui.activities;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import com.zook.services.CifsRemoteFileCopy;
import com.zook.services.RemoteFileCopyInterface;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SettingsScreenActivity extends Activity {

	public static final String PREFS_NAME = "MusicShareSync.preferences";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settingsscreen);

		// Restore preferences
		final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

		final EditText etHostname = (EditText) findViewById(R.id.etHostname);
		etHostname.setText(settings.getString("remoteHostname",
				getString(R.string.preferences_remote_hostname)));

		final EditText etRemoteBaseDirectory = (EditText) findViewById(R.id.etStartingPath);
		etRemoteBaseDirectory.setText(settings.getString("remoteBaseDirectory",
				getString(R.string.preferences_remote_basedir)));

		final EditText etTargetDomain = (EditText) findViewById(R.id.etWorkgroup);
		etTargetDomain.setText(settings.getString("targetDomain",
				getString(R.string.preferences_target_domain)));

		final EditText etRemoteUsername = (EditText) findViewById(R.id.etUsername);
		etRemoteUsername.setText(settings.getString("remoteUsername",
				getString(R.string.preferences_remote_username)));

		final EditText etPassword = (EditText) findViewById(R.id.etPassword);
		etPassword.setText(settings.getString("remotePassword",
				getString(R.string.preferences_remote_password)));

		final Button buttonSaveSettings = (Button) findViewById(R.id.buttonSaveSettings);
		buttonSaveSettings.setOnClickListener(new OnClickListener() {
			public void onClick(final View view) {
				// We need an Editor object to make preference changes.
				// All objects are from android.context.Context
				final SharedPreferences settings = getSharedPreferences(
						PREFS_NAME, 0);
				final SharedPreferences.Editor editor = settings.edit();
				editor.putString("remoteHostname", etHostname.getText()
						.toString());
				editor.putString("remoteBaseDirectory", etRemoteBaseDirectory
						.getText().toString());
				editor.putString("targetDomain", etTargetDomain.getText()
						.toString());
				editor.putString("remoteUsername", etRemoteUsername.getText()
						.toString());
				editor.putString("remotePassword", etPassword.getText()
						.toString());
				// hold your horses
				final RemoteFileCopyInterface bla = new CifsRemoteFileCopy();
				boolean canConnect = true;
				String stackTrace = "WRONG";
				try {
					bla.createConnection(etTargetDomain.getText().toString(),
							etRemoteUsername.getText().toString(), etPassword
									.getText().toString(), etHostname.getText()
									.toString());
				} catch (Exception e) {
					canConnect = false;
					final Writer result = new StringWriter();
					final PrintWriter printWriter = new PrintWriter(result);
					e.printStackTrace(printWriter);
					stackTrace = result.toString();
				}

				// Commit the edits!
				if (canConnect) {
					editor.commit();
					finish();
				} else {
					
					final Dialog dialog = new Dialog(SettingsScreenActivity.this);
					dialog.setTitle("Something went wrong: ");
					final TextView textview = new TextView(SettingsScreenActivity.this);
					textview.setText(stackTrace);
					dialog.setContentView(textview);
					dialog.setCancelable(true);
					dialog.show();
				}

				
			}
		});
	}

}
