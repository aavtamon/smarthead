package com.pisoft.headcontroller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;

import com.pisoft.headcontroller.head.HeadController;
import com.pisoft.headcontroller.util.SystemUiHider;

public class HtmlScreen extends ControllingActivity {
	private WebView webView;
	private HeadController headController;
	private Map<String, String> config;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_html_screen);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		readConfigFile();
		
		headController = new HeadController(this, config);

		webView = (WebView)findViewById(R.id.MenuView);
		webView.addJavascriptInterface(headController, "headController");
		
		final View contentView = findViewById(R.id.MainFrame);

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		SystemUiHider mSystemUiHider = SystemUiHider.getInstance(this, contentView, SystemUiHider.FLAG_HIDE_NAVIGATION);
		mSystemUiHider.setup();
		mSystemUiHider.hide();
	}
	
	protected void onStart() {
		super.onStart();

		headController.init();

		String url = config.get("startUrl");

		webView.loadUrl(url);
	}
	
	protected void onPause() {
		headController.pause();

		super.onPause();
	}

	protected void onDestroy() {
		headController.destroy();

		super.onDestroy();
	}

	protected void callJSCallback(final String callback, final Object params) {
		String callbackParam = null;
		if (params == null) {
			callbackParam = "null";
		} else if (params instanceof String) {
			callbackParam = "\"" + params.toString() + "\"";
		} else {
			callbackParam = convertObjectToJson(params);
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
		} else if (structure == null) {
			return "null";
		} else if (structure instanceof Number) {
			return structure.toString();
		} else {
			return "\"" + structure.toString() + "\"";
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
	
	
	private void readConfigFile() {
		config = new HashMap<String, String>();
		
		File configFile = new File(getExternalFilesDir(null), "config.txt");
		
		if (!configFile.isFile()) {
			createDefaultConfig(configFile);
			Log.d("HeadController", "No config file yet created -- creating one with defaults...");
		}
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(configFile));
			String nextLine;
			
			while((nextLine = reader.readLine()) != null) {
				nextLine = nextLine.trim();
				if (nextLine.length() == 0 || nextLine.startsWith("//")) {
					continue;
				}
				
				String[] parts = nextLine.split("=");
				if (parts.length != 2) {
					Log.e("HeadController", "Error in the config file: " + nextLine);
				} else {
					config.put(parts[0], parts[1]);
				}
			}
			reader.close();
		} catch (IOException ioe) {
			Log.e("HeadController", "Error reading config file", ioe);
		}
	}
	
	private void createDefaultConfig(File configFile) {
		try {
			PrintWriter pw = new PrintWriter(configFile);
			
			pw.println("startUrl=file:///android_asset/menu.html");

			pw.println();
			pw.println("//Visual Configuration");
			pw.println("visual=direct");
			pw.println("//visual=repaint");
			
			pw.println();
			pw.println("//Vision Configuration");
			pw.println("vision=realtime");
			pw.println("//vision=offline");
			
			pw.close();
		} catch (FileNotFoundException e) {
			Log.e("HeadController", "Error creating deafult config file", e);
		}
	}
}
