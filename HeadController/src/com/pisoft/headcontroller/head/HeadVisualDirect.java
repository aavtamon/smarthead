package com.pisoft.headcontroller.head;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.pisoft.headcontroller.ControllingActivity;
import com.pisoft.headcontroller.R;

public class HeadVisualDirect extends HeadVisual {
	private SurfaceHolder headHolder;
	
	private Thread animationThread;
	
	private int mouthHoleHeight = 10;
	private Paint paint = new Paint();
	
	public HeadVisualDirect(final ControllingActivity activity) {
		super(activity);
	}

	protected void init() {
		SurfaceView view = (SurfaceView)activity.findViewById(R.id.HeadDirect);
		headHolder = view.getHolder();
		headHolder.setFormat(PixelFormat.TRANSLUCENT);
		view.setZOrderOnTop(true);
		headHolder.addCallback(new SurfaceHolder.Callback() {
			public void surfaceDestroyed(SurfaceHolder holder) {
				markNotReady();
			}
			
			public void surfaceCreated(SurfaceHolder holder) {
				markReady();
				draw();
			}
			
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			}
		});
	}
	
	protected void pause() {
		stop();
	}

	protected void destroy() {
		stop();
	}
	
	public boolean say(final String text) {
		if (!isReady()) {
			return false;
		}
		
		animationThread = new Thread() {
			public void run() {
				while (true) {
					mouthHoleHeight = mouthHoleHeight == 10 ? 15 : 10;
					draw();
					
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						mouthHoleHeight = 10;
						draw();
						return;
					}
				}
			};
		};
		animationThread.start();
		
		return true;
	}
	
	public void stop() {
		if (animationThread != null) {
			animationThread.interrupt();
			animationThread = null;
		}
	}
	
	
	
	public void draw() {
		Canvas canvas = headHolder.lockCanvas();
		
		drawHeadOval(headHolder.getSurfaceFrame().width() - 40, headHolder.getSurfaceFrame().height() - 20, canvas);
        drawEyes(60, 80, canvas);
		drawNose(80, 50, 50, canvas);
		drawMounth(150, 80 - mouthHoleHeight, mouthHoleHeight, canvas);
		
		headHolder.unlockCanvasAndPost(canvas);
	}
	
	private void drawHeadOval(int width, int height, Canvas canvas) {
		paint.setColor(Color.GREEN);
		paint.setStyle(Style.FILL);

		int widthMidPoint = headHolder.getSurfaceFrame().width() / 2;
		int heightMidPoint = headHolder.getSurfaceFrame().height() / 2;
		
		RectF headOval = new RectF(widthMidPoint - width / 2, heightMidPoint - height / 2, widthMidPoint + width / 2, heightMidPoint + height / 2);
		canvas.drawOval(headOval, paint);
	}
	
	private void drawEyes(int yPosition, int width, Canvas canvas) {
		int midPoint = headHolder.getSurfaceFrame().width() / 2;

		drawEye(midPoint - width / 2, yPosition, canvas);
		drawEye(midPoint + width / 2, yPosition, canvas);
	}
	
	private void drawEye(int xPosition, int yPosition, Canvas canvas) {
		paint.setColor(Color.GRAY);
		paint.setStyle(Style.STROKE);
		RectF browOval = new RectF(xPosition - 20, yPosition - 10, xPosition + 20, yPosition + 30);
		canvas.drawArc(browOval, -30, -120, false, paint);
		
		
		paint.setColor(Color.GRAY);
		paint.setStyle(Style.FILL);
		RectF eyeOval = new RectF(xPosition - 10, yPosition, xPosition + 10, yPosition + 20);
		canvas.drawOval(eyeOval, paint);

		paint.setColor(Color.BLACK);
		RectF eyeBall = new RectF(xPosition - 3, yPosition + 7, xPosition + 3, yPosition + 12);
		canvas.drawOval(eyeBall, paint);
    }
	
	private void drawNose(int yPosition, int width, int height, Canvas canvas) {
		paint.setColor(Color.DKGRAY);
		
		int midPoint = headHolder.getSurfaceFrame().width() / 2;
		canvas.drawLine(midPoint, yPosition, midPoint - width / 2, yPosition + height, paint);
		canvas.drawLine(midPoint - width / 2, yPosition + height, midPoint + width / 2, yPosition + height, paint);
	}
	
	private void drawMounth(int yPosition, int width, int holeSize, Canvas canvas) {
        paint.setColor(Color.RED);
		
		int midPoint = headHolder.getSurfaceFrame().width() / 2;
		int lipDrop = 5 - holeSize / 5;

		canvas.drawLine(midPoint - width / 2, yPosition, midPoint - width / 3, yPosition + lipDrop, paint);
		canvas.drawLine(midPoint - width / 3, yPosition + lipDrop, midPoint + width / 3, yPosition + lipDrop, paint);
		canvas.drawLine(midPoint + width / 3, yPosition + lipDrop, midPoint + width / 2, yPosition, paint);
		
		canvas.drawLine(midPoint - width / 2, yPosition, midPoint - width / 4, yPosition + lipDrop + holeSize, paint);
		canvas.drawLine(midPoint - width / 4, yPosition + lipDrop + holeSize, midPoint + width / 4, yPosition + lipDrop + holeSize, paint);
		canvas.drawLine(midPoint + width / 4, yPosition + lipDrop + holeSize, midPoint + width / 2, yPosition, paint);
	}
}
