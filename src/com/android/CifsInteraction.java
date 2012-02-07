package com.android;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbSession;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

class CifsInteraction  {
	private Handler crapola;
	private NtlmPasswordAuthentication authentication;
	public void createConnection(String domain, String username,
			String password, String host) throws SmbException,
			UnknownHostException {
		authentication = new NtlmPasswordAuthentication(domain, username,
				password);

		UniAddress uniaddress = UniAddress.getByName(host);
		SmbSession.logon(uniaddress, authentication);

	}

	public List<String> getDirectoryContents(String host, String baseDir)
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

	public boolean isLeaf(String host, String baseDir, String itemClicked)
			throws MalformedURLException, SmbException {
		SmbFile path = new SmbFile("smb://" + host + smbifyPath(baseDir) + "/" + itemClicked,
				authentication);
		return path.isFile();
	}

	public long fileSize(String host, String baseDir)
			throws MalformedURLException, SmbException {
		SmbFile path = new SmbFile("smb://" + host + smbifyPath(baseDir),
				authentication);
		return path.length();
	}

	public boolean copyFileTo(String srcHost, String remoteFilePath,
			String remoteFileName, String localDir, Handler crap) throws IOException {
		boolean copySuccessful = false;

		File root = Environment.getExternalStorageDirectory();
		File localFilePath = new File(root.getPath() + "/" + localDir + "/"
				+ remoteFilePath);
		if (!localFilePath.exists()) {
			// create folder structure if necessary
			localFilePath.mkdirs();
		}

		if (localFilePath.canWrite()) {
			// setup where we write too.
			File outputFile = new File(localFilePath, remoteFileName);
			FileOutputStream fos = new FileOutputStream(outputFile);
			BufferedOutputStream bos = new BufferedOutputStream(fos);

			// set up where we read from
			SmbFile smbFile = new SmbFile("smb://" + srcHost
					+ smbifyPath(remoteFilePath) + "/"
					+ smbifyPath(remoteFileName), authentication);
			long fileSize = smbFile.length(); 
			SmbFileInputStream in = new SmbFileInputStream(smbFile);
			BufferedInputStream bis = new BufferedInputStream(in);

			// the actual copy
			int byte_;
			long copied = 0;
			double percentageComplete = 0;
			byte[] buff = new byte[2048];
			
			while ((byte_ = bis.read(buff)) != -1) {
				bos.write(buff,0,byte_);
				copied+= byte_;
				percentageComplete = (int)(((double)copied/(double)fileSize)*100);
				//do something update progress bar
				crap.sendMessage(Message.obtain(crap, 1, ""+ (int)(percentageComplete)));
			}

			bos.close();
			in.close();
			copySuccessful = true;
		}

		return copySuccessful;
	}

	public boolean copyFolder(String srcHost, String remoteFilePath,
			String remoteFolderName, String localDir) throws IOException {
		boolean copySuccessful = false;

		String fullRemotePath = smbifyPath(remoteFilePath) + "/"
				+ remoteFolderName;
		SmbFile path = new SmbFile("smb://" + srcHost + fullRemotePath,
				authentication);
		SmbFile[] pathContents = path.listFiles();
		for (SmbFile smbFile : pathContents) {
			if (smbFile.isFile()) {
				copyFileTo(srcHost, fullRemotePath, smbFile.getName(), localDir, null);
			}
		}

		return copySuccessful;
	}

	private String smbifyPath(String input) {
		String returnVal = input;

		// TODO replace with proper library
		if (returnVal.charAt(0) != '/') {
			returnVal = '/' + returnVal;
		}
		int length = returnVal.length() - 1;
		if (returnVal.charAt(length) != '/') {
			returnVal = returnVal + '/';
		}

		if (returnVal.contains(" ")) {
			returnVal.replaceAll(" ", "\\ ");
		}
		if (returnVal.contains("//")) {
			returnVal.replaceAll("/*", "/");
		}
		return returnVal;
	}

	public String getParent(String host, String baseDir)
			throws MalformedURLException, SmbException {
		String returnVal = baseDir;
		SmbFile path = new SmbFile("smb://" + host + smbifyPath(baseDir),
				authentication);
		returnVal = path.getParent();
		returnVal = returnVal.substring(returnVal.lastIndexOf(host),
				returnVal.length() - 1);
		returnVal = returnVal.replaceAll(host, "");
		returnVal = returnVal + '/';
		return returnVal;
	}

	public void setHandler(Handler updateProgress) {
		// TODO Auto-generated method stub
		crapola = updateProgress;
	}

}
