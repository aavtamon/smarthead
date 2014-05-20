package com.pisoft.headcontroller.head;

import com.pisoft.headcontroller.R;
import com.pisoft.headcontroller.view.HeadView;

import android.app.Activity;
import android.os.Handler;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

public class HeadController {
	private HeadVoice voice;
	private HeadVisual visual;
	
	public HeadController(final Activity activity) {
		voice = new HeadVoice(activity.getApplicationContext());
		visual = new HeadVisual(activity);

		new Handler().postDelayed(new Runnable() {
			public void run() {
				communicate("I can now speak and can do it really really long! I can actually speak forever");
			}
		}, 3000);
	}


	
	
	public void communicate(final String text) {
		voice.say(text, new UtteranceProgressListener() {
			public void onStart(String utteranceId) {
				visual.say(text);
			}
			
			public void onError(String utteranceId) {
				visual.stop();
			}
			
			public void onDone(String utteranceId) {
				visual.stop();
			}
		});
	}
	
	public String listen() {
		return null;
	}
	
}
