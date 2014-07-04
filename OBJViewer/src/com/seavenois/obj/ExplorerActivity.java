package com.seavenois.obj;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Class extending {@link Activity} that provides a list of
 * folders containing obj models, and allow to get into these
 * folders and select a model to load.
 */
public class ExplorerActivity extends Activity{

	//Boolean indicating if the user is viewing the list of folder (true)
	//or one specific folder (false).
	private boolean rootView;
	
	//UI elements
	private LinearLayout llContent;	//Linear layout where the content will be loaded.
	private ProgressBar pbReload;	//ProgressBar, only shown when a Reload AsyncTask is running.
	private ImageView ivReload;		//Acts as button to Reload AsyncTask. Hidden while its in progress.
	private ImageButton btBack;		//Button to go back to the folder list. Only visible when in a specific folder.
	private TextView tvHeader;		//Title. Standard in the folder view, the folder name otherwise.
	
	/*
	 * Overridden method. Called when the activity is created.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		//Remove title bar
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
	    //Set layout
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_explorer);
		
		//Assign elements
		llContent = (LinearLayout) findViewById(R.id.llExplorerContent);
		pbReload = (ProgressBar) findViewById(R.id.pbReload);
		ivReload = (ImageView) findViewById(R.id.ivReload);
		btBack = (ImageButton) findViewById(R.id.btBack);
		tvHeader = (TextView) findViewById(R.id.tvExplorerHeader);
		
		//Set listeners
		btBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				loadFolders();				
			}
		});
		
		ivReload.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				reload();				
			}
		});
		
		//Load the folder View
		loadFolders();
	}
	
	/**
	 * Loads the list of folders containing obj files. Called when the activity 
	 * starts, and when coming back from a folder.
	 */
	private void loadFolders(){
		//Set variable
		rootView = true;
		
		//Set title
		tvHeader.setText(R.string.explorer_header);
		
		//Hide button
		btBack.setVisibility(View.GONE);
		
		//Clean layout
		llContent.removeAllViews();
		
		//Read database
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().getPath() + "/obj.sqlite", null);
		Cursor cur;
		
		//Search for the ones in db and delete the ones that dont exist
		cur = db.rawQuery("SELECT count(file), path FROM model GROUP BY path;", null);
		cur.moveToFirst();
		View row = null;
		TextView tvName, tvPath, tvCount;
		while (cur.isAfterLast() == false){
			row = getLayoutInflater().inflate(R.layout.row_directory, null);
			tvName = (TextView) row.findViewById(R.id.tvFolderName);
			tvPath = (TextView) row.findViewById(R.id.tvFolderPath);
			tvCount = (TextView) row.findViewById(R.id.tvFolderCount);
			if (cur.getInt(0) == 1)
				tvCount.setText(cur.getString(0) + getString(R.string.explorer_file_singular));
			else
				tvCount.setText(cur.getString(0) + getString(R.string.explorer_file_plural));
			tvName.setText(cur.getString(1).substring(cur.getString(1).lastIndexOf("/") + 1));
			tvPath.setText(cur.getString(1).substring(0, cur.getString(1).lastIndexOf("/")) + "/");
			final String str = cur.getString(1);
			row.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					openFolder(str);
				}
			});
			llContent.addView(row);
			cur.moveToNext();
    	}
		
		//Add the examples folder
		row = getLayoutInflater().inflate(R.layout.row_directory, null);
		tvName = (TextView) row.findViewById(R.id.tvFolderName);
		tvPath = (TextView) row.findViewById(R.id.tvFolderPath);
		tvCount = (TextView) row.findViewById(R.id.tvFolderCount);
		tvCount.setText("7" + getString(R.string.explorer_file_plural));
		tvName.setText(R.string.explorer_examples_folder);
		tvPath.setText(getString(R.string.explorer_examples_desciption));
		row.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				openExamplesFolder();
			}
		});
		llContent.addView(row);
		
		cur.close();
		db.close();
	}
	
	/**
	 * Loads the obj files in a specific folder, making them eligible to
	 * load the model.
	 * @param path The full path to the directory.
	 */
	private void openFolder(String path){
		//Set variable
		rootView = false;
		
		//Set title
		tvHeader.setText(path.substring(path.lastIndexOf("/") + 1) + "/");
		
		//Show button
		btBack.setVisibility(View.VISIBLE);
		
		//Clear layout
		llContent.removeAllViews();
		
		//Read database
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().getPath() + "/obj.sqlite", null);
		Cursor cur;
		
		//Search for the ones in db and delete the ones that dont exist
		cur = db.rawQuery("SELECT file, path, fname, size, vertices, faces, mtl, materials FROM model WHERE path = '" + path + "';", null);
		View row = null;
		String str;
		TextView tvName, tvDetail, tvMtl;
		cur.moveToFirst();
		while (cur.isAfterLast() == false){
			row = getLayoutInflater().inflate(R.layout.row_file, null);
			tvName = (TextView) row.findViewById(R.id.tvFileName);
			tvDetail = (TextView) row.findViewById(R.id.tvFileDetails);
			tvMtl = (TextView) row.findViewById(R.id.tvFileMtl);
			tvName.setText(cur.getString(2));
			if (cur.getInt(6) == 1){
				tvMtl.setVisibility(View.VISIBLE);
				str = String.format(getString(R.string.explorer_details_mtl), cur.getString(3), cur.getString(4), cur.getString(5), cur.getString(7));
			}
			else{
				tvMtl.setVisibility(View.GONE);
				str = String.format(getString(R.string.explorer_details_nomtl), cur.getString(3), cur.getString(4), cur.getString(5));
			}
			tvDetail.setText(str);
			final String st = cur.getString(0);
			row.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					//TODO
					Log.d("Selected file", st);
					Intent resultIntent = new Intent();
					resultIntent.putExtra("ok", true);
					resultIntent.putExtra("file", st);
					setResult(Activity.RESULT_OK, resultIntent);
					finish();
				}
			});
			llContent.addView(row);
			cur.moveToNext();
		}
		
		cur.close();
		db.close();
	}
	
	/**
	 * Open an examples folder, that is not really a folder, but a collection 
	 * of models included with the app, so everything is static.
	 */
	public void openExamplesFolder(){
		//Set variable
		rootView = false;
		
		//Set title
		tvHeader.setText(getString(R.string.explorer_examples_folder) + "/");
		
		//Show button
		btBack.setVisibility(View.VISIBLE);
		
		//Clear layout
		llContent.removeAllViews();
		
		//Array with examples
		final int[] example = {R.raw.rainbowobj, R.raw.cubeobj, R.raw.sphereobj, R.raw.starobj, R.raw.monkeyobj, R.raw.boltobj, R.raw.multipleobj};
		final int[] exampleMtl = {R.raw.rainbowmtl, R.raw.cubemtl, R.raw.spheremtl, R.raw.starmtl, R.raw.monkeymtl, R.raw.boltmtl, R.raw.multiplemtl};
		final int[] exampleName = {R.string.example_0, R.string.example_1, R.string.example_2, R.string.example_3, R.string.example_4, R.string.example_5, R.string.example_6};
		
		View row = null;
		TextView tvName, tvMtl;
		for (int i = 0; i < 7; i ++){
			row = getLayoutInflater().inflate(R.layout.row_file, null);
			tvName = (TextView) row.findViewById(R.id.tvFileName);
			tvMtl = (TextView) row.findViewById(R.id.tvFileMtl);
			tvName.setText(exampleName[i]);
			tvMtl.setVisibility(View.VISIBLE);
			final int j = i;
			row.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					//TODO
					Log.d("Selected example", getString(exampleName[j]));
					Intent resultIntent = new Intent();
					resultIntent.putExtra("ok", true);
					resultIntent.putExtra("file", "none");
					resultIntent.putExtra("resource", example[j]);
					resultIntent.putExtra("mtlResource", exampleMtl[j]);
					setResult(Activity.RESULT_OK, resultIntent);
					finish();
				}
			});
			llContent.addView(row);
		}
	}
	
	/**
	 * Start a AsyncTask to find obj files in the storage directory and sub directories.
	 */
	public void reload(){
		new Reload(this, ivReload, pbReload).execute();
	}
	
	/*
	 * Overridden method. If the user is viewing a specific folder,
	 * go back to the folder list. Otherwise, do the normal stuff (finish activity).
	 */
	@Override
	public void onBackPressed() {
		if (rootView == false)
			loadFolders();
		else
			super.onBackPressed();
	}
}
