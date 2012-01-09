package com.android;
import java.util.ArrayList;
import java.util.List;

public class Artist {
	String artistName;
	String artistPath;
	boolean artistSync;
	List <Album> def = new ArrayList<Album> ();
	
	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	public String getArtistPath() {
		return artistPath;
	}

	public void setArtistPath(String artistPath) {
		this.artistPath = artistPath;
	}

	public boolean isArtistSync() {
		return artistSync;
	}

	public void setArtistSync(boolean artistSync) {
		this.artistSync = artistSync;
	}
	
	public void addAlbum (Album newAlbum) {
		def.add(newAlbum);
	}
	
    public void setAllAlbumsSync(boolean setBoolean ) {	
    	
    }
}
