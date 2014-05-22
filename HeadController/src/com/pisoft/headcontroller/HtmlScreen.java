package com.pisoft.headcontroller;

import java.util.Iterator;
import java.util.List;
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

	protected void callJSCallback(final String callback, final Object params) {
		String callbackParam = null;
		if (params == null) {
			callbackParam = "null";
		} else if (params instanceof String) {
			callbackParam = params.toString();
		} else {
			callbackParam = "JSON.parse(" + convertObjectToJson(params) + ")";
		}
		
		String javaScript = "headController['___tempJSBridgeFunctionParam'] = " + callbackParam + "; "
				            + "headController['___tempJSBridgeFunction'] = " + callback + "; headController['___tempJSBridgeFunction'](headController['___tempJSBridgeFunctionParam']); "
				            + "delete headController['___tempJSBridgeFunction']; delete headController['___tempJSBridgeFunctionParam'];";

		webView.loadUrl("javascript:{ " + javaScript + " }");
	}
	
	
	private String convertObjectToJson(final Object structure) {
		if (structure instanceof Map) {
			return convertMapToJson((Map<String, Object>)structure);
		} else if (structure instanceof List) {
			return convertListToJson((List<Object>)structure);
		} else {
			return "null";
		}
	}
	
	private String convertMapToJson(final Map<String, Object> structure) {
		StringBuffer paramString = new StringBuffer("{");
		for (Iterator<Map.Entry<String, Object>> it = structure.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<String, Object> entry = it.next();
			
			String jsonValue = convertObjectToJson(entry.getValue());
			paramString.append(entry.getKey()).append(":").append(jsonValue).append(",");
		}
		paramString.append("}");
		
		return paramString.toString();
	}
	
	
	private String convertListToJson(final List<Object> structure) {
		StringBuffer paramString = new StringBuffer("[");
		for (Iterator<Object> it = structure.iterator(); it.hasNext(); ) {
			String jsonValue = convertObjectToJson(it.next());
			paramString.append(jsonValue).append(", ");
		}
		paramString.append("]");
		
		return paramString.toString();
	}
	
}
