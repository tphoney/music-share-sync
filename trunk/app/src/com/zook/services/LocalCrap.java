package com.zook.services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jcifs.smb.SmbFile;

import android.os.Environment;

public class LocalCrap {
	static final String appsfolder = "MusicShareSync";
	final File root = Environment.getExternalStorageDirectory();

	public List<String> getDirectoryContents(final String baseDir) {
		final List<String> returnVal = new ArrayList<String>();
		final String localFilePath = root.getPath() + "/" + appsfolder + "/"
				+ baseDir;
		final File localpasdasd = new File(localFilePath);
		final String[] pathContents = localpasdasd.list();
		for (String file : pathContents) {
			returnVal.add(file);
		}

		return returnVal;
	}

	public boolean isLeaf(final String baseDir, final String itemClicked) {
		final File path = new File(root.getPath() + "/" + appsfolder + "/"
				+ baseDir + "/" + itemClicked);
		return path.isFile();
	}

	public String getParent(final String baseDir) {
		String returnVal;
		final File path = new File(root.getPath() + "/" + appsfolder + "/"
				+ baseDir);
		returnVal = path.getParent();
		returnVal = returnVal.replaceAll("/mnt/sdcard/" + appsfolder, "");
		returnVal += '/';
		return returnVal;
	}

	public void removeFileLocally(final String remoteFilePath,
			final String fileToCopy, final String destinationFolder) {
		final File root = Environment.getExternalStorageDirectory();
		final File localFilePath = new File(root.getPath() + "/"
				+ destinationFolder + "/" + remoteFilePath);

		// set up files remote and local
		final File localFile = new File(localFilePath, fileToCopy);

		if (localFile.exists()) {
			if (localFile.isDirectory()) {
				deleteDir(localFile);
			} else {
				localFile.delete();
			}
		}

	}

	private static boolean deleteDir(File node) {
		if (node.isDirectory()) {
			String[] children = node.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(node, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return node.delete();
	}

}
