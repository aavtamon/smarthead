package com.pisoft.headcontroller.head;

import android.app.Activity;
import android.os.Handler;

import com.pisoft.headcontroller.R;
import com.pisoft.headcontroller.view.HeadView;

public class HeadVisual {
	private final Activity activity;
	private final HeadView view;
	
	private boolean isStopped = false;
	
	public HeadVisual(final Activity activity) {
		this.activity = activity;
		this.view = (HeadView)activity.findViewById(R.id.Head);
	}
	
	public void say(final String text) {
		isStopped = false;

		animateSpeakAndScheduleNextAnimation();
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
