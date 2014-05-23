package com.pisoft.headcontroller.head;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class HeadMotion {
	private static final int NUM_OF_READINGS_TO_AVERAGE = 30;
	
	private boolean initialized;
	
	private List<Integer> angleReadings = new ArrayList<Integer>(NUM_OF_READINGS_TO_AVERAGE);
	
	public interface OnCompleteListener {
		public void onComplete();
	}

	private interface NetworkOperationListener {
		public void onComplete(int status);
	}
	
	
	public HeadMotion(final Activity activity) {
		SensorManager sm = (SensorManager)activity.getSystemService(Context.SENSOR_SERVICE);
		
		SensorEventListener sl = new SensorEventListener() {
			private float[] accelerometerValues;
			private float[] magneticValues;
			
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}

			public void onSensorChanged(SensorEvent event) {
			    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			    	accelerometerValues = event.values.clone();
			    }
			    if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			    	magneticValues = event.values.clone();
			    }
			    
			    if (accelerometerValues != null && magneticValues != null) {
			        float R[] = new float[16];
			        if (SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticValues)) {
			        	float screenR[] = new float[16];
			        	SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_Y, SensorManager.AXIS_Z, screenR);
			        	
			        	float orientation[] = new float[3];
						
						SensorManager.getOrientation(screenR, orientation);
						long azimut = Math.round(Math.toDegrees(orientation[0]) * 2) / 2;
						int calculatedAngle = (int)((azimut + 360) % 360);
						
						synchronized (angleReadings) {
							if (angleReadings.size() == NUM_OF_READINGS_TO_AVERAGE) {
								angleReadings.remove(0);
							}
							angleReadings.add(calculatedAngle);
						}
						
						//long pitch = Math.round(Math.toDegrees(orientation[1]));
						//long roll = Math.round(Math.toDegrees(orientation[2]));
			        }
			    }
			}
		};
		
		sm.registerListener(sl, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
		sm.registerListener(sl, sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
		
		
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
	
	public int getCurrentAngle() {
		synchronized (angleReadings) {
			int sum = 0;
			for (int i = 0; i < angleReadings.size(); i++) {
				sum += angleReadings.get(i);
			}
			return sum / angleReadings.size();
		}
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
