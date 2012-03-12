package com.zook.ui.activities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.zook.tree.InMemoryTreeStateManager;
import com.zook.tree.TreeBuilder;
import com.zook.tree.TreeStateManager;
import com.zook.tree.TreeViewList;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.zook.services.CifsRemoteFileCopy;
import com.zook.services.RemoteFileCopyInterface;
import com.zook.ui.activities.R;
import com.zook.ui.utils.UIUtils;
import com.zook.ui.view.ITreeViewActivity;
import com.zook.ui.view.SyncTreeStandardAdapter;
	
public class TreeTestScreenActivity extends Activity implements ITreeViewActivity {

	private final Set<Long> selected = new HashSet<Long>();

	private static final String TAG = TreeTestScreenActivity.class.getSimpleName();
	private TreeViewList treeView;

	@SuppressWarnings("unused")
	private static final int[] DEMO_NODES = new int[] { 0, 0, 1, 1, 1, 2, 2, 1,
			1, 2, 1, 0, 0, 0, 1, 2, 3, 2, 0, 0, 1, 2, 0, 1, 2, 0, 1 };
	private static final int LEVEL_NUMBER = 4;
	private TreeStateManager<Long> manager = null;
	private SyncTreeStandardAdapter simpleAdapter;
	@SuppressWarnings("unused")
	private TreeType treeType;
	private boolean collapsible;
	
	public static final String PREFS_NAME = "MusicShareSync.preferences";
	private transient RemoteFileCopyInterface cifsInteraction;
	private transient SharedPreferences settings;
	private transient String currentDirectory;

	/**
	 * Main layout and display creation hook
	 */
	@Override
	public void onCreate(final Bundle instanceState) {
		Log.d(TAG, "TreeTestScreenActivity>>>: oncreate()");
		super.onCreate(instanceState);
		TreeType newTreeType = null;
		boolean newCollapsible;

		manager = new InMemoryTreeStateManager<Long>();
		final TreeBuilder<Long> treeBuilder = new TreeBuilder<Long>(manager);

		// TODO: DW: DEMOABLE FLAVOUR OF THE CODE
		// we can uncomment the below for loop if we want to view a vanilla demoable set of nodes in the tree
		// remember to comment out the next 2 executable lines of code though!
		// for (int i = 0; i < DEMO_NODES.length; i++) {
		// 	treeBuilder.sequentiallyAddNextNode((long) i, DEMO_NODES[i]);
		// }
		
		//actual running recursive code
		connectToRemoteShare();
		populateNodeDescriptionsFromRemoteShare(treeBuilder);

		//setup display adapters and content 
		Log.d(TAG, manager.toString());
		newTreeType = TreeType.SIMPLE;
		newCollapsible = true;

		setContentView(R.layout.sync_tree);
		treeView = (TreeViewList) findViewById(R.id.syncTreeView);

		simpleAdapter = new SyncTreeStandardAdapter(this, selected, manager, LEVEL_NUMBER);
		setTreeAdapter(newTreeType);
		setCollapsible(newCollapsible);
		registerForContextMenu(treeView);
		
		Log.d(TAG, "TreeTestScreenActivity<<<: oncreate() exit");
	}
	
	
	/*
	 * setup connection to remote CIFS share for this activity.oncreate
	 */
	private void connectToRemoteShare()
	{
		cifsInteraction = new CifsRemoteFileCopy();

		settings = getSharedPreferences(PREFS_NAME, 0);
		currentDirectory = settings.getString("remoteBaseDirectory",
				getString(R.string.preferences_remote_basedir));
		try {
			cifsInteraction.createConnection(settings.getString("targetDomain",
					getString(R.string.preferences_target_domain)), settings
					.getString("remoteUsername",
							getString(R.string.preferences_remote_username)),
					settings.getString("remotePassword",
							getString(R.string.preferences_remote_password)),
					settings.getString("remoteHostname",
							getString(R.string.preferences_remote_hostname)));
		} catch (Exception e) {
			UIUtils.displayErrorMessage(e, this);
		}
	}
	
	
	/*
	 * Get the necessary data for the entire tree (as part of this activity.oncreate)
	 * Requires a flat loop to add level1 nodes to the base level0
	 * for each level1 node, recurse to the end of its subtree and add all subnodes
	 */
	protected void populateNodeDescriptionsFromRemoteShare(TreeBuilder<Long> treeBuilder) {
		Log.d(TAG, "populateNodeDescriptionsFromRemoteShare>>> adding treebuilder data");
		/*Initial 1 Level*/
		try {
			//get the base level directory structure
			//TODO: my dir structures arent right - check with TP how to append on basedir 
			final List<String> directoryContents = cifsInteraction.getDirectoryContents(currentDirectory);
			final List<Boolean> directoryContentsSyncStatus = cifsInteraction.getDirectoryContentsSyncStatus(currentDirectory, getString(R.string.preferences_local_basedir));
			
			//check for permissions
			if (!currentDirectory.equals(settings.getString("remoteBaseDirectory",getString(R.string.preferences_remote_basedir)))) {
				directoryContents.add(0, "..");
				directoryContentsSyncStatus.add (0, false);
			}
			
			//if we consider the root to be level 0, we must add all the level 1 entries first
			//for each level 1 entry we need to recurse down through all its child dirs, until leaf nodes are reached
			//keep adding each child to the subsequent 1 level up parent
			//keep 3 concepts in your head: id-unique for every node; childId=id of child node to parent, parentId=parentId for child
			long id =0;
			for(int i=0; i<directoryContents.size(); i++){
				id++;
				//add the node
				treeBuilder.addRelation( null, (long)id, directoryContents.get(i));
				//recursively add children to this node, returning the new unique id of next node to be added
				id = addNodesToTree(treeBuilder, id, currentDirectory + directoryContents.get(i));
			}
			
			//setListAdapter(new MyCustomAdapter(this, this, R.layout.row, directoryContents, directoryContentsSyncStatus));
		} catch (Exception e) {
			UIUtils.displayErrorMessage(e, this);
		}
		
		Log.d(TAG, "populateNodeDescriptionsFromRemoteShare<<< exit");
	}
	
	
	/*
	 * Recursive function (as part of this activity.oncreate)
	 * Take in the dir (as a full path) we want to add, and parent node's id form hich we want to add the subtree
	 */
	public long addNodesToTree(TreeBuilder<Long> treeBuilder, long id, String dir)
	{
		Log.d(TAG, "addNodesToTree>>>id="+id+", dir="+dir);
		long parentId = id;
		long childId = parentId;
		try
		{
			//provided we haven't reached the bottom of any subtree
			if (!cifsInteraction.isLeaf(dir))
			{
				//get the contents of the dir, to form a list of subnodes to this current node
				final List<String> directoryContents = cifsInteraction.getDirectoryContents(dir);
				final List<Boolean> directoryContentsSyncStatus = cifsInteraction.getDirectoryContentsSyncStatus(dir, getString(R.string.preferences_local_basedir));
				
				//check permissions? speak with TP?
				if (!currentDirectory.equals(settings.getString("remoteBaseDirectory",getString(R.string.preferences_remote_basedir)))) {
					directoryContents.add(0, "..");
					directoryContentsSyncStatus.add (0, false);
				}
				
				//loop over subnodes, add the subnode, then add any potential leaf nodes, alwyas return the incremented childid, which is the new unique id
				//of the next node to be added
				for(int i=0; i<directoryContents.size(); i++){
					childId++;
					treeBuilder.addRelation( parentId, (long)childId, directoryContents.get(i));
					childId = addNodesToTree(treeBuilder, childId, dir+directoryContents.get(i));
				}
			}
		}
		catch (Exception e) {
			UIUtils.displayErrorMessage(e, this);
		}
		Log.d(TAG, "addNodesToTree<<<returningId="+childId	);
		return childId;
	}
	

	/**
	 * Injector for tree view adapter - for now simple view
	 */
	public void setTreeAdapter(final TreeType newTreeType) {
		this.treeType = newTreeType;
		//commented out until declan gets his finger out :D
//		switch (newTreeType) {
//		case SIMPLE:
//			treeView.setAdapter(simpleAdapter);
//			break;
//		case FANCY:
//			treeView.setAdapter(simpleAdapter);
//			break;
//		default:
//			treeView.setAdapter(simpleAdapter);
//			break;
//		}
		treeView.setAdapter(simpleAdapter);
	}

	/**
	 * Fully collapsed/expanded setter
	 */
	public void setCollapsible(final boolean newCollapsible) {
		this.collapsible = newCollapsible;
		treeView.setCollapsible(this.collapsible);
	}
}