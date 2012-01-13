package com.android;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import jcifs.smb.SmbException;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MusicScreenActivity extends ListActivity {

	public static final String PREFS_NAME = "MusicShareSync.preferences";
	String remoteHostname = "test-pc";
	String remoteBaseDirectory = "Music/";
	String targetDomain = "workgroup";
	String remoteUsername = "guest";
	String remotePassword = "";
	String localBaseDirectory = "MusicShareSync";
	CifsInteraction cifsInteraction;
	SharedPreferences settings;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		cifsInteraction = new CifsInteraction();
		settings = getSharedPreferences(PREFS_NAME, 0);
		try {
			cifsInteraction.createConnection(
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
			temp = cifsInteraction.getListOfDirs(settings.getString("remoteHostname",
					remoteHostname), settings.getString("remoteBaseDirectory",
					remoteBaseDirectory));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SmbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, temp);
		setListAdapter(directoryList);

	}

	protected void onListItemClick(ListView l, View v, int position, long id) {

		String item = (String) getListAdapter().getItem(position);
		List<String> temp = new ArrayList<String>();
        String newpath = settings.getString("remoteBaseDirectory",
					remoteBaseDirectory) +  "/" + item;
//		 Dialog dialog = new Dialog(MusicScreenActivity.this);
//         dialog.setTitle("Clicked: " + newpath);
//         dialog.setCancelable(true);
//         dialog.show();         
 		
         try {
 			temp = cifsInteraction.getListOfDirs(settings.getString("remoteHostname",
 					remoteHostname), newpath) ;
 		} catch (MalformedURLException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		} catch (SmbException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
 		
         //is leaf ? no sub folders
         try {
        	for (String string : temp) {
        		String fullRemote = newpath  + string;
//        		dialog.setTitle("Clicked: " + fullRemote);
//                dialog.setCancelable(true);
//                dialog.show();
        		cifsInteraction.copyFileTo(settings.getString("remoteHostname", remoteUsername),
        				fullRemote, localBaseDirectory);
			}
 			
 		} catch (IOException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
         
         //not leaf = refreshscreen at new directory level
         
//		try {
//			cifsInteraction.copyFileTo(settings.getString("remoteHostname", remoteUsername),
//					"/Music/Adele/19/folder.jpg", localBaseDirectory);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
	}

}
