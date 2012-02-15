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

	private final DisplayShareScreenActivity displayShareScreenActivity;
	List<String> currentWorkingDirectoryContents;
	List<Boolean> currentWorkingDirectoryContentsSync;

	public MyCustomAdapter(DisplayShareScreenActivity displayShareScreenActivity, Context context, int textViewResourceId,
			List<String> contents, List<Boolean> statuses) {
		super(context, textViewResourceId, contents);
		this.displayShareScreenActivity = displayShareScreenActivity;
		currentWorkingDirectoryContents = contents;
		currentWorkingDirectoryContentsSync = statuses;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// return super.getView(position, convertView, parent);
		LayoutInflater inflater = this.displayShareScreenActivity.getLayoutInflater();
		View row = inflater.inflate(R.layout.row, parent, false);
		TextView label = (TextView) row.findViewById(R.id.rowTitle);
		label.setText(currentWorkingDirectoryContents.get(position));
		ImageView icon = (ImageView) row.findViewById(R.id.icon);

		if (!currentWorkingDirectoryContents.get(position).equals("..")) {
			if (currentWorkingDirectoryContentsSync.get(position)) {
				icon.setImageResource(R.drawable.icon);
			} else {
				icon.setImageResource(R.drawable.icongray);
			}
		} else {
			icon = null;
		}

		return row;
	}
}