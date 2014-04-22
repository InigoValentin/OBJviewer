package com.seavenois.obj;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Class extending {@link Activity} that shows and allow to change
 * app options. Changed are saved.
 */
public class SettingsActivity extends Activity{

	//UI elements
	private CheckBox cbDrawVertices, cbDrawEdges, cbDrawFaces, cbDrawBackground, cbUseMaterial;
	private TextView tvVertexColor, tvEdgeColor, tvFaceColor, tvBackgroundColor;
	private SeekBar sbEdgeSize, sbVertexSize, sbAlpha;
	
	//Shared preferences
	SharedPreferences prefs;
	boolean pDrawVertices, pDrawEdges, pDrawFaces, pDrawBackground, pUseMaterial;
	int pColorVerticesR, pColorVerticesG, pColorVerticesB;
	int pColorEdgesR, pColorEdgesG, pColorEdgesB;
	int pColorFacesR, pColorFacesG, pColorFacesB;
	int pColorBackgroundR, pColorBackgroundG, pColorBackgroundB;
	int pSizeVertices, pSizeEdges, pAlpha;
	
	
	/*
	 * Overridden method. Called when the PreferenceActivity is called
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        
		//Remove title bar.
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
	    //Set layout.
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		//Assign UI elements.
		cbDrawVertices = (CheckBox) findViewById(R.id.cbpDrawVertices);
		cbDrawEdges = (CheckBox) findViewById(R.id.cbpDrawEdges);
		cbDrawFaces = (CheckBox) findViewById(R.id.cbpDrawFaces);
		cbDrawBackground = (CheckBox) findViewById(R.id.cbpDrawBackground);
		cbUseMaterial = (CheckBox) findViewById(R.id.cbpUseMaterial);
		tvVertexColor = (TextView) findViewById(R.id.tvpVertexColor);
		tvEdgeColor = (TextView) findViewById(R.id.tvpEdgeColor);
		tvFaceColor = (TextView) findViewById(R.id.tvpFaceColor);
		tvBackgroundColor = (TextView) findViewById(R.id.tvpBackgroundColor);
		sbVertexSize = (SeekBar) findViewById(R.id.sbpVertexSize);
		sbEdgeSize = (SeekBar) findViewById(R.id.sbpEdgeSize);
		sbAlpha = (SeekBar) findViewById(R.id.sbpAlpha);
		
		readPreferences();
		
    }
	
	/**
	 * Read the preferences, storing their value, and changing the UI elements to them.
	 */
	
	private void readPreferences(){
		
		//Useful variables to set the CompoudDrawable for some TextView.
		Drawable daux;	
		Bitmap.Config conf;
		Bitmap bmp;
		Canvas canvas;
		Paint paint;
		
		//Preference manager
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		//Get all preferences
		pDrawVertices = prefs.getBoolean("drawVertices", true);
		pDrawEdges = prefs.getBoolean("drawEdges", true);
		pDrawFaces = prefs.getBoolean("drawFaces", true);
		pDrawBackground = prefs.getBoolean("drawBackground", true);
		
		pColorVerticesR = prefs.getInt("colorVerticesR", 100);
		pColorVerticesG = prefs.getInt("colorVerticesG", 0);
		pColorVerticesB = prefs.getInt("colorVerticesB", 0);
		
		pColorEdgesR = prefs.getInt("colorEdgesR", 0);
		pColorEdgesG = prefs.getInt("colorEdgesG", 0);
		pColorEdgesB = prefs.getInt("colorEdgesB", 0);
		
		pColorFacesR = prefs.getInt("colorFacesR", 0);
		pColorFacesG = prefs.getInt("colorFacesG", 0);
		pColorFacesB = prefs.getInt("colorFacesB", 255);
		
		pColorBackgroundR = prefs.getInt("colorBackgroundR", 50);
		pColorBackgroundG = prefs.getInt("colorBackgroundG", 50);
		pColorBackgroundB = prefs.getInt("colorBackgroundB", 50);
		
		pSizeVertices = prefs.getInt("sizeVertices", 3);
		pSizeEdges = prefs.getInt("sizeEdges", 2);
		
		pAlpha = prefs.getInt("alpha", 120);
		
		pUseMaterial = prefs.getBoolean("useMaterial", true);
		
		//Set UI elements
		cbDrawVertices.setChecked(pDrawVertices);
		cbDrawEdges.setChecked(pDrawEdges);
		cbDrawFaces.setChecked(pDrawFaces);
		cbDrawBackground.setChecked(pDrawBackground);
		cbUseMaterial.setChecked(pUseMaterial);
		
		sbVertexSize.setProgress(pSizeVertices);
		sbEdgeSize.setProgress(pSizeEdges);
		sbAlpha.setProgress(pAlpha);
		
		//Set TextView CompoundDrawables
		conf = Bitmap.Config.ARGB_8888;
		bmp = Bitmap.createBitmap(50, 50, conf);
		canvas = new Canvas(bmp);
		paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setARGB(255, pColorVerticesR, pColorVerticesG, pColorVerticesB);
        canvas.drawRect(0, 0, 50, 50, paint);
		daux = new BitmapDrawable(getResources(), bmp);
		daux.setBounds(0, 0, 50, 50);
		tvVertexColor.setCompoundDrawables(daux ,null, null, null);
		
		bmp = Bitmap.createBitmap(50, 50, conf);
		canvas = new Canvas(bmp);
		paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
		paint.setARGB(255, pColorEdgesR, pColorEdgesG, pColorEdgesB);
        canvas.drawRect(0, 0, 50, 50, paint);
		daux = new BitmapDrawable(getResources(), bmp);
		daux.setBounds(0, 0, 50, 50);
		tvEdgeColor.setCompoundDrawables(daux ,null, null, null);
		
		bmp = Bitmap.createBitmap(50, 50, conf);
		canvas = new Canvas(bmp);
		paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
		paint.setARGB(255, pColorFacesR, pColorFacesG, pColorFacesB);
        canvas.drawRect(0, 0, 50, 50, paint);
		daux = new BitmapDrawable(getResources(), bmp);
		daux.setBounds(0, 0, 50, 50);
		tvFaceColor.setCompoundDrawables(daux ,null, null, null);
		
		bmp = Bitmap.createBitmap(50, 50, conf);
		canvas = new Canvas(bmp);
		paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
		paint.setARGB(255, pColorBackgroundR, pColorBackgroundG, pColorBackgroundB);
        canvas.drawRect(0, 0, 50, 50, paint);
		daux = new BitmapDrawable(getResources(), bmp);
		daux.setBounds(0, 0, 50, 50);
		tvBackgroundColor.setCompoundDrawables(daux ,null, null, null);
	}
}
