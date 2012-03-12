package com.zook.services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;

public class LocalCrap  {
	public List<String> getDirectoryContents(final String baseDir)
			throws RemoteFileCopyException {
		final List<String> returnVal = new ArrayList<String>();
		
		final File root = Environment.getExternalStorageDirectory();
		final String localFilePath = root.getPath() + "/" + baseDir;
		final File localpasdasd  = new File(localFilePath);
		final String[] pathContents = localpasdasd.list();
		for (String file : pathContents) {
			returnVal.add(file);
		}	
		
		return returnVal;
	}

	
	public boolean isLeaf(final String baseDir, final String itemClicked)
			 {
		return true;
	}
	
	public boolean isLeaf(final String fullpath)
			 {
		return true;
	}

	

	private String smbifyPath(final String input) {
		String returnVal = input;

		// TODO replace with proper library
		if (returnVal.charAt(0) != '/') {
			returnVal = '/' + returnVal;
		}
		final int length = returnVal.length() - 1;
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

	public String getParent(final String baseDir)
			 {
		String returnVal = "";
		
			return returnVal;

		
	}

	public void removeFileLocally(final String remoteFilePath,
			final String fileToCopy, final String destinationFolder)
			throws RemoteFileCopyException {
		final File root = Environment.getExternalStorageDirectory();
		final File localFilePath = new File(root.getPath() + "/"
				+ destinationFolder + "/" + remoteFilePath);

		// set up files remote and local
		final File localFile = new File(localFilePath, fileToCopy);

		if (localFile.exists()) {
			localFile.delete();
		}

	}

	public boolean fileExistsLocally(final String remoteFilePath,
			final String fileToCopy, final String destinationFolder)
			throws RemoteFileCopyException {
		final File root = Environment.getExternalStorageDirectory();
		final File localFilePath = new File(root.getPath() + "/"
				+ destinationFolder + "/" + remoteFilePath);

		final File localFile = new File(localFilePath, fileToCopy);
		return localFile.exists();
	}

}
