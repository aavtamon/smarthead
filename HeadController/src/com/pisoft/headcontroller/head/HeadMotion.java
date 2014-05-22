package com.pisoft.headcontroller.head;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.util.Log;

public class HeadMotion {
	private boolean initialized;
	
	public interface OnCompleteListener {
		public void onComplete();
	}

	private interface NetworkOperationListener {
		public void onComplete(int status);
	}
	
	
	public HeadMotion(final Activity activity) {
		sendCommandToMotionDriver("isReady", new NetworkOperationListener() {
			public void onComplete(int status) {
				initialized = status == 200;
			}
		});
	}
	
	public boolean isReady() {
		return initialized;
	}
	
	public boolean rotate(final int degree, final OnCompleteListener listener) {
		if (!isReady()) {
			return false;
		}
		
		sendCommandToMotionDriver("rotate?degree=" + degree, new NetworkOperationListener() {
			public void onComplete(int status) {
				if (status == 200) {
					listener.onComplete();
				}
			}
		});
		
		return true;
	}
	
	
	private void sendCommandToMotionDriver(final String command, final NetworkOperationListener listener) {
		new Thread() {
			public void run() {
				HttpURLConnection connection = null;
				
				try {
					URL driverHttpInterface = new URL("http://192.168.1.3/motion_control/" + (command != null ? command : ""));
					
					connection = (HttpURLConnection)driverHttpInterface.openConnection();
					connection.setRequestMethod("GET");
					
					connection.connect();
		
					listener.onComplete(connection.getResponseCode());
				} catch (IOException ioe) {
					Log.e("HeadControloler", "Failed to connect to the motion driver", ioe);
					listener.onComplete(-1);
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
		}.start();
	}
}
