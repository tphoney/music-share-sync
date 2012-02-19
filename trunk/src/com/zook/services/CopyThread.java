package com.zook.services;

import android.os.Handler;
import android.os.Message;

import com.zook.ui.activities.R;

public class CopyThread implements Runnable {
	private RemoteFileCopyInterface fileCopyInterface;
	private Handler handler;
	private transient final String baseFolder;
	private transient final String thingToCopy;
	private transient final boolean isFile;
	private transient final String localDir;

	public CopyThread(final RemoteFileCopyInterface fileCopyInterface,
			final Handler handler, final String baseFolder,
			final String thingToCopy, final boolean isFile,
			final String localDir) {
		this.fileCopyInterface = fileCopyInterface;
		this.handler = handler;
		this.baseFolder = baseFolder;
		this.thingToCopy = thingToCopy;
		this.isFile = isFile;
		this.localDir = localDir;
	}

	public void run() {
		try {
			if (isFile) {
				fileCopyInterface.copyFileTo(baseFolder, thingToCopy, localDir,
						handler);
			} else {
				fileCopyInterface.copyFolder(baseFolder, thingToCopy, localDir,
						handler);
			}
		} catch (Exception e) {
			handler.sendMessage(Message.obtain(handler,
					R.integer.progressSomethingWentWrong, e));
		}
	}
}
