package com.android;

public class Album {
	String albumName;
	String albumPath;
	boolean albumSync;
	
	public String getAlbumName() {
		return albumName;
	}
	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}
	public String getAlbumPath() {
		return albumPath;
	}
	public void setAlbumPath(String albumPath) {
		this.albumPath = albumPath;
	}
	public boolean isAlbumSync() {
		return albumSync;
	}
	public void setAlbumSync(boolean albumSync) {
		this.albumSync = albumSync;
	}
}
