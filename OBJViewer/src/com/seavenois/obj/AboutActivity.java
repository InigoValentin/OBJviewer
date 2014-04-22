package com.seavenois.obj;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;

public class AboutActivity extends Activity{

	@Override 
    public void onCreate(Bundle savedInstanceState) { 
            super.onCreate(savedInstanceState); 
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_about); 
            WebView webview = (WebView) findViewById(R.id.wbAbout); 
            webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            
            //Load file
            webview.loadUrl("file:///android_asset/about.html");

    }
}
