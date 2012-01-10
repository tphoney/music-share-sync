package com.android;

import java.util.ArrayList;
import java.util.List;

public class Collection {
	List<Artist> def = new ArrayList<Artist>();

	public void unSyncCollection() {

	}

	public void addArtist(Artist tobeAdded) {
		def.add(tobeAdded);
	}
}
