package com.zook.services;

import java.util.List;

import android.os.Handler;

public interface RemoteFileCopyInterface {
	void createConnection(final String domain, final String username,
			final String password, final String host) throws RemoteFileCopyException;

	List<String> getDirectoryContents(final String remoteFilePath)
			throws RemoteFileCopyException;
	
	List<Boolean> getDirectoryContentsSyncStatus(final String remoteFilePath, final String destinationFolder)
			throws RemoteFileCopyException;

	boolean isLeaf(final String remoteFilePath, final String itemClicked)
			throws RemoteFileCopyException;
	
	boolean fileExistsLocally(final String remoteFilePath, final String fileToCopy,
			final String destinationFolder)
			throws RemoteFileCopyException;

	void copyFileTo(final String remoteFilePath, final String fileToCopy,
			final String destinationFolder, final Handler progressHandler)
			throws RemoteFileCopyException;
	
	void removeFileLocally(final String remoteFilePath, final String fileToCopy,
			final String destinationFolder)
			throws RemoteFileCopyException;
	
	void copyFolder(final String remoteFilePath, final String folderToCopy,
			final String destinationFolder, final Handler progressHandler)
			throws RemoteFileCopyException;

	String getParent(final String folderName) throws RemoteFileCopyException;
}
