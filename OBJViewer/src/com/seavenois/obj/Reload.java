package com.seavenois.obj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
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

/**
 * Class extending {@link AsyncTask} to be run in the background.
 * It searches for .obj files in the storage directory and maintains 
 * a database with the ones that finds. 
 */
public class Reload extends AsyncTask<Void, Void, Void> {

	private Context context;
	private ImageView ivReload;
	private ProgressBar pbReload;
	private File[] file = new File[1000];
	private int fileIdx = 0;
	
	/**
	 * Class constructor.
	 * 
	 * @param ctx {@link Context} of the calling {@link Activity}.
	 * 
	 * @param iv An {@link ImageView} acting like a button to 
	 * start the task and that will be replaced for the 
	 * {@link ProgressBar} in the second parameter when the task starts. 
	 * Must be passed only if the task is called from {@link ExplorerActivity}.
	 *  
	 * @param pb An {@link ProgressBar} that will be replaced for the 
	 * {@link ImageView} in the first parameter when the task ends. Must be passed only
	 * if the task is called from {@link ExplorerActivity}. 
	 * 
	 * @see Context
	 * @see Activity
	 * @see ImageView
	 * @see ProgressBar
	 * @see ExplorerActivity
	 */
	public Reload(Activity ctx, ImageView iv, ProgressBar pb) {
        this.context = ctx;
        this.ivReload = iv;
        this.pbReload = pb;
    }
	
	/**
	 * Method that creates the database to store info about found models,
	 * if it does not already created.
	 */
	protected void createDatabase(){
		SQLiteDatabase db = context.openOrCreateDatabase(context.getFilesDir().getPath() + "/obj.sqlite", Context.MODE_PRIVATE, null);
		db.execSQL("CREATE TABLE IF NOT EXISTS model (file VARCHAR(300), path VARCHAR(300),	fname VARCHAR(100), size BIGINT, vertices INT, faces INT, mtl BOOLEAN, materials INT);");
		db.close();
	}
	
	/**
	 * Method that reads a obj file, getting info about the number of vertices, faces...
	 * It also checks if a mtl file exists for the given obj file. If exists, it will
	 * read from it the number of materials.
	 * Then , it will insert or update all the data into the database.
	 * @param file The .obj file.
	 * @see File
	 */
	private void readFile(File file){
		
		Cursor cur;
		String fieldFile, fieldPath, fieldFname;
		int fieldVertices, fieldFaces, fieldMaterials, fieldMtl;;
		long fieldSize;
		
		FileInputStream fis;
		BufferedReader input;
		
		//Assign some fields
		fieldFile = file.getAbsolutePath();
		fieldPath = file.getParent();
		fieldFname = file.getName();
		fieldSize = file.length();
		
		//Check for material file
		File mtlFile = new File(file.getAbsolutePath().toString().substring(0, file.getAbsoluteFile().toString().length() - 3) + "mtl"); //TODO upper case?
		if (mtlFile.exists())
			fieldMtl = 1;
		else
			fieldMtl = 0;
		
		//Read the obj files and complete the data
		fieldVertices = 0;
		fieldFaces = 0;
		try{
			fis = new FileInputStream(file);
			input =  new BufferedReader(new InputStreamReader(fis), 1024*8);
			try {
				String line = null; 
				while ((line = input.readLine()) != null){
					if (line.length() > 2){
						if (line.substring(0, 2).equals("v "))
							fieldVertices ++;
						else if (line.substring(0,2).equals("f "))
							fieldFaces ++;
					}
				}
			}
			finally {
				input.close();
			}
		}
		catch (FileNotFoundException ex) {
			Log.e("ERROR", "Couldn't find the file " + file.getAbsolutePath()  + " " + ex);
		}
		catch (IOException ex){
			Log.e("ERROR", "Error reading file " + file.getAbsolutePath() + " " + ex);
		}
		
		//If present, read the mtl files and complete the data
		fieldMaterials = 0;
		if (fieldMtl == 1){
			try{
				fis = new FileInputStream(mtlFile);
				input =  new BufferedReader(new InputStreamReader(fis), 1024*8);
				try {
					String line = null; 
					while ((line = input.readLine()) != null)
						if (line.length() > 6)
							if (line.substring(0, 6).equals("newmtl"))
								fieldMaterials ++;
				}
				finally {
					input.close();
				}
			}
			catch (FileNotFoundException ex) {
				Log.e("ERROR", "Couldn't find the file " + mtlFile.getAbsolutePath()  + " " + ex);
			}
			catch (IOException ex){
				Log.e("ERROR", "Error reading file " + mtlFile.getAbsolutePath() + " " + ex);
			}
		}
		
		//Check if exists in db
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(context.getFilesDir().getPath() + "/obj.sqlite", null);
		cur = db.rawQuery("SELECT file, path, fname, size, vertices, faces, mtl, materials FROM model WHERE file = '" + fieldFile + "'", null);
		if (cur.getCount() == 0)
			db.execSQL("INSERT INTO model VALUES('" + fieldFile + "', '" + fieldPath + "', '" + fieldFname + "', " + fieldSize + ", " + fieldVertices + ", " + fieldFaces + ", " + fieldMtl + ", " + fieldMaterials + ");");
		else{
			cur.moveToFirst();
			if (cur.getString(1).equals(fieldPath))
				db.execSQL("UPDATE model SET path = '" + fieldPath + "' WHERE file = '" + fieldFile + "';");
			if (cur.getString(2).equals(fieldFname))
				db.execSQL("UPDATE model SET fname = '" + fieldFname + "' WHERE file = '" + fieldFile + "';");
			if (cur.getInt(3) != fieldSize)
				db.execSQL("UPDATE model SET size = " + fieldSize + " WHERE file = '" + fieldFile + "';");
			if (cur.getInt(4) != fieldVertices)
				db.execSQL("UPDATE model SET vertices = " + fieldVertices + " WHERE file = '" + fieldFile + "';");
			if (cur.getInt(5) != fieldFaces)
				db.execSQL("UPDATE model SET faces = " + fieldFaces + " WHERE file = '" + fieldFile + "';");
			if (cur.getInt(6) != fieldMtl)
				db.execSQL("UPDATE model SET mtl = " + fieldMtl + " WHERE file = '" + fieldFile + "';");
			if (cur.getInt(7) != fieldMaterials)
				db.execSQL("UPDATE model SET materials = " + fieldMaterials + " WHERE file = '" + fieldFile + "';");
		}
		cur.close();
		db.close();
	}
	
	/**
	 * For each file found, call the {@link readFile(File file)} method.
	 * @see readFile(File file)
	 */
	private void populateFiles(){
		int i = 0;
		while (i < fileIdx){
			readFile(file[i]);
			i ++;
		}
	}
	
	/**
	 * Recursive function that looks for files ending in ".obj" in a directory and
	 * it's sub directories. For each found, a {@link Log} entry is written and
	 * the file is added to the {@link File} array to be processed later by
	 * {@link populateFiles()}
	 * @param dir The tod dir to search for files
	 * @see Log
	 * @see File
	 * @see populatedFiles()
	 */
	private void searchDirectory(File dir){
		for (File child : dir.listFiles() ){
			if (child.isFile()){
				String ext = child.getName().substring(child.getName().length() - 4);
				ext = ext.toLowerCase(Locale.getDefault());
				if (ext.equals(".obj")){
					file[fileIdx] = child;
					Log.i("File found", child.getAbsolutePath());
					//Increase file counter
					fileIdx ++;
				}
			}
			else if (child.isDirectory())
				searchDirectory(child);
		}
		return;
	}
	
	/*
	 * Overridden method. Called before the task starts.
	 * Shows and hide UI elements if necessary, and
	 * writes an entry in the Log.
	 */
	@Override
	protected void onPreExecute(){
		if (ivReload != null)
			ivReload.setVisibility(View.GONE);
		if (pbReload != null)
			pbReload.setVisibility(View.VISIBLE);
		Log.i("Search files", "Starting obj file search");
	}
	
	/*
	 * Overridden method. Called after the task finishes.
	 * Shows and hide UI elements if necessary, and
	 * writes an entry in the Log.
	 */
	@Override
	protected void onPostExecute(Void v){
		if (ivReload != null)
			ivReload.setVisibility(View.VISIBLE);
		if (pbReload != null)
			pbReload.setVisibility(View.GONE);
		Log.i("Search files", "Search finished");
	}
	
	/*
	 * Overridden method. The task itself.
	 */
	@Override
	protected Void doInBackground(Void... params) {
		
		Cursor cur;
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(context.getFilesDir().getPath() + "/obj.sqlite", null);
		File file;
		
		createDatabase();
		//Search for the ones in db and delete the ones that dont exist
		cur = db.rawQuery("SELECT file, path, fname, size, vertices, faces, mtl, materials FROM model", null);
		cur.moveToFirst();
		while (cur.isAfterLast() == false){
			file = new File(cur.getString(0));
			if (file.exists() == false)
				db.rawQuery("DELETE FROM model WHERE file = '" + cur.getString(0) + "'", null);
			cur.moveToNext();
    	}
		
		searchDirectory(Environment.getExternalStorageDirectory());
		populateFiles();
		
		cur.close();
		db.close();
		return null;
	}
}
