package com.pisoft.headcontroller;

import java.util.Iterator;
import java.util.Map;

import android.os.Bundle;
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
		String javaScript = "headController['___tempJSBridgeFunction'] = " + callback + "; headController['___tempJSBridgeFunction'](" + (paramString != null ? "'" + paramString  + "'" : "") + "); delete headController['___tempJSBridgeFunction'];";
		
		webView.loadUrl("javascript:{ " + javaScript + " }");
	}
	
	protected void callJSCallback(final String callback, final Map params) {
		StringBuffer paramString = new StringBuffer("{");
		for (Iterator<Map.Entry<String, String>> it = params.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<String, String> entry = it.next();
			paramString.append(entry.getKey()).append(":").append(entry.getValue()).append(",");
		}
		paramString.append("{");

		String javaScript = "headController['___tempJSBridgeFunctionParam'] = JSON.parse(" + paramString + "); "
				            + "headController['___tempJSBridgeFunction'] = " + callback + "; headController['___tempJSBridgeFunction'](headController['___tempJSBridgeFunctionParam']); "
				            + "delete headController['___tempJSBridgeFunction']; delete headController['___tempJSBridgeFunctionParam'];";
		
		webView.loadUrl("javascript:{ " + javaScript + " }");
	}
}
