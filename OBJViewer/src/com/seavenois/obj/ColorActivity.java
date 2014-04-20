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

public class ColorActivity extends Activity{
	
	//SeekBars
	SeekBar sbR, sbG, sbB;
	
	//Preview
	ImageView ivPreview;
	
	//Buttons
	Button btSelect, btDiscard;
	
	//Color values
	int r, g, b;
		
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
		
		//Assign listeners
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
		
		//Set orientation
		LinearLayout ll = (LinearLayout) findViewById(R.id.llColorActivity);
	    int orientation = getResources().getConfiguration().orientation;
	    if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
	    	ll.setOrientation(LinearLayout.VERTICAL);
	    else if (orientation == 2) //TODO: No constant
	    	ll.setOrientation(LinearLayout.HORIZONTAL);
		
		//Get extra and move seek bars
	    Intent intent = getIntent();
	    sbR.setProgress(intent.getIntExtra("r", 0));
	    sbG.setProgress(intent.getIntExtra("g", 0));
	    sbB.setProgress(intent.getIntExtra("b", 0));
	    preview();
	}
	
	public void preview(){
		int w = ivPreview.getWidth();
		int h = ivPreview.getHeight();
		//Failsafe for the first time, when ivPreview properties are not loaded
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Check which request we're responding to
		if (requestCode == 1) {
			// Make sure the request was successful
			if (resultCode == RESULT_OK) {
	            data.putExtra("r", r);
	            data.putExtra("g", g);
	            data.putExtra("b", b);
	            setResult(resultCode);
	        }
	    }
	}
	
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
