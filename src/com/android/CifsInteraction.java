package com.android;

//TODO create interface so we can utilise different protocols
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;
import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbSession;

public class CifsInteraction {
	private NtlmPasswordAuthentication authentication;

	public void createConnection(String domain, String username,
			String password, String host) throws SmbException,
			UnknownHostException {
		authentication = new NtlmPasswordAuthentication(domain, username,
				password);

		UniAddress uniaddress = UniAddress.getByName(host);
		SmbSession.logon(uniaddress, authentication);

	}

	public List<String> getListOfDirs(String host, String baseDir)
			throws MalformedURLException, SmbException {
		List<String> returnVal = new ArrayList<String>();
		SmbFile path = new SmbFile("smb://" + host + smbifyPath(baseDir),
				authentication);
		SmbFile[] pathContents = path.listFiles();
		for (SmbFile smbFile : pathContents) {
			returnVal.add(smbFile.getName());
		}
		return returnVal;
	}


	
	public boolean copyFileTo(String srcHost, String remoteFilePath,
			String localDir) throws IOException {
		boolean copySuccessful = false;

		File root = Environment.getExternalStorageDirectory();
		File localFilePath = new File(root.getPath() + "/" + localDir + "/"
				+ getArtistAlbum(remoteFilePath));
		if (!localFilePath.exists()) {
			// create folder structure if necessary
			localFilePath.mkdirs();
		}

		if (localFilePath.canWrite()) {
			// setup where we write too.
			File outputFile = new File(localFilePath,
					getFileName(remoteFilePath));
			FileOutputStream fos = new FileOutputStream(outputFile);
			BufferedOutputStream bos = new BufferedOutputStream(fos);

			// set up where we read from
			SmbFile smbFile = new SmbFile("smb://" + srcHost + remoteFilePath,
					authentication);

			SmbFileInputStream in = new SmbFileInputStream(smbFile);
			BufferedInputStream bis = new BufferedInputStream(in);

			// the actual copy
			int byte_;
			while ((byte_ = bis.read()) != -1) {
				bos.write(byte_);
			}

			bos.close();
			in.close();
			copySuccessful = true;
		}

		return copySuccessful;
	}

	private String smbifyPath(String input) {
		String returnVal = input;
		// TODO smbify path
		// do stuff correct and other balls
		// basically end up with /afasfs/afsfaf/
		returnVal = "/" + input + "/";
		return returnVal;
	}

	private String getFileName(String input) {
		String returnVal = input;
		// get the filename
		returnVal = input.substring(input.lastIndexOf('/'), input.length());
		return returnVal;
	}

	private String getArtistAlbum(String input) {
		String returnVal = input;
		// get the filename
		returnVal = input.substring(0, input.lastIndexOf('/'));
		return returnVal;
	}
	
	private String smbEscapeString (String input){
		String returnVal = input;
		returnVal = input.replace(" ", "\\ ");
		return returnVal;
	}
	
}
