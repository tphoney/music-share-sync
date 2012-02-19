package com.zook.ui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

public class ExceptionDialog {
	public ExceptionDialog(final Throwable exception, final Context context) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		exception.printStackTrace(printWriter);
		final String stacktrace = exception.getMessage();
		final Dialog dialog = new Dialog(context);
		dialog.setTitle("Something went wrong: ");
		final TextView textview = new TextView(context);
		textview.setText(stacktrace);
		dialog.setContentView(textview);
		dialog.setCancelable(true);
		dialog.show();
	}

}
