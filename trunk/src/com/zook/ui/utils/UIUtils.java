package com.zook.ui.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import android.app.Activity;
import android.app.Dialog;
import android.widget.TextView;

/**
 * Generic utility class for helper UI things
 * @author GB105983
 *
 */
public class UIUtils {

	/**
	 * Helper method to display the stack trace of some blowup point.
	 * Pass along the exception thrown somewhere in code, and the screen (that is any screen, even screens that 
	 * extends other SomeActivtywhatevers ... as long as the 
	 * super class somewhere up the tree implements andoird interface Activity) 
	 * and this method should render a popup textbox on that same screen.
	 * Note method is class level and static. 
	 * 
	 * @param exception
	 * @param screenActivity
	 */
	public static void displayErrorMessage(final Throwable exception, final Activity screenActivity ) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		exception.printStackTrace(printWriter);
		final String stacktrace = result.toString();
		final Dialog dialog = new Dialog(screenActivity);
		dialog.setTitle("Something went wrong: ");
		final TextView textview = new TextView(screenActivity);
		textview.setText(stacktrace);
		dialog.setContentView(textview);
		dialog.setCancelable(true);
		dialog.show();
	}
	
}
