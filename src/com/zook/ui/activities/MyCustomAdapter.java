package com.zook.ui.activities;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyCustomAdapter extends ArrayAdapter<String> {

	private transient final DisplayShareScreenActivity activity;
	private transient final List<String> directoryContents;
	private transient final List<Boolean> directoryStatuses;

	public MyCustomAdapter(final DisplayShareScreenActivity activity, final Context context, final int resourceId,
			final List<String> contents, final List<Boolean> statuses) {
		super(context, resourceId, contents);
		this.activity = activity;
		directoryContents = contents;
		directoryStatuses = statuses;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		final LayoutInflater inflater = this.activity.getLayoutInflater();
		final View row = inflater.inflate(R.layout.row, parent, false);
		final TextView label = (TextView) row.findViewById(R.id.rowTitle);
		label.setText(directoryContents.get(position));
		final ImageView icon = (ImageView) row.findViewById(R.id.icon);

		if (directoryContents.get(position).equals("..")) {
			icon.setImageResource(R.drawable.icon);
		} else {
			if (directoryStatuses.get(position)) {
				icon.setImageResource(R.drawable.icon);
			} else {
				icon.setImageResource(R.drawable.icongray);
			}
		}

		return row;
	}
}