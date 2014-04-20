package com.seavenois.obj;

import java.io.File;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class Reload extends AsyncTask<Void, Void, Void> {

	Context context;
	ImageView ivReload;
	ProgressBar pbReload;
	File[] file = new File[1000];
	int fileIdx = 0;
	
	public Reload(Activity ctx, ImageView iv, ProgressBar pb) {
        this.context = ctx;
        this.ivReload = iv;
        this.pbReload = pb;
    }
	
	protected void createDatabase(){
		SQLiteDatabase db = context.openOrCreateDatabase(context.getFilesDir().getPath() + "/obj.sqlite", Context.MODE_PRIVATE, null);
		db.execSQL("CREATE TABLE IF NOT EXISTS model (file VARCHAR(300), path VARCHAR(300),	fname VARCHAR(100), size BIGINT, vertices INT, faces INT, mtl BOOLEAN, materials INT, name VARCHAR(100));");
		db.close();
	}
	
	private void readFile(File file){
		
		Cursor cur;
		String fieldFile, fieldPath, fieldFname, fieldName;
		int fieldVertices, fieldFaces, fieldMaterials, fieldMtl;;
		long fieldSize;
		//Assign some fields
		fieldFile = file.getAbsolutePath();
		fieldPath = file.getParent();
		fieldFname = file.getName();
		fieldSize = file.length();
		
		//Check for material file
		File mtlFile = new File(file.getAbsoluteFile().toString().substring(0, file.getAbsoluteFile().toString().length() - 3) + ".mtl"); //TODO upper case?
		if (mtlFile.exists())
			fieldMtl = 1;
		else
			fieldMtl = 0;
		//TODO: Read the files and complete the data
		
		//Check if exists in db
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(context.getFilesDir().getPath() + "/obj.sqlite", null);
		cur = db.rawQuery("SELECT file FROM model WHERE file = '" + fieldFile + "'", null);
		if (cur.getCount() == 0)
			db.execSQL("INSERT INTO model VALUES('" + fieldFile + "', '" + fieldPath + "', '" + fieldFname + "', " + fieldSize + ", 0, 0, " + fieldMtl + ", 0, 'none');");
		else{
			//TODO: update fields
		}
		cur.close();
		db.close();
	}
	
	private void populateFiles(){
		int i = 0;
		while (i < fileIdx){
			readFile(file[i]);
			i ++;
		}
	}
	
	private void searchDirectory(File dir){
		for (File child : dir.listFiles() ){
			if (child.isFile()){
				String ext = child.getName().substring(child.getName().length() - 4);
				ext = ext.toLowerCase(Locale.getDefault());
				if (ext.equals(".obj")){
					file[fileIdx] = child;
					Log.i("File found", child.getAbsolutePath());
					fileIdx ++;
				}
			}
			else if (child.isDirectory())
				searchDirectory(child);
		}
		return;
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
		
		Cursor cur;
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(context.getFilesDir().getPath() + "/obj.sqlite", null);
		File file;
		
		createDatabase();
		//Search for the ones in db and delete the ones that dont exist
		cur = db.rawQuery("SELECT file, path, fname, size, vertices, faces, mtl, materials, name FROM model", null);
		cur.moveToFirst();
		while (cur.isAfterLast() == false){
			file = new File(cur.getString(0));
			if (file.exists() == false)
				db.rawQuery("DELETE FROM model WHERE file = '" + cur.getString(0) + "'", null);
    	}
		
		searchDirectory(Environment.getExternalStorageDirectory());
		populateFiles();
		//TODO: Recursively scan directories searching for obj files
		//TODO: Check if they are in db
		//TODO: Update if they are modified
		//TODO: Add if not present in db
		
		cur.close();
		db.close();
		return null;
	}
}
