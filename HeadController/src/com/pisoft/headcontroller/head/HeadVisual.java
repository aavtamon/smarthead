package com.pisoft.headcontroller.head;

import android.os.Handler;

import com.pisoft.headcontroller.ControllingActivity;
import com.pisoft.headcontroller.R;
import com.pisoft.headcontroller.view.HeadView;

public class HeadVisual extends AbstractHeadSense {
	private final HeadView view;
	
	private boolean isStopped = false;
	
	public HeadVisual(final ControllingActivity activity) {
		super(activity);
		
		this.view = (HeadView)activity.findViewById(R.id.Head);
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

	public boolean say(final String text) {
		isStopped = false;

		animateSpeakAndScheduleNextAnimation();
		
		return true;
	}
	
	public void stop() {
		isStopped = true;

		activity.runOnUiThread(new Runnable() {
			public void run() {
				view.showDefaultLook();
			}
		});
	}
	
	
	private void animateSpeakAndScheduleNextAnimation() {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				view.speak();

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
