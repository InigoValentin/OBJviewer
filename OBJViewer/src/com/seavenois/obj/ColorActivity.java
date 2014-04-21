package com.seavenois.obj;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * An activity to pick a color. Is launched as a dialog, every time the user
 * want to pick a color for the background, vertices, edges or faces.
 * 
 * Currently, consist only in three sliders (RGB) to compose a color and a preview.
 * It does not include an alpha selector.
 */
public class ColorActivity extends Activity{
	
	//SeekBars
	private SeekBar sbR, sbG, sbB;
	
	//Preview
	private ImageView ivPreview;
	
	//Buttons
	private Button btSelect, btDiscard;
	
	//Color values
	private int r, g, b;
	
	/*
	 * Overridden method. Called when the activity starts. See in line comments.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Remove title bar
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
	    //Set layout
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_color);
		
		//Assign preview
		ivPreview = (ImageView) findViewById(R.id.ivColorPreview);
		
		//Assign bars
		sbR = (SeekBar) findViewById(R.id.sbColorRed);
		sbG = (SeekBar) findViewById(R.id.sbColorGreen);
		sbB = (SeekBar) findViewById(R.id.sbColorBlue);
		
		//Assign buttons
		btSelect = (Button) findViewById(R.id.btColorSelect);
		btDiscard = (Button) findViewById(R.id.btColorDiscard);
		
		//Assign "Select" button listener. Put selected color values as extra and finish.
		btSelect.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent resultIntent = new Intent();
				resultIntent.putExtra("ok", true);
				resultIntent.putExtra("r", r);
				resultIntent.putExtra("g", g);
				resultIntent.putExtra("b", b);
				setResult(Activity.RESULT_OK, resultIntent);
				finish();
			}
		});
		
		//Assign "Discard" button listener. Put invalid color values as extra and finish.
		btDiscard.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent resultIntent = new Intent();
				resultIntent.putExtra("ok", false);
				resultIntent.putExtra("r", -1);
				resultIntent.putExtra("g", -1);
				resultIntent.putExtra("b", -1);
				setResult(Activity.RESULT_OK, resultIntent);
				finish();
			}
		});
		
		//OnSeekBarChangeListener for the red bar. Assigns the color value and calculates the preview.
		sbR.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				r = progress;
				preview();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			
		});
		
		//OnSeekBarChangeListener for the green bar. Assigns the color value and calculates the preview.
		sbG.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				g = progress;
				preview();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			
		});
		
		//OnSeekBarChangeListener for the blue bar. Assigns the color value and calculates the preview.
		sbB.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				b = progress;
				preview();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			
		});
		
		//Set layout orientation depending on screen orientation 
		LinearLayout ll = (LinearLayout) findViewById(R.id.llColorActivity);
	    int orientation = getResources().getConfiguration().orientation;
	    if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
	    	ll.setOrientation(LinearLayout.VERTICAL);
	    else if (orientation == 2) //TODO: No constant with this value
	    	ll.setOrientation(LinearLayout.HORIZONTAL);
		
		//Get extra data and set seek bars to the current color
	    Intent intent = getIntent();
	    sbR.setProgress(intent.getIntExtra("r", 0));
	    sbG.setProgress(intent.getIntExtra("g", 0));
	    sbB.setProgress(intent.getIntExtra("b", 0));
	    preview();
	}
	
	/**
	 * Calculate the color depending on the seek bars state and colors a ImageView
	 */
	private void preview(){
		int w = ivPreview.getWidth();
		int h = ivPreview.getHeight();
		//Fail safe for the first time, when ivPreview properties are not loaded
		if (w <= 0)
			w = 80;
		if (h <= 0)
			h = 80;
		Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		Bitmap bmp = Bitmap.createBitmap(w, h, conf);
		Canvas canvas = new Canvas(bmp);
		Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setARGB(255, r, g, b);
        canvas.drawRect(0, 0, w, h, paint);
        ivPreview.setImageBitmap(bmp);
	}

	/*
	 * Overridden method. Changes layout orientation when screen orientation does.
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    LinearLayout ll = (LinearLayout) findViewById(R.id.llColorActivity);
	    int orientation = getResources().getConfiguration().orientation;
	    if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
	    	ll.setOrientation(LinearLayout.VERTICAL);
	    else if (orientation == 2) //TODO: No constant
	    	ll.setOrientation(LinearLayout.HORIZONTAL);
	    	
	}
}
