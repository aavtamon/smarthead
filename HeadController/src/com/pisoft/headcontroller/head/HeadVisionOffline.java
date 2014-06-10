package com.pisoft.headcontroller.head;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.pisoft.headcontroller.ControllingActivity;
import com.pisoft.headcontroller.R;

public class HeadVisionOffline extends AbstractHeadSense {
	private Camera camera;
	private SurfaceView cameraPreviewSurface;
	private SurfaceHolder holder; 
	
	public interface OnCompleteListener {
		public void onComplete(Object data);
	}


	public HeadVisionOffline(final ControllingActivity activity) {
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
		
		cameraPreviewSurface = (SurfaceView)activity.findViewById(R.id.CameraPreview);
		cameraPreviewSurface.setZOrderOnTop(true);
	    holder = cameraPreviewSurface.getHolder();
	    
	    holder.addCallback(new SurfaceHolder.Callback() {
			public void surfaceDestroyed(SurfaceHolder holder) {
				camera.stopPreview();
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
			camera.release();
		}
	}

	
	public boolean detectFaces(final OnCompleteListener listener, final boolean continuesDetectionMode) {
		if (!isReady()) {
			return false;
		}
		
		
		camera.takePicture(null, null, new Camera.PictureCallback() {
			public void onPictureTaken(byte[] data, Camera camera) {
				camera.stopPreview();
			    BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inPreferredConfig = Bitmap.Config.RGB_565;
				Bitmap img = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
				
 			    FaceDetector fd = new FaceDetector(img.getWidth(), img.getHeight(), 1);
				Face[] faces = new Face[1];
				int numOfFaces = fd.findFaces(img, faces);
				
				try {
					camera.setPreviewDisplay(null);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Canvas canvas = holder.lockCanvas();
				canvas.drawBitmap(img, 0, 0, new Paint());
				holder.unlockCanvasAndPost(canvas);
				
				listener.onComplete("Number of found faces = " + numOfFaces);
			}
		});

		return true;
	}
}
