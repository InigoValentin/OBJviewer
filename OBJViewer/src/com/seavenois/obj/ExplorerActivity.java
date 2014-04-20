package com.seavenois.obj;

import java.io.File;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ExplorerActivity extends Activity{

	boolean rootView;
	LinearLayout llContent;
	ProgressBar pbReload;
	ImageView ivReload;
	Button btBack;
	TextView tvHeader;
	
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
		btBack = (Button) findViewById(R.id.btBack);
		tvHeader = (TextView) findViewById(R.id.tvExplorerHeader);
		
		//Set listeners
		btBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO 				
			}
		});
		
		ivReload.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				reload();				
			}
		});
		
		loadFolders();
	}
	
	private void loadFolders(){
		//Set variable
		rootView = true;
		
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
			tvName.setText(cur.getString(1).substring(cur.getString(1).lastIndexOf("/")));
			tvPath.setText(cur.getString(1).substring(0, cur.getString(1).lastIndexOf("/")));
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
		
		cur.close();
		db.close();
	}
	
	private void openFolder(String path){
		//Clear layout
		llContent.removeAllViews();
		
		//Read database
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().getPath() + "/obj.sqlite", null);
		Cursor cur;
		
		//Search for the ones in db and delete the ones that dont exist
		cur = db.rawQuery("SELECT file, path, fname, size, vertices, faces, mtl, materials, name FROM model WHERE path = '" + path + "';", null);
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
					Log.d("Selectef file", st);
				}
			});
			llContent.addView(row);
			cur.moveToNext();
		}
		
		cur.close();
		db.close();
	}
	
	public void reload(){
		new Reload(this, ivReload, pbReload).execute();
	}
	
	@Override
	public void onBackPressed() {
		if (rootView != false)
			loadFolders();
		else
			super.onBackPressed();
	}
}
