package com.pisoft.headcontroller.head;

import android.content.Intent;
import android.graphics.Bitmap;

import com.pisoft.headcontroller.ControllingActivity;

public class HeadVision {
	private final ControllingActivity activity;
	private final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	private boolean initialized;
	
	public interface OnCompleteListener {
		public void onComplete(String text);
	}

	private interface OnImageOperationListener {
		public void onComplete(Object data);
	}
	
	
	public HeadVision(final ControllingActivity activity) {
		this.activity = activity;
		
		initialized = captureIntent.resolveActivity(activity.getPackageManager()) != null;
	}
	
	public boolean scanPeople(final OnCompleteListener listener) {
		if (!isReady()) {
			listener.onComplete(null);
			
			return false;
		}
		
		takePicture(new OnImageOperationListener() {
			public void onComplete(Object data) {
				Bitmap photo = (Bitmap)data;
				
			}
		});
		

		return true;
	}
	
	public boolean isReady() {
		return initialized;
	}
	

	private void takePicture(final OnImageOperationListener listener) {
		
		activity.requestOperation(captureIntent, new ControllingActivity.OperationListener() {
			public void onFailed() {
			}
			
			public void onComplete(Intent result) {
				Bitmap photo = (Bitmap)result.getExtras().get("data");
				listener.onComplete(photo);
			}
		});
	}
}
