package com.zook.services;

public class RemoteFileCopyException extends Exception {

	RemoteFileCopyException (final Exception exception) {
		super (exception);
	}
	
	RemoteFileCopyException (final String message) {
		super (message);
	}
	private static final long serialVersionUID = 1L;

}
