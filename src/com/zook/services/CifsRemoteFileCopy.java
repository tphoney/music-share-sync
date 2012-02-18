package com.zook.services;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.zook.ui.activities.R;

import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbSession;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

public class CifsRemoteFileCopy implements RemoteFileCopyInterface {
	private static final String SMB__FILE_PREFIX = "smb://";
	private transient String smbHost;
	transient private NtlmPasswordAuthentication authentication;

	public void createConnection(final String domain, final String username,
			final String password, final String host) throws SmbException,
			UnknownHostException {
		this.smbHost = host;
		authentication = new NtlmPasswordAuthentication(domain, username,
				password);

		final UniAddress uniaddress = UniAddress.getByName(host);
		SmbSession.logon(uniaddress, authentication);

	}

	public List<String> getDirectoryContents(final String baseDir)
			throws MalformedURLException, SmbException {
		final List<String> returnVal = new ArrayList<String>();
		final SmbFile path = new SmbFile(SMB__FILE_PREFIX + smbHost
				+ smbifyPath(baseDir), authentication);
		final SmbFile[] pathContents = path.listFiles();
		for (SmbFile smbFile : pathContents) {
			returnVal.add(smbFile.getName());
		}
		return returnVal;
	}

	public List<Boolean> getDirectoryContentsSyncStatus(
			final String remoteFilePath, final String localDir)
			throws MalformedURLException, SmbException {
		final List<Boolean> returnVal = new ArrayList<Boolean>();

		// remote stuff
		final SmbFile path = new SmbFile(SMB__FILE_PREFIX + smbHost
				+ smbifyPath(remoteFilePath), authentication);
		final SmbFile[] pathContents = path.listFiles();
		final String template = SMB__FILE_PREFIX + smbHost;

		// local stuff
		final File root = Environment.getExternalStorageDirectory();
		final String localFilePath = root.getPath() + "/" + localDir;
		File localFile;
		for (SmbFile smbFile : pathContents) {
			localFile = new File(localFilePath + "/"
					+ smbFile.getPath().replaceAll(template, ""));
			if (localFile.exists()) {
				returnVal.add(true);
			} else {
				returnVal.add(false);
			}
		}
		return returnVal;
	}

	public boolean isLeaf(final String baseDir, final String itemClicked)
			throws MalformedURLException, SmbException {
		final SmbFile path = new SmbFile(SMB__FILE_PREFIX + smbHost
				+ smbifyPath(baseDir) + "/" + itemClicked, authentication);
		return path.isFile();
	}

	public void copyFileTo(final String remoteFilePath,
			final String remoteFileName, final String localDir,
			final Handler progressHandler) throws IOException {

		final File root = Environment.getExternalStorageDirectory();
		final File localFilePath = new File(root.getPath() + "/" + localDir
				+ "/" + remoteFilePath);
		if (!localFilePath.exists()) {
			// create folder structure if necessary
			localFilePath.mkdirs();
		}
		
		//set up files remote and local
		final File localFile = new File(localFilePath, remoteFileName);
		final SmbFile remoteFile = new SmbFile(SMB__FILE_PREFIX + smbHost
				+ smbifyPath(remoteFilePath) + "/"
				+ smbifyPath(remoteFileName), authentication);
		
		if (localFile.exists() && localFile.length() == remoteFile.length()) {
			progressHandler.sendMessage(Message.obtain(progressHandler,
					R.integer.progressDialogSetProgress,
					Integer.toString(100)));
		} else {
			if (localFilePath.canWrite()) {
				// setup where we write too.
				final FileOutputStream fos = new FileOutputStream(localFile);
				final BufferedOutputStream bos = new BufferedOutputStream(fos);

				// set up where we read from				
				final double fileSize = (double) remoteFile.length();
				final SmbFileInputStream sfis = new SmbFileInputStream(remoteFile);
				final BufferedInputStream bis = new BufferedInputStream(sfis);

				// the actual copy
				int byte_;
				double copied = 0;
				int percentageDone = 0;
				final byte[] buff = new byte[1048 * 4];

				while ((byte_ = bis.read(buff)) != -1) {
					bos.write(buff, 0, byte_);
					copied += (double) byte_;
					percentageDone = (int) ((copied / fileSize) * 100);
					// do something update progress bar
					progressHandler.sendMessage(Message.obtain(progressHandler,
							R.integer.progressDialogSetProgress,
							Integer.toString(percentageDone)));
				}
				bos.flush();
				bos.close();
				sfis.close();
			}
		}
	}

	public void copyFolder(final String remoteFilePath,
			final String remoteFolderName, final String localDir,
			final Handler progressHandler) throws IOException {

		final String fullRemotePath = smbifyPath(remoteFilePath) + "/"
				+ remoteFolderName;
		final SmbFile path = new SmbFile(SMB__FILE_PREFIX + smbHost
				+ fullRemotePath, authentication);
		final SmbFile[] pathContents = path.listFiles();
		float filesCopied = 1;
		for (SmbFile smbFile : pathContents) {
			progressHandler.sendMessage(Message.obtain(progressHandler,
					R.integer.progressDialogSetTitle,
					"Copying: " + smbFile.getName()));
			if (smbFile.isFile()) {
				copyFileTo(fullRemotePath, smbFile.getName(), localDir,
						progressHandler);
			}
			final int secondaryProgress = (int) ((filesCopied / pathContents.length) * 100);
			progressHandler.sendMessage(Message.obtain(progressHandler,
					R.integer.progressDialogSetSecondaryProgress, ""
							+ secondaryProgress));
			filesCopied++;
		}

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

	public String getParent(final String baseDir) throws MalformedURLException,
			SmbException {
		String returnVal;
		final SmbFile path = new SmbFile(SMB__FILE_PREFIX + smbHost
				+ smbifyPath(baseDir), authentication);
		returnVal = path.getParent();
		returnVal = returnVal.substring(returnVal.lastIndexOf(smbHost),
				returnVal.length() - 1);
		returnVal = returnVal.replaceAll(smbHost, "");
		returnVal += '/';
		return returnVal;
	}

}
