package com.seavenois.obj;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * Class extending {@link Activity} that shows and allow to change
 * app options. Changed are saved.
 */
public class SettingsActivity extends Activity{
	
	/**
	 * Request code to pass to activity {@link ColorActivity} to be retrieved
	 * by onActivityResult(int requestCode, int resultCode, Intent data). 
	 * Indicates that the result is intended to be set as vertex color.
	 */
	public static final int CODE_COLOR_VERTEX = 1803;
	
	/**
	 * Request code to pass to activity {@link ColorActivity} to be retrieved
	 * by onActivityResult(int requestCode, int resultCode, Intent data). 
	 * Indicates that the result is intended to be set as edge color.
	 */
	public static final int CODE_COLOR_EDGE = 1804;
	
	/**
	 * Request code to pass to activity {@link ColorActivity} to be retrieved
	 * by onActivityResult(int requestCode, int resultCode, Intent data). 
	 * Indicates that the result is intended to be set as face color.
	 */
	public static final int CODE_COLOR_FACE = 1805;
	
	/**
	 * Request code to pass to activity {@link ColorActivity} to be retrieved
	 * by onActivityResult(int requestCode, int resultCode, Intent data). 
	 * Indicates that the result is intended to be set as background color.
	 */
	public static final int CODE_COLOR_BACKGROUND = 1806;

	//UI elements
	private CheckBox cbDrawVertices, cbDrawEdges, cbDrawFaces, cbDrawBackground, cbUseMaterial;
	private TextView tvVertexColor, tvEdgeColor, tvFaceColor, tvBackgroundColor;
	private SeekBar sbEdgeSize, sbVertexSize, sbAlpha;
	
	//Shared preferences
	SharedPreferences prefs;
	Editor editor;
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
		
		//Set listeners
		//OnCheckedChangeListener for "Vertices" check box, set preference.
		cbDrawVertices.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
				editor.putBoolean("drawVertices", isChecked);
				editor.commit();
			}
		});
		
		//OnCheckedChangeListener for "Edges" check box, set preference.
		cbDrawEdges.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
				editor.putBoolean("drawEdges", isChecked);
				editor.commit();	
			}
		});
		
		//OnCheckedChangeListener for "Faces" check box, set preference.
		cbDrawFaces.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
				editor.putBoolean("drawFaces", isChecked);
				editor.commit();		
			}
		});
		
		//OnCheckedChangeListener for "Background" check box, set preference.
		cbDrawBackground.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
				editor.putBoolean("drawBackground", isChecked);
				editor.commit();		
			}
		});
		
		//OnCheckedChangeListener for "Use materials" check box, set preference.
		cbUseMaterial.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
				editor.putBoolean("useMaterial", isChecked);
				editor.commit();		
			}
		});
		
		
		
		//OnClickListener for "Vertex color" TextView, launch ColorActivity.
		tvVertexColor.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Intent i = new Intent(getBaseContext(), ColorActivity.class);
				i.putExtra("r", pColorVerticesR);
				i.putExtra("g", pColorVerticesG);
				i.putExtra("b", pColorVerticesB);
				startActivityForResult(i, CODE_COLOR_VERTEX);
			}
		});
		
		//OnClickListener for "Edge color" TextView, launch ColorActivity.
		tvEdgeColor.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Intent i = new Intent(getBaseContext(), ColorActivity.class);
				i.putExtra("r", pColorEdgesR);
				i.putExtra("g", pColorEdgesG);
				i.putExtra("b", pColorEdgesB);
				startActivityForResult(i, CODE_COLOR_EDGE);
			}
		});
		
		//OnClickListener for "Face color" TextView, launch ColorActivity.
		tvFaceColor.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Intent i = new Intent(getBaseContext(), ColorActivity.class);
				i.putExtra("r", pColorFacesR);
				i.putExtra("g", pColorFacesG);
				i.putExtra("b", pColorFacesB);
				startActivityForResult(i, CODE_COLOR_FACE);
			}
		});
		
		//OnClickListener for "Background color" TextView, launch ColorActivity.
		tvBackgroundColor.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Intent i = new Intent(getBaseContext(), ColorActivity.class);
				i.putExtra("r", pColorBackgroundR);
				i.putExtra("g", pColorBackgroundG);
				i.putExtra("b", pColorBackgroundB);
				startActivityForResult(i, CODE_COLOR_BACKGROUND);
			}
		});
		
		//OnSeekBarChangeListenr for "Vertex size" SeekBar, change preference.
		sbVertexSize.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				editor.putInt("sizeVertices", progress);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				editor.commit();
			}
			
		});
		
		//OnSeekBarChangeListenr for "Edge size" SeekBar, change preference.
		sbEdgeSize.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				editor.putInt("sizeEdges", progress);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				editor.commit();	
			}
			
		});
		
		//OnSeekBarChangeListenr for "Transparency" SeekBar, change preference.
		sbAlpha.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				editor.putInt("alpha", progress);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				editor.commit();
			}
			
		});
		
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
		editor = prefs.edit();
		
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
	
	/*
	 * Overridden method. Receives results from other activities.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		//Super
		super.onActivityResult(requestCode, resultCode, data);
		
		//Variables to store temporary data
		int r, g, b;
		Bitmap.Config conf;
		Bitmap bmp;
		Canvas canvas;
		Paint paint;
		
		//Switch code
		switch(requestCode) {
		
			//If its coming from ColorActivity to set the vertex color.
			case (CODE_COLOR_VERTEX):
				if (resultCode == Activity.RESULT_OK) {
					
					//Read data.
					r = data.getIntExtra("r", -1);
					g = data.getIntExtra("g", -1);
					b = data.getIntExtra("b", -1);
					
					//Check if its valid.
					if(r != -1 && g != -1 && b != -1){
						
						//Set color in preferences
						editor.putInt("colorVerticesR", r);
						editor.putInt("colorVerticesG", g);
						editor.putInt("colorVerticesB", b);
						editor.commit();
						
						//Set CompoundDrawable for color selector.
						conf = Bitmap.Config.ARGB_8888;
						bmp = Bitmap.createBitmap(50, 50, conf);
						canvas = new Canvas(bmp);
						paint = new Paint();
				        paint.setStyle(Paint.Style.FILL);
				        paint.setARGB(255, r, g, b);
				        canvas.drawRect(0, 0, 50, 50, paint);
						Drawable daux = new BitmapDrawable(getResources(), bmp);
						daux.setBounds(0, 0, 50, 50);
						tvVertexColor.setCompoundDrawables(daux ,null, null, null);
					}
				}
				break;
				
			//If its coming from ColorActivity to set the edge color.
			case (CODE_COLOR_EDGE):
				if (resultCode == Activity.RESULT_OK) {
					
					//Read data.
					r = data.getIntExtra("r", -1);
					g = data.getIntExtra("g", -1);
					b = data.getIntExtra("b", -1);
					
					//Check if its valid.
					if(r != -1 && g != -1 && b != -1){
						
						//Set color in preferences
						editor.putInt("colorEdgesR", r);
						editor.putInt("colorEdgesG", g);
						editor.putInt("colorEdgesB", b);
						editor.commit();
						
						//Set CompoundDrawable for color selector.
						conf = Bitmap.Config.ARGB_8888;
						bmp = Bitmap.createBitmap(50, 50, conf);
						canvas = new Canvas(bmp);
						paint = new Paint();
				        paint.setStyle(Paint.Style.FILL);
				        paint.setARGB(255, r, g, b);
				        canvas.drawRect(0, 0, 50, 50, paint);
						Drawable daux = new BitmapDrawable(getResources(), bmp);
						daux.setBounds(0, 0, 50, 50);
						tvEdgeColor.setCompoundDrawables(daux ,null, null, null);
					}
				}
				break;
				
			//If its coming from ColorActivity to set the face color.
			case (CODE_COLOR_FACE):
				if (resultCode == Activity.RESULT_OK) {
					
					//Read data.
					r = data.getIntExtra("r", -1);
					g = data.getIntExtra("g", -1);
					b = data.getIntExtra("b", -1);
					
					//Check if its valid.
					if(r != -1 && g != -1 && b != -1){
						
						//Set color in preferences
						editor.putInt("colorFacesR", r);
						editor.putInt("colorFacesG", g);
						editor.putInt("colorFacesB", b);
						editor.commit();
						
						//Set CompoundDrawable for color selector.
						conf = Bitmap.Config.ARGB_8888;
						bmp = Bitmap.createBitmap(50, 50, conf);
						canvas = new Canvas(bmp);
						paint = new Paint();
				        paint.setStyle(Paint.Style.FILL);
				        paint.setARGB(255, r, g, b);
				        canvas.drawRect(0, 0, 50, 50, paint);
						Drawable daux = new BitmapDrawable(getResources(), bmp);
						daux.setBounds(0, 0, 50, 50);
						tvFaceColor.setCompoundDrawables(daux ,null, null, null);
					}
				}
				break;
				
			//If its coming from ColorActivity to set the face color.
			case (CODE_COLOR_BACKGROUND):
				if (resultCode == Activity.RESULT_OK) {
					
					//Read data
					r = data.getIntExtra("r", -1);
					g = data.getIntExtra("g", -1);
					b = data.getIntExtra("b", -1);
					
					//Check if its valid.
					if(r != -1 && g != -1 && b != -1){
						
						//Set color in preferences
						editor.putInt("colorBackgroundR", r);
						editor.putInt("colorBackgroundG", g);
						editor.putInt("colorBackgroundB", b);
						editor.commit();
						
						//Set CompoundDrawable for color selector.
						conf = Bitmap.Config.ARGB_8888;
						bmp = Bitmap.createBitmap(50, 50, conf);
						canvas = new Canvas(bmp);
						paint = new Paint();
				        paint.setStyle(Paint.Style.FILL);
				        paint.setARGB(255, r, g, b);
				        canvas.drawRect(0, 0, 50, 50, paint);
						Drawable daux = new BitmapDrawable(getResources(), bmp);
						daux.setBounds(0, 0, 50, 50);
						tvBackgroundColor.setCompoundDrawables(daux ,null, null, null);
					}
				}
				break;
		}
	}
}
