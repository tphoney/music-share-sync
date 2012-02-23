package com.zook.ui.view;

import java.util.Set;

import com.zook.tree.AbstractTreeViewAdapter;
import com.zook.tree.R;
import com.zook.tree.TreeNodeInfo;
import com.zook.tree.TreeStateManager;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SyncTreeStandardAdapter extends AbstractTreeViewAdapter<Long> {

    private final Set<Long> selected;

    private final OnCheckedChangeListener onCheckedChange = new OnCheckedChangeListener() {
        public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
            final Long id = (Long) buttonView.getTag();
            changeSelected(isChecked, id);
        }

    };

    private void changeSelected(final boolean isChecked, final Long id) {
        if (isChecked) {
            selected.add(id);
        } else {
            selected.remove(id);
        }
    }

    public SyncTreeStandardAdapter(final Activity treeViewListDemo,
            final Set<Long> selected,
            final TreeStateManager<Long> treeStateManager,
            final int numberOfLevels) {
        super(treeViewListDemo, treeStateManager, numberOfLevels);
        this.selected = selected;
    }

    @SuppressWarnings("unused")
	private String getDescription(final long id) {
    	final String desc = getManager().getNodeInfo(id).getDescription();
    	return desc;
    }

    @Override
    public View getNewChildView(final TreeNodeInfo<Long> treeNodeInfo) {
        final LinearLayout viewLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.demo_list_item, null);
        return updateView(viewLayout, treeNodeInfo);
    }

    @Override
    public LinearLayout updateView(final View view, final TreeNodeInfo<Long> treeNodeInfo) {
    	//get the defined ui components on the screen
        final LinearLayout viewLayout = (LinearLayout) view;
        final TextView descriptionView = (TextView) viewLayout.findViewById(R.id.demo_list_item_description);
        final CheckBox box = (CheckBox) viewLayout.findViewById(R.id.demo_list_checkbox);
        
        //setup initial values
        descriptionView.setText(treeNodeInfo.getDescription());
        
        box.setTag(treeNodeInfo.getId());
        if (treeNodeInfo.isWithChildren()) {
            box.setVisibility(View.GONE);
        } else {
            box.setVisibility(View.VISIBLE);
            box.setChecked(selected.contains(treeNodeInfo.getId()));
        }
        box.setOnCheckedChangeListener(onCheckedChange);
        return viewLayout;
    }

    @Override
    public void handleItemClick(final View view, final Object id) {
        final Long longId = (Long) id;
        final TreeNodeInfo<Long> info = getManager().getNodeInfo(longId);
        if (info.isWithChildren()) {
            super.handleItemClick(view, id);
        } else {
            final ViewGroup vg = (ViewGroup) view;
            final CheckBox cb = (CheckBox) vg
                    .findViewById(R.id.demo_list_checkbox);
            cb.performClick();
        }
    }

    public long getItemId(final int position) {
        return getTreeId(position);
    }
}