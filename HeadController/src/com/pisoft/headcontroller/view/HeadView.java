package com.pisoft.headcontroller.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class HeadView extends View {
	private Paint paint = new Paint();
	
	private int mouthHoleHeight = 10;
	
	public HeadView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void speak() {
		mouthHoleHeight = mouthHoleHeight == 10 ? 15 : 10;
				
		invalidate();
	}
	
	public void showDefaultLook() {
		mouthHoleHeight = 10;
		invalidate();
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		drawHeadOval(getWidth() - 40, getHeight() - 20, canvas);
        drawEyes(60, 80, canvas);
		drawNose(80, 50, 50, canvas);
		drawMounth(150, 80 - mouthHoleHeight, mouthHoleHeight, canvas);
	}
	
	
	private void drawHeadOval(int width, int height, Canvas canvas) {
		paint.setColor(Color.GREEN);
		paint.setStyle(Style.FILL);

		int widthMidPoint = getWidth() / 2;
		int heightMidPoint = getHeight() / 2;
		
		RectF headOval = new RectF(widthMidPoint - width / 2, heightMidPoint - height / 2, widthMidPoint + width / 2, heightMidPoint + height / 2);
		canvas.drawOval(headOval, paint);
	}
	
	private void drawEyes(int yPosition, int width, Canvas canvas) {
		int midPoint = getWidth() / 2;

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
		
		int midPoint = getWidth() / 2;
		canvas.drawLine(midPoint, yPosition, midPoint - width / 2, yPosition + height, paint);
		canvas.drawLine(midPoint - width / 2, yPosition + height, midPoint + width / 2, yPosition + height, paint);
	}
	
	private void drawMounth(int yPosition, int width, int holeSize, Canvas canvas) {
        paint.setColor(Color.RED);
		
		int midPoint = getWidth() / 2;
		int lipDrop = 5 - holeSize / 5;

		canvas.drawLine(midPoint - width / 2, yPosition, midPoint - width / 3, yPosition + lipDrop, paint);
		canvas.drawLine(midPoint - width / 3, yPosition + lipDrop, midPoint + width / 3, yPosition + lipDrop, paint);
		canvas.drawLine(midPoint + width / 3, yPosition + lipDrop, midPoint + width / 2, yPosition, paint);
		
		canvas.drawLine(midPoint - width / 2, yPosition, midPoint - width / 4, yPosition + lipDrop + holeSize, paint);
		canvas.drawLine(midPoint - width / 4, yPosition + lipDrop + holeSize, midPoint + width / 4, yPosition + lipDrop + holeSize, paint);
		canvas.drawLine(midPoint + width / 4, yPosition + lipDrop + holeSize, midPoint + width / 2, yPosition, paint);
	}

}
