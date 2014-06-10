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

public class HeadVision extends AbstractHeadSense {
	protected Camera camera;
	protected SurfaceView cameraPreviewSurface;
	
	public interface OnCompleteListener {
		public void onComplete(Object data);
	}


	public HeadVision(final ControllingActivity activity) {
		super(activity);
	}
	
	protected void init() {
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
		
		if (camera == null) {
			Log.e("HeadController", "No suitable camera found");
			return;
		}
		
		if (camera.getParameters().getMaxNumDetectedFaces() == 0) {
			Log.e("HeadController", "Camera does not support face recognition");
			return;
		}
		
		camera.setDisplayOrientation(0);
		
		cameraPreviewSurface = (SurfaceView)activity.findViewById(R.id.CameraPreview);
		cameraPreviewSurface.setZOrderOnTop(true);
	    SurfaceHolder holder = cameraPreviewSurface.getHolder();
	    
	    holder.addCallback(new SurfaceHolder.Callback() {
			public void surfaceDestroyed(SurfaceHolder holder) {
//				camera.stopPreview();
			}
			
			public void surfaceCreated(SurfaceHolder holder) {
				try {
					camera.setPreviewDisplay(cameraPreviewSurface.getHolder());
					camera.startPreview();
					markReady();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.e("HeadController", "Failed to initialize a preview surface", e);
				}
			}
			
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			}
		});
	}
	
	protected void destroy() {
		if (camera != null) {
			stopFaceDetection();
			camera.release();
		}
	}

	
	public boolean detectFaces(final OnCompleteListener listener, final boolean continuesDetectionMode) {
		if (!isReady()) {
			return false;
		}

		camera.stopFaceDetection();
		camera.setFaceDetectionListener(new Camera.FaceDetectionListener() {
			public void onFaceDetection(Face[] faces, Camera camera) {
				if (!continuesDetectionMode) {
					camera.stopFaceDetection();
				}
				
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
	
	public void stopFaceDetection() {
		camera.stopFaceDetection();
	}
}
