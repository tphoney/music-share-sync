package com.android;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
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
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

class CifsInteraction extends AsyncTask<String, String, String> {
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

	public boolean isLeaf(String host, String baseDir)
			throws MalformedURLException, SmbException {
		SmbFile path = new SmbFile("smb://" + host + smbifyPath(baseDir),
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
			String remoteFileName, String localDir) throws IOException {
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
				copyFileTo(srcHost, fullRemotePath, smbFile.getName(), localDir);
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

	private ProgressDialog dialog;
	private Context context;

	public void setContext(Context bla) {
		context = bla;
	}

	// can use UI thread here
	@Override
	protected void onPreExecute() {
		dialog = new ProgressDialog(context);
		dialog.setMessage("Downloading file..");
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setIndeterminate(false);

		dialog.show();
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... params) {
		String srcHost = params[0];
		String remoteFilePath = params[1];
		String remoteFileName = params[2];
		String localDir = params[3];

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
			try {
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

				while ((byte_ = bis.read()) != -1) {
					bos.write(byte_);
					copied += byte_;
					int percent = (int) ((copied * 100) / fileSize);

					publishProgress("" + percent);
				}

				bos.close();
				in.close();

			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SmbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null;
	}

	@Override
	protected void onProgressUpdate(String... progress) {
		dialog.setProgress(Integer.parseInt(progress[0]));
	}

	@Override
	protected void onPostExecute(String unused) {

		// super.onPostExecute(unused);
		dialog.dismiss();

	}

}
