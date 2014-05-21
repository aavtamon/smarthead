package com.pisoft.headcontroller.head;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

public class HeadMotion {
	private boolean initialized;
	
	public interface OnCompleteListener {
		public void onComplete();
	}
	
	public HeadMotion(final Activity activity) {
		initialized = sendCommandToMotionDriver("isReady") == 200;
	}
	
	public boolean isReady() {
		return initialized;
	}
	
	public boolean rotate(final int degree, final OnCompleteListener listener) {
		if (!isReady()) {
			return false;
		}
		
		notifyWhenFinished(listener);
		
		return sendCommandToMotionDriver("rotate?degree=" + degree) == 202;
	}
	
	
	private void notifyWhenFinished(final OnCompleteListener listener) {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				if (sendCommandToMotionDriver("isIdle") == 200) {
					listener.onComplete();
				} else {
					notifyWhenFinished(listener);
				}
			}
		}, 1000);
	}
	
	private int sendCommandToMotionDriver(final String command) {
		HttpURLConnection connection = null;
		
		try {
			URL driverHttpInterface = new URL("http://192.168.1.3/motion_control/" + (command != null ? command : ""));
			
			connection = (HttpURLConnection)driverHttpInterface.openConnection();
			connection.setRequestMethod("GET");
			
			connection.connect();

			return connection.getResponseCode();
		} catch (IOException ioe) {
			Log.e("HeadControloler", "Failed to connect to the motion driver", ioe);
			return -1;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}
}
