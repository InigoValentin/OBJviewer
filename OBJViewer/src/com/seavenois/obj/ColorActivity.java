package com.seavenois.obj;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ColorActivity extends Activity{
	
	//SeekBars
	SeekBar sbR, sbG, sbB;
	
	//Preview
	ImageView ivPreview;
	
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
		
		//Assign on change listeners
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
	}
	
	public void preview(){
		int w = ivPreview.getWidth();
		int h = ivPreview.getHeight();
		Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
		Bitmap bmp = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
		Canvas canvas = new Canvas(bmp);
		Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setARGB(255, r, g, b);
        canvas.drawRect(0, 0, w, h, paint);
        ivPreview.setImageBitmap(bmp);
	}
}
