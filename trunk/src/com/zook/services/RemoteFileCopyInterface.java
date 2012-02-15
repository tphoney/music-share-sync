package com.zook.services;

import java.util.List;

import android.os.Handler;

public interface RemoteFileCopyInterface {
	void createConnection(final String domain, final String username,
			final String password, final String host) throws Exception;

	List<String> getDirectoryContents(final String remoteFilePath)
			throws Exception;
	
	List<Boolean> getDirectoryContentsSyncStatus(final String remoteFilePath, final String destinationFolder)
			throws Exception;

	boolean isLeaf(final String remoteFilePath, final String itemClicked)
			throws Exception;

	void copyFileTo(final String remoteFilePath, final String fileToCopy,
			final String destinationFolder, final Handler progresHandler)
			throws Exception;

	void copyFolder(final String remoteFilePath, final String folderToCopy,
			final String destinationFolder, final Handler progressHandler)
			throws Exception;

	String getParent(final String folderName) throws Exception;
}
