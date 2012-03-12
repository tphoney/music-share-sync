package com.zook.ui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zook.ui.activities.DeleteScreenActivity;
import com.zook.ui.activities.R;

public class LocalContentsArrayAdapter extends ArrayAdapter<String> {

	private transient final DeleteScreenActivity activity;
	private transient final List<String> directoryContents;

	public LocalContentsArrayAdapter(final DeleteScreenActivity activity,
			final Context context, final int resourceId,
			final List<String> contents) {
		super(context, resourceId, contents);
		this.activity = activity;
		directoryContents = contents;
	}

	@Override
	public View getView(final int position, final View convertView,
			final ViewGroup parent) {
		final LayoutInflater inflater = this.activity.getLayoutInflater();
		final View row = inflater.inflate(R.layout.row, parent, false);
		final TextView label = (TextView) row.findViewById(R.id.rowTitle);
		label.setText(directoryContents.get(position));
		final ImageView icon = (ImageView) row.findViewById(R.id.rowIcon);

		if (directoryContents.get(position).equals("..")) {
			icon.setImageResource(R.drawable.up_folder);
		} else {
			icon.setImageResource(R.drawable.gray);
		}

		return row;
	}
}