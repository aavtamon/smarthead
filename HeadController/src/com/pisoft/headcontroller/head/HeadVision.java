package com.pisoft.headcontroller.head;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Face;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.pisoft.headcontroller.ControllingActivity;
import com.pisoft.headcontroller.R;

public class HeadVision {
	private Camera camera;
	private SurfaceView cameraPreviewSurface;
	private boolean initialized;
	
	public interface OnCompleteListener {
		public void onComplete(Object data);
	}


	public HeadVision(final ControllingActivity activity) {
		int numOfCams = Camera.getNumberOfCameras();
		for (int i = 0; i < numOfCams; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			
			if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
				try {
					camera = Camera.open(i);
				} catch (Exception e) {
					Log.e("HeadController", "Failed to initialize a camera", e);
				}
				break;
			}
		}
		
		cameraPreviewSurface = (SurfaceView)activity.findViewById(R.id.CameraPreview);
	    SurfaceHolder holder = cameraPreviewSurface.getHolder();
	    holder.addCallback(new SurfaceHolder.Callback() {
			public void surfaceDestroyed(SurfaceHolder holder) {
				camera.stopPreview();
			}
			
			public void surfaceCreated(SurfaceHolder holder) {
				try {
					camera.setPreviewDisplay(cameraPreviewSurface.getHolder());
					camera.startPreview();
					initialized = true;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.e("HeadController", "Failed to initialize a preview surface", e);
				}
			}
			
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			}
		});

		
		
		
	}
	
	public boolean detectFaces(final OnCompleteListener listener) {
		if (!isReady()) {
			return false;
		}

		camera.setFaceDetectionListener(new Camera.FaceDetectionListener() {
			public void onFaceDetection(Face[] faces, Camera camera) {
				camera.stopFaceDetection();
				
				List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
				
				for (int i = 0; i < faces.length; i++) {
					Face f = faces[i];
					Map<String, Object> faceObject = new HashMap<String, Object>();
					
					faceObject.put("id", f.id);
					faceObject.put("bounds", f.rect.toShortString());
					
					result.add(faceObject);
				}
				
				listener.onComplete(result);
			}
		});
		
		camera.startFaceDetection();
		
		return true;
	}
	
	public boolean isReady() {
		return initialized;
	}
}
