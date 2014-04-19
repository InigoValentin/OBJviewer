package com.seavenois.obj;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
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

public class MainActivity extends Activity {

	//Static variables
	static final int MAX_VERTIZES = 100000;		//Max number of vertices in file
	static final int MAX_FACES = 100000;		//Max number of faces in file
	static final int MAX_MATERIALS = 100;		//Max number of materials in file
	
	//Codes passed to ColorActivity activity. Can be anything.
	static final int CODE_COLOR_VERTEX = 1803;	
	static final int CODE_COLOR_EDGE = 1804;
	static final int CODE_COLOR_FACE = 1805;
	
	//UI elements
	ImageView ivCanvas;
	Button btLoadModel;
	LinearLayout llVertexPreferences, llEdgePreferences, llFacePreferences;
	CheckBox cbDrawVertizes, cbDrawEdges, cbDrawFaces;
	TextView tvVertexColor, tvEdgeColor, tvFaceColor;
	SeekBar sbEdgeSize, sbVertexSize;
	
	MainLayout mLayout;
	Vertex[] vert = new Vertex[MAX_VERTIZES];
	Face[] face = new Face[MAX_FACES];
	Material[] material = new Material[MAX_MATERIALS];
	int totalVerts, totalFaces, totalMaterials;
	boolean touching = false;
	float prevX, prevY;
	
	//Useful measures
	float scale = 40;
	float rotationSpeed = 90;
	int vertexWidth = 3;
	int edgeWidth = 2;
	
	//Colors
	Color colorVertex, colorEdge, colorFace;
	
	//Booleans to determine wich elements to draw
	boolean drawVertizes = true;
	boolean drawEdges = true;
	boolean drawFaces = true;
	
	//Booleans to determine if material file is present, and if it is to be used
	boolean mtlFilePresent;
	boolean useMaterial = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Remove title bar
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
	    //Set layout
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Useful variables
		Drawable daux;
		Bitmap.Config conf;
		Bitmap bmp;
		Canvas canvas;
		Paint paint;
		
		//Init colors
		colorVertex = new Color(100, 0, 0, 255);
		colorEdge = new Color(0, 0, 0, 255);
		colorFace = new Color(0, 0, 255, 255);
		
		//Assign elements
		ivCanvas = (ImageView) findViewById(R.id.ivCanvas);
		mLayout = (MainLayout) findViewById(R.id.main_layout);
		btLoadModel = (Button) findViewById(R.id.btLoadModel);
		cbDrawVertizes = (CheckBox) findViewById(R.id.cbDrawVertizes);
		cbDrawEdges = (CheckBox) findViewById(R.id.cbDrawEdges);
		cbDrawFaces = (CheckBox) findViewById(R.id.cbDrawFaces);
		llVertexPreferences = (LinearLayout) findViewById(R.id.llVertexPreferences);
		llEdgePreferences = (LinearLayout) findViewById(R.id.llEdgePreferences);
		llFacePreferences = (LinearLayout) findViewById(R.id.llFacePreferences);
		tvVertexColor = (TextView) findViewById(R.id.tvVertexColor);
		tvEdgeColor = (TextView) findViewById(R.id.tvEdgeColor);
		tvFaceColor = (TextView) findViewById(R.id.tvFaceColor);
		sbVertexSize = (SeekBar) findViewById(R.id.sbVertexSize);
		sbEdgeSize = (SeekBar) findViewById(R.id.sbEdgeSize);
		
		//Assign listeners for ui elements
		btLoadModel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				//TODO
			}
		});
		
		cbDrawVertizes.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
				if (isChecked == true){
					llVertexPreferences.setVisibility(View.VISIBLE);
					drawVertizes = true;
				}
				else{
					llVertexPreferences.setVisibility(View.GONE);
					drawVertizes = false;
				}
				draw();
			}
		});
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
		tvVertexColor.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Intent i = new Intent(getBaseContext(), ColorActivity.class);
				startActivityForResult(i, CODE_COLOR_VERTEX);
			}
		});
		tvEdgeColor.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Intent i = new Intent(getBaseContext(), ColorActivity.class);
				startActivityForResult(i, CODE_COLOR_EDGE);
			}
		});
		tvFaceColor.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Intent i = new Intent(getBaseContext(), ColorActivity.class);
				startActivityForResult(i, CODE_COLOR_FACE);
			}
		});
		
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
		
		//Assign touch listener for the main canvas
		ivCanvas.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				DisplayMetrics metrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(metrics);
				int w = metrics.widthPixels;
				if (event.getX() < 0.1 * w)
					return false;
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						touching = true;
						prevX = event.getX();
						prevY = event.getY();
						break;
					case MotionEvent.ACTION_UP:
						touching = false;
						break;
					case MotionEvent.ACTION_MOVE:
						if (touching){
							rotateX(prevX - event.getX());
							rotateY(prevY - event.getY());
							draw();
							prevX = event.getX();
							prevY = event.getY();
						}
						break;
				}
				return true;
			}
		});
		//REad mtl file
		mtlFilePresent = true; //TODO: Check if exists
		if (mtlFilePresent == true)
			readMtl("raw/cubemtl");
		//Read obj file file
		readFile("raw/cubeobj");
		draw();
	}

	public void draw(){
		Arrays.sort(face, 0, totalFaces);
		Bitmap bmp = createBitMap();
		//Draw the image
		ivCanvas.setImageBitmap(bmp);
	}
	
	public void rotateX(float des){
		for (int i = 0; i < totalVerts; i ++) {
			float x = vert[i].getX();
			float z = vert[i].getZ();
			vert[i].setX((float) (x * Math.cos(des / rotationSpeed) - z * Math.sin(des / rotationSpeed)));
			vert[i].setZ((float) (z * Math.cos(des / rotationSpeed) + x * Math.sin(des / rotationSpeed)));
			
		}
	}
	
	public void rotateY(float des){
		for (int i = 0; i < totalVerts; i ++) {
			float y = vert[i].getY();
			float z = vert[i].getZ();
			vert[i].setY((float) (y * Math.cos(des / rotationSpeed) - z * Math.sin(des / rotationSpeed)));
			vert[i].setZ((float) (z * Math.cos(des / rotationSpeed) + y * Math.sin(des / rotationSpeed)));
		}
	}
	
	private Bitmap createBitMap(){
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int h = metrics.heightPixels;
		int w = metrics.widthPixels;
		int x, y;
		Path path;
		Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		Bitmap bmp = Bitmap.createBitmap(w, h, conf);
		Canvas canvas = new Canvas(bmp);
		Paint paintVert = new Paint();
		Paint paintEdge = new Paint();
		Paint paintFace = new Paint();
        paintVert.setStyle(Paint.Style.FILL);
        paintVert.setARGB(colorVertex.getA(), colorVertex.getR(), colorVertex.getG(), colorVertex.getB());
        paintEdge.setARGB(255, colorEdge.getR(), colorEdge.getG(), colorEdge.getB());
        paintEdge.setStyle(Paint.Style.STROKE);
        paintEdge.setStrokeWidth(edgeWidth);
        paintFace.setStyle(Paint.Style.FILL);
        paintFace.setARGB(colorFace.getA(), colorFace.getR(), colorFace.getG(), colorFace.getB());
		for (int f = 0; f < totalFaces; f ++){
			path = new Path();
			path.setFillType(Path.FillType.EVEN_ODD);
			x = (int) (w / 2 + face[f].getVertex(0).getX() * scale);
			y = (int) (h / 2 + face[f].getVertex(0).getY() * scale);
			path.moveTo(x, y);
			if (drawVertizes)
				canvas.drawCircle(x, y, vertexWidth, paintVert);
			for (int v = 1; v < face[f].getVertexCount(); v ++){
				x = (int) (w / 2 + face[f].getVertex(v).getX() * scale);
				y = (int) (h / 2 + face[f].getVertex(v).getY() * scale);
				path.lineTo(x, y);
				if (drawVertizes)
					canvas.drawCircle(x, y, vertexWidth, paintVert);
			}
			if (drawFaces){
				if (useMaterial)
					paintFace.setARGB(255, face[f].getMaterial().getColor().getR(), face[f].getMaterial().getColor().getG(), face[f].getMaterial().getColor().getB());
				else
					paintFace.setARGB(colorFace.getA(), colorFace.getR(), colorFace.getG(), colorFace.getB());
				canvas.drawPath(path, paintFace);
			}
			if(drawEdges)
				canvas.drawPath(path, paintEdge);
		}
		return bmp;
	}
	
	private void readMtl(String filename){
		String str;
		Color c;
		float r, g, b;
		int ri, gi, bi;
		int m = -1;
		try{
			InputStream is = getBaseContext().getResources().openRawResource(R.raw.cubemtl); //TODO: Open file, not raw
			BufferedReader input =  new BufferedReader(new InputStreamReader(is), 1024*8);
			try{
				String line = null;
				while ((line = input.readLine()) != null){
					if (line.length() > 6){
						if (line.substring(0, 6).equals("newmtl")){
							m = m + 1;
							material[m] = new Material();
							str = line.substring(7);
							material[m].setName(str);
						}
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
	
	private void readFile(String filename){
		String str;
		float x, y, z, dist;
		float max = 0;
		int v = 0;
		int f = 0;
		int j;
		Material mat = null;
		try {			
			InputStream is = getBaseContext().getResources().openRawResource(R.raw.cubeobj); //TODO: Open file, not raw
			BufferedReader input =  new BufferedReader(new InputStreamReader(is), 1024*8);
			try {
				String line = null; 
				while ((line = input.readLine()) != null){
					if (line.length() > 6){
						if (line.substring(0, 2).equals("v ")){
							line = line.substring(2);
							x = Float.valueOf(line.substring(0, line.indexOf(" ")));
							line = line.substring(line.indexOf(" ") + 1);
							y = Float.valueOf(line.substring(0, line.indexOf(" ")));
							line = line.substring(line.indexOf(" ") + 1);
							z = Float.valueOf(line);
							dist = (float) Math.sqrt(x * x + y * y + z * z);
							if (dist > max)
								max = dist;
							vert[v] = new Vertex(x, y, z);
							v = v + 1;
						}
						else if (line.substring(0,2).equals("f ")){
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
							face[f].setMaterial(mat);
							f = f + 1;
						}
						else if (line.substring(0, 6).equals("usemtl")){
							String name = line.substring(7);
							int i = 0;
							while (i < totalMaterials && material[i].getName().equals(name) == false)
								i ++;
							mat = material[i];
						}
					}
				}
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
	
	@Override
	public void onBackPressed() {
		if (mLayout.isMenuShown())
			mLayout.toggleMenu();
		else
			super.onBackPressed();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) { 
        if (keyCode == KeyEvent.KEYCODE_MENU) {
        	mLayout.toggleMenu();
            return true;
        }
        return super.onKeyDown(keyCode, event); 
    }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		int r, g, b;
		Bitmap.Config conf;
		Bitmap bmp;
		Canvas canvas;
		Paint paint;
		switch(requestCode) {
		case (CODE_COLOR_VERTEX):
			if (resultCode == Activity.RESULT_OK) {
				r = data.getIntExtra("r", -1);
				g = data.getIntExtra("g", -1);
				b = data.getIntExtra("b", -1);
				if(r != -1 && g != -1 && b != -1){
					colorVertex.setR(r);
					colorVertex.setG(g);
					colorVertex.setB(b);
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
					draw();
				}
			}
			break;
		case (CODE_COLOR_EDGE):
			if (resultCode == Activity.RESULT_OK) {
				r = data.getIntExtra("r", -1);
				g = data.getIntExtra("g", -1);
				b = data.getIntExtra("b", -1);
				if(r != -1 && g != -1 && b != -1){
					colorEdge.setR(r);
					colorEdge.setG(g);
					colorEdge.setB(b);
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
					draw();
				}
			}
			break;
		case (CODE_COLOR_FACE):
			if (resultCode == Activity.RESULT_OK) {
				r = data.getIntExtra("r", -1);
				g = data.getIntExtra("g", -1);
				b = data.getIntExtra("b", -1);
				if(r != -1 && g != -1 && b != -1){
					colorFace.setR(r);
					colorFace.setG(g);
					colorFace.setB(b);
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
					draw();
				}
			}
			break;
		}
	}
}
