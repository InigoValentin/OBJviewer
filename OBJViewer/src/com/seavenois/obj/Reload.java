package com.seavenois.obj;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class Reload extends AsyncTask<Void, Void, Void> {

	Context context;
	ImageView ivReload;
	ProgressBar pbReload;
	
	public Reload(Activity ctx, ImageView iv, ProgressBar pb) {
        this.context = ctx;
        this.ivReload = iv;
        this.pbReload = pb;
    }
	
	protected void createDatabase(){
		SQLiteDatabase db = context.openOrCreateDatabase(context.getFilesDir().getPath() + "/databases/gm.sqlite", Context.MODE_PRIVATE, null);
		db.execSQL("CREATE TABLE IF NOT EXISTS model (file VARCHAR(300), path VARCHAR(300),	fname VARCHAR(100), size BIGINT, vertices INT, faces INT, mtl BOOLEAN, materials INT, name VARCHAR(100);");
	}
	
	@Override
	protected void onPreExecute(){
		if (ivReload != null)
			ivReload.setVisibility(View.GONE);
		if (pbReload != null)
			pbReload.setVisibility(View.VISIBLE);
		Log.i("Search files", "Starting obj file search");
	}
	
	@Override
	protected void onPostExecute(Void v){
		if (ivReload != null)
			ivReload.setVisibility(View.VISIBLE);
		if (pbReload != null)
			pbReload.setVisibility(View.GONE);
		Log.i("Search files", "Search finished");
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		return null;
	}
}
