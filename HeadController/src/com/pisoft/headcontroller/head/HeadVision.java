package com.pisoft.headcontroller.head;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Face;
import android.util.Log;
import android.view.SurfaceView;

import com.pisoft.headcontroller.ControllingActivity;
import com.pisoft.headcontroller.R;

public class HeadVision {
	private final ControllingActivity activity;
	private Camera camera;
	private SurfaceView cameraPreviewSurface;
	
	public interface OnCompleteListener {
		public void onComplete(Object data);
	}


	public HeadVision(final ControllingActivity activity) {
		this.activity = activity;
		
		cameraPreviewSurface = (SurfaceView)activity.findViewById(R.id.CameraPreview);
		
		int numOfCams = Camera.getNumberOfCameras();
		for (int i = 0; i < numOfCams; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			
			if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
				try {
					camera = Camera.open(i);
					camera.setPreviewDisplay(cameraPreviewSurface.getHolder());
					
					camera.startPreview();
				} catch (Exception e) {
					Log.e("HeadController", "Failed to initialize a camera", e);
				}
				break;
			}
		}
		
		
	}
	
	public boolean detectFaces(final OnCompleteListener listener) {
		if (!isReady()) {
			return false;
		}

		camera.setFaceDetectionListener(new Camera.FaceDetectionListener() {
			public void onFaceDetection(Face[] faces, Camera camera) {
				camera.stopFaceDetection();
				
				List<Map> result = new ArrayList<Map>();
				
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
		return camera != null;
	}
}
