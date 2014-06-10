package com.pisoft.headcontroller.head;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.os.Handler;

import com.pisoft.headcontroller.ControllingActivity;

public class HeadVisionOffline extends HeadVision {
	private boolean isStopped;
	
	public HeadVisionOffline(final ControllingActivity activity) {
		super(activity);
	}
	
	public boolean detectFaces(final OnCompleteListener listener, final boolean continuesDetectionMode) {
		if (!isReady()) {
			return false;
		}
		
		isStopped = false;
		
		if (continuesDetectionMode) {
			takePictureAndScheduleNextTake(listener);
		} else {
			takePicture(listener);
		}

		return true;
	}
	
	
	public void stopFaceDetection() {
		isStopped = true;
	}
	
	private void takePictureAndScheduleNextTake(final OnCompleteListener listener) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				if (isStopped) {
					return;
				}
				
				takePicture(listener);

				new Handler().postDelayed(new Runnable() {
					public void run() {
						if (!isStopped) {
							takePictureAndScheduleNextTake(listener);
						}
                    }
				}, 1000);
			}
		});
	}
	
	private void takePicture(final OnCompleteListener listener) {
		camera.takePicture(null, null, new Camera.PictureCallback() {
			public void onPictureTaken(byte[] data, Camera camera) {
			    BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inPreferredConfig = Bitmap.Config.RGB_565;
				Bitmap img = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
				
 			    FaceDetector fd = new FaceDetector(img.getWidth(), img.getHeight(), 5);
				Face[] faces = new Face[5];
				int numOfFaces = fd.findFaces(img, faces);
				
				listener.onComplete("Number of found faces = " + numOfFaces);
				
				camera.startPreview();
			}
		});
	}
}
