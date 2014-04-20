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
		
		//Read database
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().getPath() + "/obj.sqlite", null);
		File file;
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
			//TODO: Add touch listener to row
			llContent.addView(row);
			cur.moveToNext();
    	}
		
		cur.close();
		db.close();
	}
	
	public void reload(){
		new Reload(this, ivReload, pbReload).execute();
	}
}
