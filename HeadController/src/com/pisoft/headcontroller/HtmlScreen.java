package com.pisoft.headcontroller;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import com.pisoft.headcontroller.head.HeadController;
import com.pisoft.headcontroller.util.SystemUiHider;

public class HtmlScreen extends ControllingActivity {
	private WebView webView;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_html_screen);

		webView = (WebView)findViewById(R.id.MenuView);
		webView.addJavascriptInterface(new HeadController(this), "headController");
		webView.loadUrl("http://10.13.26.90:8050/menu.html");
		
		
		final View contentView = findViewById(R.id.MainFrame);

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		SystemUiHider mSystemUiHider = SystemUiHider.getInstance(this, contentView, SystemUiHider.FLAG_HIDE_NAVIGATION);
		mSystemUiHider.setup();
		mSystemUiHider.hide();
	}

	protected void callJSCallback(final String callback, final String paramString) {
		webView.loadUrl("javascript:{ headController['___tempJSBridgeFunction'] = " + callback + "; headController['___tempJSBridgeFunction'](" + (paramString != null ? "'" + paramString  + "'" : "") + "); delete headController['___tempJSBridgeFunction']; }");
	}
}
