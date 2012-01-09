package com.android;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import android.app.Activity;
import jcifs.UniAddress;
import jcifs.netbios.NbtAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbSession;
import android.os.Bundle;
import android.widget.TextView;

public class MusicShareSyncActivity extends Activity {
	/** Called when the activity is first created. */
	private Collection musicCollection = new Collection();

	String pc = "test-pc";
	String username = "guest";
	String password = "guest";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		TextView tv = new TextView(this);
	       tv.setText("WTF:" );
	       setContentView(tv);
		
	       NtlmPasswordAuthentication authentication = new NtlmPasswordAuthentication("workgroup", "guest", "" );  
           
	          SmbFile file;  
	          try  
	          {  
	          UniAddress domain = UniAddress.getByName("test-pc");  
	          SmbSession.logon(domain, authentication);  
	          file = new SmbFile("smb://test-pc/Music/", authentication);  
	          SmbFile[] files;  
	          files = file.listFiles();	         
	          String bla = "";
	          for (int i = 0; i < files.length; i++)
	          {  
	        	  if ( files[i].isDirectory() ) {
	        		  bla = bla +  files[i].getName()  + ", ";
	        	  }
	          }  
	          
	          tv.setText(  bla);
  			  setContentView(tv);
			
		} catch (SmbException e) {
			// TODO Auto-generated catch block
			tv.setText(  e.toString());
			setContentView(tv);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			tv.setText(  e.toString());
			setContentView(tv);

	} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
		tv.setText(  e.toString());
		setContentView(tv);
		}


	}
	
}
