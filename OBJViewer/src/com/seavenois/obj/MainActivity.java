package com.seavenois.obj;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
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
import android.widget.TextView;

public class MainActivity extends Activity {

	static final int MAX_VERTIZES = 100000;
	static final int MAX_FACES = 100000;
	
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
	int totalVerts, totalFaces;
	float scale = 40;
	float rotationSpeed = 90;
	boolean touching = false;
	float prevX, prevY;
	
	//Booleans to determine wich elements to draw
	boolean drawVertizes = true;
	boolean drawEdges = true;
	boolean drawFaces = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Remove title bar
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
	    //Set layout
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Useful variables
		Drawable daux;
		
		//Assign elements
		ivCanvas = (ImageView) findViewById(R.id.ivCanvas);
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
		
		daux = getResources().getDrawable(R.drawable.square); //TODO: Create drawable with the color
		daux.setBounds(0, 0, 50, 50);
		tvVertexColor.setCompoundDrawables(daux ,null, null, null);
		daux = getResources().getDrawable(R.drawable.square); //TODO: Create drawable with the color
		daux.setBounds(0, 0, 50, 50);
		tvEdgeColor.setCompoundDrawables(daux ,null, null, null);
		daux = getResources().getDrawable(R.drawable.square); //TODO: Create drawable with the color
		daux.setBounds(0, 0, 50, 50);
		tvFaceColor.setCompoundDrawables(daux ,null, null, null);
		tvVertexColor.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				//TODO
			}
		});
		tvEdgeColor.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				//TODO
			}
		});
		tvFaceColor.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				//TODO
			}
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
						Log.d("Touching", "move");
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
		
		//Load file
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
		Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
		Bitmap bmp = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
		Canvas canvas = new Canvas(bmp);
		Paint paintVert = new Paint();
		Paint paintEdge = new Paint();
		Paint paintFace = new Paint();
        paintVert.setStyle(Paint.Style.FILL);
        paintVert.setColor(android.graphics.Color.RED);
        paintEdge.setStyle(Paint.Style.STROKE);
        paintEdge.setStrokeWidth(2);
        paintEdge.setColor(android.graphics.Color.GREEN);
        paintFace.setStyle(Paint.Style.FILL);
        paintFace.setColor(android.graphics.Color.YELLOW);
        //canvas.drawCircle(w/2, h/2 , w/2, paint);
		//ivCanvas.setImageBitmap(bmp);
		for (int f = 0; f < totalFaces; f ++){
			path = new Path();
			path.setFillType(Path.FillType.EVEN_ODD);
			x = (int) (w / 2 + face[f].getVertex(0).getX() * scale);
			y = (int) (h / 2 + face[f].getVertex(0).getY() * scale);
			path.moveTo(x, y);
			if (drawVertizes)
				canvas.drawCircle(x, y, 2, paintVert);
			for (int v = 1; v < face[f].getVertexCount(); v ++){
				x = (int) (w / 2 + face[f].getVertex(v).getX() * scale);
				y = (int) (h / 2 + face[f].getVertex(v).getY() * scale);
				path.lineTo(x, y);
				if (drawVertizes)
					canvas.drawCircle(x, y, 2, paintVert);
			}
			paintEdge.setColor(android.graphics.Color.BLUE);
			if (drawFaces)
				canvas.drawPath(path, paintFace);
			if(drawEdges)
				canvas.drawPath(path, paintEdge);
		}
		return bmp;
	}
	
	private void readFile(String filename){
	    String str;
	    float x, y, z, dist;
	    float max = 0;
	    int v = 0;
	    int f = 0;
	    int j;
	    Material mat;
	    try {			
	      InputStream is = getBaseContext().getResources().openRawResource(R.raw.cubeobj);
 
	      BufferedReader input =  new BufferedReader(new InputStreamReader(is), 1024*8);
	      try {
	        String line = null; 
	        while (( line = input.readLine()) != null){
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
					//face[f].setMaterial(mat); TODO
					f = f + 1;
				}
				//else if (line.substring(0, 6) == "usemtl"){ TODO
				//	mat = line.substring(7);Edge
				//}
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
		if (mLayout.isMenuShown()) {
			mLayout.toggleMenu();
		}
		else {
			super.onBackPressed();
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) { 
        if (keyCode == KeyEvent.KEYCODE_MENU) {
        	mLayout.toggleMenu();
            return true;
        }
        return super.onKeyDown(keyCode, event); 
    }
	
	public void toggleMenu(View v) {
		mLayout.toggleMenu();
	}

}
