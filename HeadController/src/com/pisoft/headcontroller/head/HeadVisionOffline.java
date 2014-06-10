package com.pisoft.headcontroller.head;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
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
		camera.startPreview();
		
		camera.takePicture(null, null, new Camera.PictureCallback() {
			public void onPictureTaken(byte[] data, Camera camera) {
			    BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inPreferredConfig = Bitmap.Config.RGB_565;
				Bitmap img = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
				
 			    FaceDetector fd = new FaceDetector(img.getWidth(), img.getHeight(), 5);
				Face[] faces = new Face[5];
				int numOfFaces = fd.findFaces(img, faces);
				
				List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
				
				for (int i = 0; i < numOfFaces; i++) {
					Face f = faces[i];
					
					if (f.confidence() < 0.3) {
						continue;
					}
					
					Map<String, Object> faceObject = new HashMap<String, Object>();
					
					faceObject.put("eyeDistance", (int)f.eyesDistance());
					
					PointF midPoint = new PointF();
					f.getMidPoint(midPoint);
					faceObject.put("positionX", (int)midPoint.x);
					faceObject.put("positionY", (int)midPoint.y);
					
					result.add(faceObject);
				}

				listener.onComplete(result);
			}
		});
	}
}
