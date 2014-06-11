package com.pisoft.headcontroller.head;

import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;

import com.pisoft.headcontroller.ControllingActivity;
import com.pisoft.headcontroller.R;
import com.pisoft.headcontroller.view.HeadView;

public class HeadVisual extends AbstractHeadSense {
	protected View view;
	
	private boolean isStopped = false;
	
	public HeadVisual(final ControllingActivity activity) {
		super(activity);

		view = (HeadView)activity.findViewById(R.id.Head);
	}
	
	protected void init() {
		markReady();
	}
	
	protected void pause() {
		stop();
	}

	protected void destroy() {
		stop();
	}
	
	public boolean setBounds(final int x, final int y, final int width, final int height) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				LayoutParams params = (LayoutParams)view.getLayoutParams();
				params.leftMargin = x;
				params.topMargin = y;
				params.width = width;
				params.height = height;
				view.setLayoutParams(params);
			}
		});
		
		return true;
	}

	public boolean say(final String text) {
		isStopped = false;

		animateSpeakAndScheduleNextAnimation();
		
		return true;
	}
	
	public void stop() {
		isStopped = true;

		activity.runOnUiThread(new Runnable() {
			public void run() {
				((HeadView)view).showDefaultLook();
			}
		});
	}
	
	
	private void animateSpeakAndScheduleNextAnimation() {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				((HeadView)view).speak();

				new Handler().postDelayed(new Runnable() {
					public void run() {
						if (!isStopped) {
							animateSpeakAndScheduleNextAnimation();
						}
                    }
				}, 300);
			}
		});
	}
}
