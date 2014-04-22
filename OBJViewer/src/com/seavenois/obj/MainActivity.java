package com.seavenois.obj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The main activity of the app, includes the model viewer, the menu, and
 * methods to load any .obj file.
 */
public class MainActivity extends Activity {

	/**
	 * Maximum number of vertices that the program admits.
	 */
	public static final int MAX_VERTICES = 100000;
	
	/**
	 * Maximum number of faces that the program admits.
	 */
	public static final int MAX_FACES = 100000;
	
	/**
	 * Maximum number of materials that the program admits.
	 */
	public static final int MAX_MATERIALS = 100;
	
	/**
	 * Request code to pass to activity {@link ExplorerActivity} to be retrieved
	 * by onActivityResult(int requestCode, int resultCode, Intent data). 
	 */
	public static final int CODE_MODEL = 1802;
	
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
	private ImageView ivCanvas;		//The ImageView where the model will be drawn.
	private Button btLoadModel, btScreenshot, btSettings, btAbout;
	private LinearLayout llVertexPreferences, llEdgePreferences, llFacePreferences, llBackgroundPreferences;
	private CheckBox cbDrawVertices, cbDrawEdges, cbDrawFaces, cbDrawBackground, cbUseMaterial;
	private TextView tvVertexColor, tvEdgeColor, tvFaceColor, tvBackgroundColor;
	private SeekBar sbEdgeSize, sbVertexSize, sbAlpha;
	private MainLayout mLayout;		//Custom layout, parent of this activity view.
	
	//Arrays containing the model data. They are loaded once, and each modification.
	//transforms the arrays/
	private Vertex[] vert = new Vertex[MAX_VERTICES];			//Vertex array, with all the vertices in the .obj file.
	private Face[] face = new Face[MAX_FACES];					//Face array, with all the faces in the .obj file.
	private Material[] material = new Material[MAX_MATERIALS];	//Material array, with all the materials in the .mtl file.
	
	//The preference manager
	SharedPreferences prefs;
	
	//Variables containing the total amount of elements
	private int totalVerts, totalFaces, totalMaterials;
	
	//Variables to temporary store values to be used on listeners
	private boolean touching = false;	//Indicates if the canvas is being touched.
	private float prevX, prevY; 		//Indicate he coordinates of the touch event in the previous frame.
	private float prevDist = -1;		//Indicates the separation of the fingers in a pinch gesture in the previous frame.
	
	//Useful measures
	private float scale = 40;				//Value to scale the model, modified when zooming.
	private float rotationSpeed = 90;		//Speed at which the model will rotate.
	private float zoomSpeed = (float) 0.2;	//Speed at which the scale will change when zooming.
	private int vertexWidth = 3;			//Radius of the circle representing each vertex.
	private int edgeWidth = 2;				//Width of the lines representing the edges.
	private int alpha = 120;				//Alpha value (0-255) of the faces.
	
	//Colors for each element.
	private Color colorVertex, colorEdge, colorFace, colorBackground;
	
	//Booleans to determine which elements to draw.
	private boolean drawVertices = true;
	private boolean drawEdges = true;
	private boolean drawFaces = true;
	private boolean drawBackground = true;
	
	//Booleans to determine if material file is present, and if it is to be used.
	private boolean mtlFilePresent;
	private boolean useMaterial = true;
	
	/*
	 * Overridden method. Called when the activity starts. See in line comments.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Remove title bar.
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
	    //Set layout.
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
				
		//Assign ui elements.
		ivCanvas = (ImageView) findViewById(R.id.ivCanvas);
		mLayout = (MainLayout) findViewById(R.id.mainLayout);
		btLoadModel = (Button) findViewById(R.id.btLoadModel);
		btScreenshot = (Button) findViewById(R.id.btScreenshot);
		btSettings = (Button) findViewById(R.id.btSettings);
		btAbout = (Button) findViewById(R.id.btAbout);
		cbDrawVertices = (CheckBox) findViewById(R.id.cbDrawVertices);
		cbDrawEdges = (CheckBox) findViewById(R.id.cbDrawEdges);
		cbDrawFaces = (CheckBox) findViewById(R.id.cbDrawFaces);
		cbDrawBackground = (CheckBox) findViewById(R.id.cbDrawBackground);
		cbUseMaterial = (CheckBox) findViewById(R.id.cbUseMaterial);
		llVertexPreferences = (LinearLayout) findViewById(R.id.llVertexPreferences);
		llEdgePreferences = (LinearLayout) findViewById(R.id.llEdgePreferences);
		llFacePreferences = (LinearLayout) findViewById(R.id.llFacePreferences);
		llBackgroundPreferences = (LinearLayout) findViewById(R.id.llBackgroundPreferences);
		tvVertexColor = (TextView) findViewById(R.id.tvVertexColor);
		tvEdgeColor = (TextView) findViewById(R.id.tvEdgeColor);
		tvFaceColor = (TextView) findViewById(R.id.tvFaceColor);
		tvBackgroundColor = (TextView) findViewById(R.id.tvBackgroundColor);
		sbVertexSize = (SeekBar) findViewById(R.id.sbVertexSize);
		sbEdgeSize = (SeekBar) findViewById(R.id.sbEdgeSize);
		sbAlpha = (SeekBar) findViewById(R.id.sbAlpha);
		
		//Preference manager
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		//Assign preferences
		vertexWidth = prefs.getInt("sizeVertices", 3);			//Radius of the circle representing each vertex.
		edgeWidth = prefs.getInt("sizeEdges", 2);				//Width of the lines representing the edges.
		alpha = prefs.getInt("alpha", 120);
		drawVertices = prefs.getBoolean("drawVertices", true);
		drawEdges = prefs.getBoolean("drawEdges", true);
		drawFaces = prefs.getBoolean("drawFAces", true);
		drawBackground = prefs.getBoolean("drawBackground", true);
		useMaterial = prefs.getBoolean("useMaterial", true);
		colorVertex = new Color(prefs.getInt("colorVerticesR", 100), prefs.getInt("colorVerticesG", 0), prefs.getInt("colorVerticesB", 0));
		colorEdge = new Color(prefs.getInt("colorEdgesR", 0), prefs.getInt("colorEdgesG", 0), prefs.getInt("colorEdgesB", 0));
		colorFace = new Color(prefs.getInt("colorFacesR", 0), prefs.getInt("colorFacesG", 0), prefs.getInt("colorFacesB", 255), alpha);
		colorBackground = new Color(prefs.getInt("colorBackgroundR", 50), prefs.getInt("colorBackgroundG", 50), prefs.getInt("colorBackgroundB", 50));
		
		//Change UI elements depending on preferences
		cbDrawVertices.setChecked(drawVertices);
		cbDrawEdges.setChecked(drawEdges);
		cbDrawFaces.setChecked(drawFaces);
		cbDrawBackground.setChecked(drawBackground);
		cbUseMaterial.setChecked(useMaterial);
		if (drawVertices == false)
			llVertexPreferences.setVisibility(View.GONE);
		if (drawEdges == false)
			llEdgePreferences.setVisibility(View.GONE);
		if (drawFaces == false)
			llFacePreferences.setVisibility(View.GONE);
		if (drawBackground == false)
			llBackgroundPreferences.setVisibility(View.GONE);
		if (useMaterial)
			tvFaceColor.setVisibility(View.GONE);
		
		//Useful variables to set the CompoudDrawable for some TextView.
		Drawable daux;	
		Bitmap.Config conf;
		Bitmap bmp;
		Canvas canvas;
		Paint paint;
		
		//OnClickListener for "Load Model" button, launch ExplorerActivity.
		btLoadModel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Intent i = new Intent(getBaseContext(), ExplorerActivity.class);
				startActivityForResult(i, CODE_MODEL);
			}
		});
		
		//OnClickListener for "Screenshot" button, call screenshot().
		btScreenshot.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				screenshot();
			}
		});
		
		//OnClickListener for "Settings" button, start SettingsActivity.
		btSettings.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Intent i = new Intent(getBaseContext(), SettingsActivity.class);
			    startActivity(i);
			}
		});
		
		//OnClickListener for "About" button, TODO
		btAbout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				//TODO
			}
		});
		
		//OnCheckedChangeListener for "Vertices" check box, set boolean, show/hide some preferences and call draw().
		cbDrawVertices.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
				if (isChecked == true){
					llVertexPreferences.setVisibility(View.VISIBLE);
					drawVertices = true;
				}
				else{
					llVertexPreferences.setVisibility(View.GONE);
					drawVertices = false;
				}
				draw();
			}
		});
		
		//OnCheckedChangeListener for "Edges" check box, set boolean, show/hide some preferences and call draw().
		cbDrawEdges.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
				if (isChecked == true){
					llEdgePreferences.setVisibility(View.VISIBLE);
					drawEdges = true;
				}
				else{
					llEdgePreferences.setVisibility(View.GONE);
					drawEdges = false;
				}
				draw();		
			}
		});
		
		//OnCheckedChangeListener for "Faces" check box, set boolean, show/hide some preferences and call draw().
		cbDrawFaces.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
				if (isChecked == true){
					llFacePreferences.setVisibility(View.VISIBLE);
					drawFaces = true;
				}
				else{
					llFacePreferences.setVisibility(View.GONE);
					drawFaces = false;
				}
				draw();			
			}
		});
		
		//OnCheckedChangeListener for "Background" check box, set boolean, show/hide some preferences and call draw().
		cbDrawBackground.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
				if (isChecked == true){
					llBackgroundPreferences.setVisibility(View.VISIBLE);
					drawBackground = true;
				}
				else{
					llBackgroundPreferences.setVisibility(View.GONE);
					drawBackground = false;
				}
				draw();			
			}
		});
		
		//OnCheckedChangeListener for "Use materials" check box (visible only if cbDrawFaces is checked), set boolean, show/hide some preferences and call draw().
		cbUseMaterial.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
				if (isChecked == true){
					tvFaceColor.setVisibility(View.GONE);
					useMaterial = true;
				}
				else{
					tvFaceColor.setVisibility(View.VISIBLE);
					useMaterial = false;
				}
				draw();			
			}
		});
		
		//Set CompoundDrawable for "Vertex color" TextView, filled with the appropriate color.
		conf = Bitmap.Config.ARGB_8888;
		bmp = Bitmap.createBitmap(50, 50, conf);
		canvas = new Canvas(bmp);
		paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setARGB(colorVertex.getA(), colorVertex.getR(), colorVertex.getG(), colorVertex.getB());
        canvas.drawRect(0, 0, 50, 50, paint);
		daux = new BitmapDrawable(getResources(), bmp);
		daux.setBounds(0, 0, 50, 50);
		tvVertexColor.setCompoundDrawables(daux ,null, null, null);
		tvVertexColor.setCompoundDrawablePadding(10);
		
		//Set CompoundDrawable for "Edge color" TextView, filled with the appropriate color.
		conf = Bitmap.Config.ARGB_8888;
		bmp = Bitmap.createBitmap(50, 50, conf);
		canvas = new Canvas(bmp);
		paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setARGB(colorEdge.getA(), colorEdge.getR(), colorEdge.getG(), colorEdge.getB());
        canvas.drawRect(0, 0, 50, 50, paint);
		daux = new BitmapDrawable(getResources(), bmp);
		daux.setBounds(0, 0, 50, 50);
		tvEdgeColor.setCompoundDrawables(daux ,null, null, null);
		tvEdgeColor.setCompoundDrawablePadding(10);
		
		//Set CompoundDrawable for "Face color" TextView, filled with the appropriate color.
		conf = Bitmap.Config.ARGB_8888;
		bmp = Bitmap.createBitmap(50, 50, conf);
		canvas = new Canvas(bmp);
		paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setARGB(colorFace.getA(), colorFace.getR(), colorFace.getG(), colorFace.getB());
        canvas.drawRect(0, 0, 50, 50, paint);
		daux = new BitmapDrawable(getResources(), bmp);
		daux.setBounds(0, 0, 50, 50);
		tvFaceColor.setCompoundDrawables(daux ,null, null, null);
		tvFaceColor.setCompoundDrawablePadding(10);
		
		//Set CompoundDrawable for "Background color" TextView, filled with the appropriate color.
		conf = Bitmap.Config.ARGB_8888;
		bmp = Bitmap.createBitmap(50, 50, conf);
		canvas = new Canvas(bmp);
		paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setARGB(colorBackground.getA(), colorBackground.getR(), colorBackground.getG(), colorBackground.getB());
        canvas.drawRect(0, 0, 50, 50, paint);
		daux = new BitmapDrawable(getResources(), bmp);
		daux.setBounds(0, 0, 50, 50);
		tvBackgroundColor.setCompoundDrawables(daux ,null, null, null);
		tvBackgroundColor.setCompoundDrawablePadding(10);
		
		//OnClickListener for "Vertex color" TextView, launch ColorActivity.
		tvVertexColor.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Intent i = new Intent(getBaseContext(), ColorActivity.class);
				i.putExtra("r", colorVertex.getR());
				i.putExtra("g", colorVertex.getG());
				i.putExtra("b", colorVertex.getB());
				startActivityForResult(i, CODE_COLOR_VERTEX);
			}
		});
		
		//OnClickListener for "Edge color" TextView, launch ColorActivity.
		tvEdgeColor.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Intent i = new Intent(getBaseContext(), ColorActivity.class);
				i.putExtra("r", colorEdge.getR());
				i.putExtra("g", colorEdge.getG());
				i.putExtra("b", colorEdge.getB());
				startActivityForResult(i, CODE_COLOR_EDGE);
			}
		});
		
		//OnClickListener for "Face color" TextView, launch ColorActivity.
		tvFaceColor.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Intent i = new Intent(getBaseContext(), ColorActivity.class);
				i.putExtra("r", colorFace.getR());
				i.putExtra("g", colorFace.getG());
				i.putExtra("b", colorFace.getB());
				startActivityForResult(i, CODE_COLOR_FACE);
			}
		});
		
		//OnClickListener for "Background color" TextView, launch ColorActivity.
		tvBackgroundColor.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Intent i = new Intent(getBaseContext(), ColorActivity.class);
				i.putExtra("r", colorBackground.getR());
				i.putExtra("g", colorBackground.getG());
				i.putExtra("b", colorBackground.getB());
				startActivityForResult(i, CODE_COLOR_BACKGROUND);
			}
		});
		
		//OnSeekBarChangeListenr for "Vertex size" SeekBar, change value and call draw().
		sbVertexSize.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				vertexWidth = progress + 1;
				draw();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			
		});
		
		//OnSeekBarChangeListenr for "Edge size" SeekBar, change value and call draw().
		sbEdgeSize.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				edgeWidth = progress + 1;
				draw();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			
		});
		
		//OnSeekBarChangeListenr for "Transparency" SeekBar, change value and call draw().
		sbAlpha.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				alpha = progress;
				draw();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			
		});
		
		//OnTouchListener for the main canvas, listening for various events.
		ivCanvas.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				//Get screen dimensions
				DisplayMetrics metrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(metrics);
				int w = metrics.widthPixels;
				
				//If the touch event is in the left edge of the canvas or the menu is displayed, 
				//return and pass the event to the parent so the menu can be opened or closed.
				if (event.getX() < 0.1 * w || mLayout.isMenuShown())
					return false;
				switch (event.getAction()) {
				
					//Screen starting to be touched, set control variable and store position.
					case MotionEvent.ACTION_DOWN:
						touching = true;
						prevX = event.getX();
						prevY = event.getY();
						break;
						
					//Screen stopped being touched, set control variable.
					case MotionEvent.ACTION_UP:
						touching = false;
						break;
						
					//Moving the finger across the screen
					case MotionEvent.ACTION_MOVE:
						
						//If only one finger, rotate, redraw, and store position.
						if (event.getPointerCount() == 1){
							if (touching){
								rotateX(prevX - event.getX());
								rotateY(prevY - event.getY());
								draw();
								prevX = event.getX();
								prevY = event.getY();
							}
						}
						
						//If two (or more) fingers, zoom (redraws automatically) and store position.
						else if (event.getPointerCount() > 1){
							float dist = (float) Math.sqrt(Math.abs((event.getX(1) - event.getX(0)) * (event.getX(1) - event.getX(0) + (event.getY(1) - event.getY(0)) * (event.getY(1) - event.getY(0)))));
							zoom(prevDist - dist);
							prevDist = dist;
						}
						break;
				}
				return true;
			}
		});
		
		//Load the default model
		loadModel(null);
		
		//Initiate background search for files
		reload();
	}

	/**
	 * Initializes the sequence to load a model. If a filename is specified
	 * it will load that model, otherwise it will load the file that  
	 * opened the app, and if it doesn't exists, it will load a default
	 * model. 
	 *
	 * @param  model The filename with full path to the file to be loaded.
	 * If null, it will load a default model.
	 */
	public void loadModel(String model){
		
		//No file selected or selected outside the app
		if (model == null){
			if (getIntent().getAction().equals(Intent.ACTION_VIEW)){
				//Load selected file
				String obj, mtl;
				Log.d("Opening model", getIntent().getData().getEncodedPath());
				obj = getIntent().getData().getEncodedPath();
				mtl = obj.substring(0, obj.length() - 3) + "mtl";
				mtlFilePresent = fileExists(mtl);
				if (mtlFilePresent == true)
					readMtl(mtl, false);
				readFile(obj, false);
			}
			else{
				//Load default file
				Log.d("Opening model", "DEFAULT");
				mtlFilePresent = true;
				readMtl(null, true);
				readFile(null, true);
			}
		}
		
		//File selected in the built in file manager
		else{
			//Load selected file
			String obj, mtl;
			Log.d("Opening model", model);
			obj = model;
			mtl = obj.substring(0, obj.length() - 3) + "mtl";
			mtlFilePresent = fileExists(mtl);
			if (mtlFilePresent == true){
				readMtl(mtl, false);
				useMaterial = true;
			}
			else
				useMaterial = false;
			readFile(obj, false);
		}
		draw();
	}
	
	/**
	 * Given a file name with an absolute path, it checks if the file exists.
	 *
	 * @param  filename  Filename, with its full path, to the file to check.
	 * @return      true if the file exists, false otherwise.
	 */
	private boolean fileExists(String filename){
		File file = new File(filename);
		Log.d(filename, Boolean.toString(file.exists()));
		if(file.exists())      
			return true;
		else
			return false;
	}
	
	/**
	 * Draws the loaded model into the canvas. Must be called after loading
	 * a model and after every transformation, such as rotate or zoom.
	 */
	public void draw(){
		//Sort the face array, so the faces in the back are drawled first (Face implements Comparable)
		Arrays.sort(face, 0, totalFaces);
		//Create the bitmap.
		Bitmap bmp = createBitMap();
		//Draw the image in the canvas.
		ivCanvas.setImageBitmap(bmp);
	}
	
	/**
	 * Saves a file with the current view of the model, in the folder  
	 * "Pictures/ObjViewer/", located in the external storage directory.
	 * If the directory does not exist, it will also create it.
	 * Success and errors will be notified with a {@link Toast} and a
	 * {@link Log} entry.
	 *
	 * @see         Toast
	 * @see         Log
	 */
	public void screenshot(){
		Arrays.sort(face, 0, totalFaces);
		//TODO: Calculate a new scale.
		Bitmap bmp = createBitMap();
		
		File dir = new File(Environment.getExternalStorageDirectory() + "/Pictures/ObjViewer"); 

	    //Create directory if it does not exist.
	    if (dir.exists() == false)
	    	if (dir.mkdirs() == false){
	    		Log.e("I/O error", "Error creating directory " + dir.getPath());
				return;
	    	}

	    // Create a media file name.
	    String ts = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
	    String fName = ts +".png";
	    File file = new File(dir.getPath() + File.separator + fName);  
		
	    //Try to save the bitmap to a file.
	    //Toast and Log if error or success.
		try{
			FileOutputStream fos = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
			fos.close();
			Toast toast = Toast.makeText(this, getString(R.string.toast_file_saved) + dir.getPath() + File.separator + fName, Toast.LENGTH_LONG);
			toast.show();
			Log.i("Screenshot", "Saved file " + getString(R.string.toast_file_saved) + dir.getPath() + File.separator + fName);
		}
		catch (FileNotFoundException e) {
			Log.e("I/O error", "File not found: " + e.getMessage());
			Toast toast = Toast.makeText(this, getString(R.string.toast_file_error) + dir.getPath() + File.separator + fName, Toast.LENGTH_LONG);
			toast.show();
		}
		catch (IOException e) {
			Log.e("I/O error", "Unable to open file: " + e.getMessage());
			Toast toast = Toast.makeText(this, getString(R.string.toast_file_error) + dir.getPath() + File.separator + fName, Toast.LENGTH_LONG);
			toast.show();
		}  
	}
	
	/**
	 * Zooms in or out the current model based on a factor. This factor 
	 * is calculated by the pinch gesture over the canvas, but can be 
	 * called manually. Note that the amount of zoom depends on the parameter
	 * passed and the current value of {@link getZoomSpeed()}.
	 * {@link draw()} is automatically called when the model is zoomed.
	 *
	 * @param  factor Amount of zoom. Positive values will cause a zoom in
	 * and negative values in a zoom out.
	 * @see setZoomSpeed(float speed)
	 * @see getZoomSpeed()
	 * @see draw()
	 */
	public void zoom(float factor){
		//TODO: Improve, has to be smooth, and if possible, faded in and out
		if (factor > 0)
			scale = (float) (scale - scale * getZoomSpeed());
		else if (factor < 0)
			scale = (float) (scale + scale * getZoomSpeed());
		draw();
	}
	
	/**
	 * Sets the speed at witch the model will be zoomed in or out. 
	 * If an invalid value is passed, a {@link Log} entry will be written, 
	 * ant the value will not be changed.
	 *
	 * @param  speed  Only values bigger than 0 and smaller than 1 are allowed.
	 * The bigger the speed, the faster the model will zoom in or out.
	 * @see zoom(float factor)
	 * @see getZoomSpeed()
	 */
	public void setZoomSpeed(float speed){
		if (speed > 0 && speed < 1)
			zoomSpeed = speed;
		else
			Log.e("Zoom speed", "Ilegal value " + speed + ", only values bigger than 0 and smaller than 1 are allowed. Left unchanged...");
	}
	
	/**
	 * Gets the speed at witch the model will be zoomed in or out. 
	 *
	 * @return The current zoom speed. 
	 * 
	 * @see zoom(float factor)
	 * @see setZoomSpeed(float speed)
	 */
	public float getZoomSpeed(){
		return zoomSpeed;
	}
	
	/**
	 * Rotates the current model along the X axis based on a factor. This factor 
	 * is calculated by the drag gesture over the canvas, but can be 
	 * called manually. Note that the amount of rotation depends on the parameter
	 * passed and the current value of {@link getRotationSpeed()}.
	 * Usually this function is called along with {@link rotateY(float des)}, and
	 * {@link draw()} is not automatically called when the model is rotated: It 
	 * must be called after the two methods. 
	 *
	 * @param  factor Amount of rotation. Positive values will cause a rotation to 
	 * the right and negative values a rotation to the left. It does not correspond with
	 * actual degrees.
	 * @see rotateY(float des)
	 * @see setRotationSpeed(float speed)
	 * @see getRotationSpeed()
	 * @see draw()
	 */
	public void rotateX(float des){
		for (int i = 0; i < totalVerts; i ++) {
			float x = vert[i].getX();
			float z = vert[i].getZ();
			vert[i].setX((float) (x * Math.cos(des / getRotationSpeed()) - z * Math.sin(des / getRotationSpeed())));
			vert[i].setZ((float) (z * Math.cos(des / getRotationSpeed()) + x * Math.sin(des / getRotationSpeed())));
			
		}
	}
	
	/**
	 * Rotates the current model along the Y axis based on a factor. This factor 
	 * is calculated by the drag gesture over the canvas, but can be 
	 * called manually. Note that the amount of rotation depends on the parameter
	 * passed and the current value of {@link getRotationSpeed()}.
	 * Usually this function is called along with {@link rotateY(float des)}, and
	 * {@link draw()} is not automatically called when the model is rotated: It 
	 * must be called after the two methods. 
	 *
	 * @param  factor Amount of rotation. Positive values will cause the model to rotate
	 * up and negative values will cause it to rotate down.It does not correspond with
	 * actual degrees.
	 * @see rotateX(float des)
	 * @see setRotationSpeed(float speed)
	 * @see getRotationSpeed()
	 * @see draw()
	 */	
	public void rotateY(float des){
		for (int i = 0; i < totalVerts; i ++) {
			float y = vert[i].getY();
			float z = vert[i].getZ();
			vert[i].setY((float) (y * Math.cos(des / getRotationSpeed()) - z * Math.sin(des / getRotationSpeed())));
			vert[i].setZ((float) (z * Math.cos(des / getRotationSpeed()) + y * Math.sin(des / getRotationSpeed())));
		}
	}
	
	/**
	 * Sets the speed at witch the model will be rotated.
	 * 
	 * @param  speed   The bigger the speed, the faster the model will rotate. 90 is the
	 * default value. If a negative speed is set, the model will rotate on the opposite direction.
	 * @see rotateX(float des)
	 * @see rotateY(float des)
	 * @see getRotationSpeed()
	 */
	public void setRotationSpeed(float speed){
		rotationSpeed = speed;
	}
	
	/**
	 * Gets the speed at witch the model will rotate. 
	 *
	 * @return The current rotation speed. 
	 * 
	 * @see rotateX(float des)
	 * @see rotateY(float des)
	 * @see setRotationSpeed(float speed)
	 */
	public float getRotationSpeed(){
		return rotationSpeed;
	}
	
	/**
	 * Called on {@link draw()}, this method creates the {@link Bitmap} that will be drawn
	 * in the canvas.
	 *
	 * @return The {@link Bitmap} to be drawn. 
	 * 
	 * @see Bitmap
	 * @see draw()
	 */
	private Bitmap createBitMap(){
		//Get screen dimensions
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int h = metrics.heightPixels;
		int w = metrics.widthPixels;
		int x, y;
		
		//Initialize elements to draw
		Path path;
		Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		Bitmap bmp = Bitmap.createBitmap(w, h, conf);
		Canvas canvas = new Canvas(bmp);
		Paint paintVert = new Paint();
		Paint paintEdge = new Paint();
		Paint paintFace = new Paint();
		Paint paintBackground = new Paint();
		
		//Set styles and color for each paint
        paintVert.setStyle(Paint.Style.FILL);
        paintVert.setARGB(colorVertex.getA(), colorVertex.getR(), colorVertex.getG(), colorVertex.getB());
        paintEdge.setARGB(255, colorEdge.getR(), colorEdge.getG(), colorEdge.getB());
        paintEdge.setStyle(Paint.Style.STROKE);
        paintEdge.setStrokeWidth(edgeWidth);
        paintFace.setStyle(Paint.Style.FILL);
        paintFace.setARGB(alpha, colorFace.getR(), colorFace.getG(), colorFace.getB());
        paintBackground.setStyle(Paint.Style.FILL);
        
        //Draw backgraund if required. TODO: If not, just draw in black?? I don't like it, but transparent looks bad.
        if (drawBackground)
            paintBackground.setARGB(colorBackground.getA(), colorBackground.getR(), colorBackground.getG(), colorBackground.getB());
        else
            paintBackground.setColor(android.graphics.Color.BLACK); //If not black
        canvas.drawRect(0, 0, w, h, paintBackground);
        
        //For each face...
		for (int f = 0; f < totalFaces; f ++){
			
			//... initialize a path and move to the first vertex...
			path = new Path();
			path.setFillType(Path.FillType.EVEN_ODD);
			x = (int) (w / 2 + face[f].getVertex(0).getX() * scale);
			y = (int) (h / 2 + face[f].getVertex(0).getY() * scale);
			path.moveTo(x, y);
			
			//... if vertices are to be drawn, do so... 
			if (drawVertices)
				canvas.drawCircle(x, y, vertexWidth, paintVert);
			
			//... make a line to all the other vertices, drawing them if required... 
			for (int v = 1; v < face[f].getVertexCount(); v ++){
				x = (int) (w / 2 + face[f].getVertex(v).getX() * scale);
				y = (int) (h / 2 + face[f].getVertex(v).getY() * scale);
				path.lineTo(x, y);
				if (drawVertices)
					canvas.drawCircle(x, y, vertexWidth, paintVert);
			}
			
			//...if faces are to be drawn...
			if (drawFaces){
				
				//...and the materials used...
				if (useMaterial)
					paintFace.setARGB(alpha, face[f].getMaterial().getColor().getR(), face[f].getMaterial().getColor().getG(), face[f].getMaterial().getColor().getB());
				
				//...or the default face color...
				else
					paintFace.setARGB(alpha, colorFace.getR(), colorFace.getG(), colorFace.getB());
				
				//...draw the face...
				canvas.drawPath(path, paintFace);
			}
			
			//... and if the edges are to be drawn, draw the lines.
			if(drawEdges)
				canvas.drawPath(path, paintEdge);
		}
		
		//Return the drawn bitmap 
		return bmp;
	}
	
	/**
	 * Reads and loads a .mtl file, storing info about the materials that will be used later
	 * to transform and draw the model. It does not check if the file exists, so it must be
	 * checked before calling this method.
	 * 
	 * @param  filename Filename, with its full path, of the file.
	 * 
	 * @param resource Must be true if filename contains the path to a file embedded in the package
	 * (under the res/ folder).
	 */
	private void readMtl(String filename, boolean resource){
		
		//Variables to store temporary data.
		String str;
		Color c;
		float r, g, b;
		int ri, gi, bi;
		
		//Variable that will count the materials.
		int m = -1;
		
		//Data streams.
		InputStream is;
		FileInputStream fis;
		
		//Stream reader.
		BufferedReader input;
		
		//The file.
		File f;
		
		try{
			
			//If the model is not a file, but a resource contained in the app. Load file content.
			if (resource){
				//TODO: Don't hard code the name.
				is= getBaseContext().getResources().openRawResource(R.raw.cubemtl);
				input =  new BufferedReader(new InputStreamReader(is), 1024*8);
			}
			
			//If the model really is on a file. Load file content.
			else{
				f = new File(filename);
				fis = new FileInputStream(f);
				input =  new BufferedReader(new InputStreamReader(fis), 1024*8);
			}
			try{
				
				//Read the file content, line by line.
				String line = null;
				while ((line = input.readLine()) != null){
					
					//If the line is longer than 6 characters (to avoid null pointer exceptions).
					if (line.length() > 6){
						
						//If the line contains the header of a material, advance the counter, create a new material, and save the name.
						if (line.substring(0, 6).equals("newmtl")){
							m = m + 1;
							material[m] = new Material();
							str = line.substring(7);
							material[m].setName(str);
						}
						
						//If the line has info about the diffuse color (Kd), read the color values, and set them for the current material.
						else if (line.substring(0, 3).equals("Kd ")){
							line = line.substring(3);
							r = Float.valueOf(line.substring(0, line.indexOf(" ")));
							line = line.substring(line.indexOf(" ") + 1);
							g = Float.valueOf(line.substring(0, line.indexOf(" ")));
							line = line.substring(line.indexOf(" ") + 1);
							b = Float.valueOf(line);
							ri = (int) (r * 255);
							gi = (int) (g * 255);
							bi = (int) (b * 255);
							c = new Color(ri, gi, bi, 255);
							material[m].setColor(c);
						}
					}
				}
				//Assign the material counter variable.
				totalMaterials = m;
			}
			finally {
				input.close();
			}
		}
		catch (FileNotFoundException ex) {
			Log.e("ERROR", "Couldn't find the file " + filename  + " " + ex);
		}
		catch (IOException ex){
			Log.e("ERROR", "Error reading file " + filename + " " + ex);
		}
	}
	
	/**
	 * Reads and loads a .obj file, storing info about the materials that will be used later
	 * to transform and draw the model. It does not check if the file exists, so it must be
	 * checked before calling this method.
	 * 
	 * @param  filename Filename, with its full path, of the file.
	 * 
	 * @param resource Must be true if filename contains the path to a file embedded in the package
	 * (under the res/ folder).
	 */
	private void readFile(String filename, boolean resource){
		
		//Variables to store temporary data.
		String str;
		float x, y, z, dist;
		
		//Variable that will store the farthest vertex from the center, to calculate the best scale (TODO)
		float max = 0;
		
		//Variable that will count the vertices and faces.
		int v = 0;
		int f = 0;
		
		//Loop variable.
		int j;
		
		//Material to store the material for faces.
		Material mat = null;
		
		//Streams.
		InputStream is;
		FileInputStream fis;
		
		//Stream reader.
		BufferedReader input;
		
		//The file.
		File file;
		
		try{
			
			//If the model is not a file, but a resource contained in the app. Load file content.
			if (resource){
				is= getBaseContext().getResources().openRawResource(R.raw.cubeobj);
				input =  new BufferedReader(new InputStreamReader(is), 1024*8);
			}
			
			//If the model really is on a file. Load file content.
			else{
				file = new File(filename);
				fis = new FileInputStream(file);
				input =  new BufferedReader(new InputStreamReader(fis), 1024*8);
			}
			try {
				
				//Read the file content, line by line.
				String line = null; 
				while ((line = input.readLine()) != null){
					
					//If the line is longer than 6 characters (to avoid null pointer exceptions).
					if (line.length() > 6){
						
						//If the line contains the info about a vertex...
						if (line.substring(0, 2).equals("v ")){
							
							//...read its coordinates...
							line = line.substring(2);
							x = Float.valueOf(line.substring(0, line.indexOf(" ")));
							line = line.substring(line.indexOf(" ") + 1);
							y = Float.valueOf(line.substring(0, line.indexOf(" ")));
							line = line.substring(line.indexOf(" ") + 1);
							z = Float.valueOf(line);
							
							//...calculate its distance to the center...
							dist = (float) Math.sqrt(x * x + y * y + z * z);
							
							//...override if this vertex is the farthest so far...
							if (dist > max)
								max = dist;
							
							//...create a new vertex in the current position...
							vert[v] = new Vertex(x, y, z);
							
							//...and advance the counter.
							v = v + 1;
						}
						
						//If the line contains info about a face...
						else if (line.substring(0,2).equals("f ")){
							
							//... create a new face, get all the vertex forming the face, removing the normals if present, and add them to the face... 
							line = line.substring(2);
							face[f] = new Face();
							j = 0;
							while(line.indexOf(" ") != -1){
								str = line.substring(0, line.indexOf(" "));
								if (str.indexOf('/') != -1)
									str = str.substring(0, str.indexOf('/'));
								face[f].addVertex(vert[Integer.valueOf(str) - 1]);
								line = line.substring(line.indexOf(" ") + 1);
								j = j + 1;
							}
							str = line;
							if (str.indexOf('/') != -1)
								str = str.substring(0, str.indexOf('/'));
							face[f].addVertex(vert[Integer.valueOf(str) - 1]);
							
							//...set face material to the last founded material...
							face[f].setMaterial(mat);
							
							//...and move the counter.
							f = f + 1;
						}
						
						//If the line contains the material for the next faces...
						else if (line.substring(0, 6).equals("usemtl")){
							
							//...find the material with the same name...
							String name = line.substring(7);
							int i = 0;
							while (i < totalMaterials && material[i].getName().equals(name) == false)
								i ++;
							
							//and assign it, so the next faces will have it.
							mat = material[i];
						}
					}
				}
				
				//Assign counters
		        totalVerts = v;
		        totalFaces = f;
			}
			finally {
				input.close();
			}
		}
		catch (FileNotFoundException ex) {
			Log.e("ERROR", "Couldn't find the file " + filename  + " " + ex);
		}
		catch (IOException ex){
			Log.e("ERROR", "Error reading file " + filename + " " + ex);
		}
	}
	
	
	/*
	 * Overridden method. If the menu is being displayed, closes it.
	 * Otherwise, do the normal stuff (finish activity).
	 */
	@Override
	public void onBackPressed() {
		
		//If the menu is being shown, just close it.
		if (mLayout.isMenuShown())
			mLayout.toggleMenu();
		
		//Otherwise, just do the normal stuff (terminate activity)
		else
			super.onBackPressed();
	}
	
	/*
	 * Overridden method. If the pressed key is menu, show or close it.
	 * Otherwise, do the normal stuff.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		//If the pressed key is the menu, open or close slider menu.
        if (keyCode == KeyEvent.KEYCODE_MENU) {
        	mLayout.toggleMenu();
            return true;
        }
        
        //Otherwise, normal stuff.
        return super.onKeyDown(keyCode, event); 
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
						
						//Set color.
						colorVertex.setR(r);
						colorVertex.setG(g);
						colorVertex.setB(b);
						
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
						
						//Redraw.
						draw();
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
						
						//Set color.
						colorEdge.setR(r);
						colorEdge.setG(g);
						colorEdge.setB(b);
						
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
						
						//Redraw.
						draw();
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
						
						//Set color.
						colorFace.setR(r);
						colorFace.setG(g);
						colorFace.setB(b);
						
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
						
						//Redraw.
						draw();
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
						
						//Set color.
						colorBackground.setR(r);
						colorBackground.setG(g);
						colorBackground.setB(b);
						
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
						
						//Redraw.
						draw();
					}
				}
				break;
			
			//If its coming from ExplorerActivity to load a model.
			case (CODE_MODEL):
				if (resultCode == Activity.RESULT_OK) {
					
					//Read data
					String filename = data.getStringExtra("file");
					Log.d("MODEL", filename);
					loadModel(filename);
					//Load model
					//TODO
				}
				break;
		}
	}
	
	/**
	 * Start a AsyncTask to find obj files in the storage directory and sub directories.
	 * Is automatically called when the activity starts.
	 */
	public void reload(){
		new Reload(this, null, null).execute();
	}
	
	//TODO: dont restart
	/*@Override
	public void onConfigurationChanged(Configuration newConfig) {
		draw();
	    super.onConfigurationChanged(newConfig);
	    	
	}*/
}
