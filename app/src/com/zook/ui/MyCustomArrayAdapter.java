package com.zook.ui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zook.ui.activities.DisplayShareScreenActivity;
import com.zook.ui.activities.R;

public class MyCustomArrayAdapter extends ArrayAdapter<String> {

	private transient final DisplayShareScreenActivity activity;
	private transient final List<String> directoryContents;
	private transient final List<Boolean> directoryStatuses;

	public MyCustomArrayAdapter(final DisplayShareScreenActivity activity, final Context context, final int resourceId,
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
		final ImageView icon = (ImageView) row.findViewById(R.id.rowIcon);

		if (directoryContents.get(position).equals("..")) {
			icon.setImageResource(R.drawable.up_folder);
		} else {
			if (directoryStatuses.get(position)) {
				icon.setImageResource(R.drawable.not_gray);
			} else {
				icon.setImageResource(R.drawable.gray);
			}
		}

		return row;
	}
}